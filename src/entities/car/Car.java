package entities.car;

import entities.Intersection;
import entities.Lane;
import entities.RoadNetwork;
import entities.traffic_light.TrafficLight;
import entities.traffic_light.TrafficLightState;
import entities.zone.Zone;
import graph_network.DijkstraAlgorithm;
import graph_network.Edge;
import graph_network.Node;
import logging.Logger;
import simulation.Entity;
import simulation.SimEngine;

import java.time.Duration;
import java.util.LinkedList;

// TODO: 05/01/2017 write methods documentation

/**
 * This class models a car that moves in a roadNetwork
 */
public class Car implements Entity {

    public static final int length = 4;
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
    }

    /*
    * ##############################################################################################################
    * Behaviors
    * ##############################################################################################################
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
    private void drive() {
        // update the position using the current event posted time and current speed
        if (currentEvent != null && !currentEvent.wasProcessed()) {
            Logger.getInstance().logDebug(getName(), simEngine.getCurrentSimTime(), "there was a previous event not processed");
            Duration sinceLastPostedEvent = Duration.between(currentEvent.getPostedTime(), simEngine.getCurrentSimTime());
            double elapsed = sinceLastPostedEvent.getSeconds() + sinceLastPostedEvent.getNano() * 1e-9;
            double distance = Math.round(elapsed * speed);
            addTravel(distance);
            simEngine.removeEvent(currentEvent);
        }
        // then we can calculate the next period of driving
        speed = currentLane.getSpeed_limit();
        destinationInLane = currentLane.getFreeSpotPositionForCar(this);
        Duration timeToArrive = calculateTravelTime(positionInLane, destinationInLane);
        Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "set destination: " + destinationInLane + " on lane " + currentLane.getId());
        currentEvent = new ExpectedStopEvent(this, simEngine.getCurrentSimTime().plus(timeToArrive));
        simEngine.addEvent(currentEvent);
        setCarState(CarState.DRIVING);
    }

    public void stop() {
        speed = 0;
        setCarState(CarState.STOPPED);
    }

    public void update() {
        // if car was stopped before updating, and this car is the last one (and there is other cars) and one place just
        // got freed up
        if (isStopped() && currentLane.isLastCar(this) && currentLane.getCarQueue().size() == currentLane.maxQueue() - 1) {
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "I was the last car blocking the lane");
            for (Edge connection : roadNetwork.getConnectionsThatArriveAt(currentLane.getSource())) {
                if (!((Lane) connection).getCarQueue().isEmpty()) {
                    Car waitingCar = ((Lane) connection).getCarQueue().get(0);
                    if (waitingCar.isLaneNextStep(currentLane)) waitingCar.update();
                }
                // FIXME: 10/01/2017 only select cars which next step is the current lane
            }
        }
        if (positionInLane == currentLane.getLength()) {
            // do next step of the path if we arrived at the end of the current lane
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "i am at end of lane -> do next step");
            nextStep();
        } else if (positionInLane == currentLane.getFreeSpotPositionForCar(this)) {
            // if arrived at the free spot then stop
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "arrived at free spot -> stop");
            Logger.getInstance().logDebug(getName(), simEngine.getCurrentSimTime(), "number of car in lane (including me): " + String.valueOf(currentLane.getCarQueue().size()));
            stop();
        } else {
            // Else we must update the destination and the driving period
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "updating new destination: " + currentLane.getFreeSpotPositionForCar(this));
            drive();
        }
    }

    /**
     * Next is a method that defines what a car does when it is at the end of one lane
     */
    private void nextStep() {
        Node nextStep = currentLane.getDestination();
        /*
        * ##############################################################################################################
        * The next step might be an intersection, or a zone
        * ##############################################################################################################
        */
        
        if (nextStep instanceof Intersection) {
            // TODO: 07/01/2017 after we can create a method trafficSign.isOpen()
            /*
            * **********************************************************************************************************
            * 1. Check TrafficLight status
            *
            * if green go to next section
            * if red stop and return
            */

            if (currentLane.getTrafficSign() != null &&
                    currentLane.getTrafficSign() instanceof TrafficLight &&
                    ((TrafficLight) currentLane.getTrafficSign()).getState() == TrafficLightState.RED) {
                stop();
                Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "Stopping at RedLight");
                return;
            }
            /*
            * **********************************************************************************************************
            * 2. Try to go to next lane
            *
            * if has space change lane and drive
            * else wait on lane
            */

            int indexOfNextNodeInPath = path.indexOf(currentLane.getDestination()) + 1;
            Lane nextLane = roadNetwork.getLaneBetween(currentLane.getDestination(), path.get(indexOfNextNodeInPath));
            if (nextLane.hasSpace()) {
                changeLane(nextLane);
                drive();
            } else {
                Intersection currentIntersection = (Intersection) currentLane.getDestination();
                String msg = "Next lane is full, waiting at intersection " + currentIntersection.getName();
                Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), msg);
                stop();
            }
        } else if (nextStep instanceof Zone) {
            // this should mean the car arrived, we'll check anyway
            if (nextStep == destination) {
                Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "I arrived at " + nextStep.getId());
                currentLane.removeCar(this);
                updateNextCar();
                stop();
                ((Zone) nextStep).addNewArrivedCar(this);
                setCarState(CarState.ARRIVED);
            }
        } else {
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "NextStep is nor an Intersection nor a Zone");
        }
    }

    private void changeLane(Lane nextLane) {
        currentLane.removeCar(this);
        // notify the previous car on the current lane before changing
        updateNextCar();
        nextLane.addCar(this);
        currentLane = nextLane;
        Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "Changing Lane");
        positionInLane = 0;
    }

    private void updateNextCar() {
        Car nextCar = currentLane.getNextCar(this);
        if (nextCar != null) {
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "Updating next car: " + nextCar.getName());
            if (nextCar.isStopped()) {
                simEngine.addEvent(new DelayedReactionEvent(nextCar, Duration.ofSeconds(2)));
            } else if (nextCar.isDriving()) {
                nextCar.update();
            }
        }
    }

    public boolean isLaneNextStep(Lane lane) {
        int indexOfNextNodeInPath = path.indexOf(currentLane.getDestination()) + 1;
        Lane nextLane = roadNetwork.getLaneBetween(currentLane.getDestination(), path.get(indexOfNextNodeInPath));
        return nextLane == lane;
    }

    private Duration calculateTravelTime(double position, double destination) {
        double distance = destination - position;
        double time = distance / speed;
        int timeSec = (int) time;
        long timeNano = (long) ((time - timeSec) * 1e9);
        return Duration.ofSeconds(timeSec, timeNano);
    }

    public void addTravel(double distanceTraveled) {
        positionInLane += distanceTraveled;
        Logger.getInstance().logDebug(getName(), simEngine.getCurrentSimTime(), "position in lane: " + positionInLane);
        // update total travelled distance
        totalTravelledDistance += distanceTraveled;
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

    private boolean isDriving() {
        return carState == CarState.DRIVING;
    }

    private boolean isStopped() {
        return carState == CarState.STOPPED;
    }

    public double getSpeed() {
        return speed;
    }

    public CarState getCarState() {
        return carState;
    }

    public void setCarState(CarState newCarState) {
        if (newCarState == carState)
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "Changing state to the same: " + carState);
        carState = newCarState;
    }

    public double getTotalTravelledDistance() {
        return totalTravelledDistance;
    }

    public Zone getSource() {
        return source;
    }

    public Zone getDestination() {
        return destination;
    }
}
