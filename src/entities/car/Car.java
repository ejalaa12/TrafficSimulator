package entities.car;

import entities.RoadNetwork;
import entities.intersection.Intersection;
import entities.lane.Lane;
import entities.zone.Zone;
import graph_network.DijkstraAlgorithm;
import graph_network.Node;
import logging.Logger;
import simulation.Entity;
import simulation.SimEngine;

import java.time.Duration;
import java.util.LinkedList;

/**
 * This class models a car that moves in a roadNetwork
 */
public class Car implements Entity {

    public static final int length = 4;
    public CarStats stats;
    private Zone source, destination;
    private double speed;
    private String Id;
    private CarState carState;
    // Simulator Engine
    private SimEngine simEngine;
    // A car has a knowledge of the roadNetwork
    private RoadNetwork roadNetwork;
    private LinkedList<Node> path;
    // positionInLane in network
    private Lane currentLane;
    private int destinationInLane; // destination in currentLane
    private double totalTravelledDistance;
    private double positionInLane;


    // keeping track currentEvent so we can remove it from simEngine
    private CarEvent currentEvent;

    public Car(String Id, Zone source, Zone destination, RoadNetwork roadNetwork, SimEngine simEngine) {
        setCarState(CarState.CREATED);
        stats = new CarStats(this);
        stats.creation = simEngine.getCurrentSimTime();
        this.Id = Id;
        this.source = source;
        this.destination = destination;
        speed = 0;
        this.simEngine = simEngine;
        this.roadNetwork = roadNetwork;
        // Calculate path
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(roadNetwork);
        dijkstraAlgorithm.execute(source);
        path = dijkstraAlgorithm.getPath(destination);
        if (path == null) {
            throw new IllegalStateException("Car didn't find path from " + source.getName() + " to " + destination.getName());
        }
    }

    /*
    * ##############################################################################################################
    * Behaviors
    * ##############################################################################################################
    */

    /**
     * When initializing a Car we need to put it on the first lane if possible and start driving
     */
    @Override
    public void init() {
        Lane firstLane = roadNetwork.getLaneBetween(path.get(0), path.get(1));
        if (firstLane.hasSpace()) {
            firstLane.addCar(this);
            currentLane = firstLane;
            positionInLane = 0;
            speed = 0;
            drive();
        } else {
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "I was dismissed");
            source.addDismissedCar(this);
        }
    }

    /**
     * This method force a car to drive to the next free spot.
     * It creates an event when the car is supposed to stop.
     */
    public void drive() {
        /*
        * **************************************************************************************************************
        * If a previous event was posted but not processed then we update the position
        * Otherwise the position was updated when the event was processed
        */
        
        // update the position using the current event posted time and current speed
        if (currentEvent != null && !currentEvent.wasProcessed()) {
            Logger.getInstance().logDebug(getName(), "there was a previous event not processed");
            Duration sinceLastPostedEvent = Duration.between(currentEvent.getPostedTime(), simEngine.getCurrentSimTime());
            double elapsed = sinceLastPostedEvent.getSeconds() + sinceLastPostedEvent.getNano() * 1e-9;
            double distance = Math.round(elapsed * speed);
            addTravel(distance);
            simEngine.removeEvent(currentEvent);
        }
        /*
        * **************************************************************************************************************
        * Then we calculate the next period of driving
        */
        // Set speed
        speed = currentLane.getSpeed_limit();
        // Set Destination
        destinationInLane = currentLane.getFreeSpotPositionForCar(this);
        // Calculate time to arrive
        Duration timeToArrive = calculateTravelTime(positionInLane, destinationInLane);
        Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "set destination: " + destinationInLane + " on lane " + currentLane.getId());
        // Use it to create event when the car is supposed to arrive
        currentEvent = new ExpectedStopEvent(this, simEngine.getCurrentSimTime().plus(timeToArrive));
        simEngine.addEvent(currentEvent);
        // Set the car state to driving
        setCarState(CarState.DRIVING);
    }

    /**
     * Stops the car by setting the speed to 0 and the car state to STOPPED
     * <p>
     * Stop happen when:
     * 1. car is updated and car has arrived at this destination
     * 2. car has arrived at the end of the lane and the traffic light is red (in next step)
     * 3. car has arrived at an intersection but the next lane is full (in next step)
     * 4. car has arrived at the final destination (in next step)
     * <p>
     * Which means that in any case a stop is called from update method. Which happens only when an event occur.
     * And, when an event occur the position is updated
     */
    public void stop() {
        // Set speed to zero
        speed = 0;
        setCarState(CarState.STOPPED);
    }

    /**
     * This method is called by:
     * 1. Another Car
     * 2. An intersection
     * 3. When a stop is expected
     */
    public void update() {
        // if car was stopped before updating, and this car is the last one (and there is other cars) and one place just
        // got freed up
        if (isStopped() && currentLane.isLastCar(this) && currentLane.getCarQueue().size() == currentLane.maxQueue() - 1) {
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "I was the last car blocking the entry to this lane");
            Node source = currentLane.getSource();
            if (source instanceof Intersection) {
                ((Intersection) source).notifyCarsWithNextLane(currentLane);
            } else {
                // in this case source is the origin of the car (a Zone) and we don't need to notify
            }
        }
        if (positionInLane == currentLane.getLength()) {
            // do next step of the path if we arrived at the end of the current lane
            Logger.getInstance().logInfo(getName(), "i am at end of lane -> do next step");
            nextStep();
        } else if (positionInLane == currentLane.getFreeSpotPositionForCar(this)) {
            // if arrived at the free spot then stop and wait for an event
            Logger.getInstance().logInfo(getName(), "arrived at free spot -> stop");
            Logger.getInstance().logDebug(getName(), "number of car in lane (including me): " + String.valueOf(currentLane.getCarQueue().size()));
            stop();
        } else {
            // Else we must update the destination and the driving period
            Logger.getInstance().logInfo(getName(), "updating new destination: " + currentLane.getFreeSpotPositionForCar(this));
            drive();
        }
    }

    /**
     * Next is a method that defines what a car does when it is at the end of one lane
     */
    private void nextStep() {
        Node nextStep = currentLane.getDestination();
        if (nextStep instanceof Intersection) {
            ((Intersection) nextStep).registerCar(this, getNextLane());
        } else if (nextStep instanceof Zone) {
            ((Zone) nextStep).addNewArrivedCar(this);
            stats.arrival = simEngine.getCurrentSimTime();
            stats.logFinish();
        } else {
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "NextStep is nor an Intersection nor a Zone");
        }
    }

    /**
     * This method moves a car inside an intersection
     *
     * @param intersection the intersection to get into
     */
    public void getIntoIntersection(Intersection intersection) {
        currentLane.removeCar(this);
        intersection.addCarInsideIntersection(this);
        currentLane = null;
    }

    /**
     * Exit from an intersection to the next lane
     *
     * @param intersection the intersection where the car was
     * @param exitLane     the lane where the car is going
     */
    public void getOutOfIntersection(Intersection intersection, Lane exitLane) {
        intersection.removeCarFromInsideIntersection(this);
        exitLane.addCar(this);
        currentLane = exitLane;
        positionInLane = 0;
        drive();
    }

    /**
     * check if the lane given in paramter is the next step of the car
     * @param lane the lane to check
     * @return true if this lane is the next step of this car
     */
    public boolean isLaneNextStep(Lane lane) {
        return getNextLane() == lane;
    }

    /**
     * Calculate the time to travel from one position on a lane to another
     * @param position the start position
     * @param destination the destination position
     * @return the duration to travel from position to destination
     */
    private Duration calculateTravelTime(double position, double destination) {
        double distance = destination - position;
        double time = distance / speed;
        int timeSec = (int) time;
        long timeNano = (long) ((time - timeSec) * 1e9);
        return Duration.ofSeconds(timeSec, timeNano);
    }

    /**
     * Adds the distance travelled to its position and to the total distance travelled
     *
     * @param distanceTravelled the distance to add
     */
    public void addTravel(double distanceTravelled) {
        positionInLane += distanceTravelled;
        Logger.getInstance().logDebug(getName(), simEngine.getCurrentSimTime(), "position in lane: " + positionInLane);
        // update total travelled distance
        totalTravelledDistance += distanceTravelled;
    }

    /*
    * ##############################################################################################################
    * Entity methods
    * ##############################################################################################################
    */

    @Override
    public void logStats() {

    }

    @Override
    public String getName() {
        return Id;
    }

    @Override
    public SimEngine getSimEngine() {
        return simEngine;
    }

    /*
    * ##############################################################################################################
    * Getter and Setters
    * ##############################################################################################################
    */

    /**
     * Returns the next lane in the path to the destination
     * @return the next lane in the path to the destination
     */
    public Lane getNextLane() {
        int indexOfNextNodeInPath = path.indexOf(currentLane.getDestination()) + 1;
        return roadNetwork.getLaneBetween(currentLane.getDestination(), path.get(indexOfNextNodeInPath));
    }

    /**
     *
     * @return true if the car is driving
     */
    public boolean isDriving() {
        return carState == CarState.DRIVING;
    }

    /**
     *
     * @return true if the car is stopped
     */
    public boolean isStopped() {
        return carState == CarState.STOPPED;
    }

    /**
     *
     * @return the current speed of the car
     */
    public double getSpeed() {
        return speed;
    }

    /**
     *
     * @return current car state
     */
    public CarState getCarState() {
        return carState;
    }

    /**
     * Update the car state
     * @param newCarState the new car state
     */
    public void setCarState(CarState newCarState) {
        if (carState == CarState.DRIVING && newCarState == CarState.STOPPED) stats.addNewStopTime();
        else if (carState == CarState.STOPPED && newCarState == CarState.DRIVING) stats.addNewRestartTime();
        carState = newCarState;
//        if (currentLane != null)
//            LaneStats.log(currentLane);
    }

    /**
     *
     * @return the total distance travelled by the car
     */
    public double getTotalTravelledDistance() {
        return totalTravelledDistance;
    }

    /**
     *
     * @return the zone where the car was created
     */
    public Zone getSource() {
        return source;
    }

    /**
     *
     * @return the zone where the car wants to go
     */
    public Zone getDestination() {
        return destination;
    }

    /**
     *
     * @return the lane on which car currently is
     */
    public Lane getCurrentLane() {
        return currentLane;
    }

    /**
     * Update the current lane of the car
     * @param currentLane the lane where the car is now
     */
    public void setCurrentLane(Lane currentLane) {
        this.currentLane = currentLane;
    }

    /**
     *
     * @return a list of nodes describing the path from the source node to the destination
     */
    public LinkedList<Node> getPath() {
        return path;
    }

    /**
     *
     * @return the road network where the car is evolving
     */
    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }

    @Override
    public String toString() {
        return getName();
    }
}
