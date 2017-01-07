package entities.car;

import entities.Intersection;
import entities.Lane;
import entities.RoadNetwork;
import entities.traffic_light.TrafficLight;
import entities.traffic_light.TrafficLightState;
import entities.zone.Zone;
import graph_network.DijkstraAlgorithm;
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
            source.addDismissedCar(this);
        }
    }

    private void drive() {
        if (currentEvent != null && !currentEvent.wasProcessed()) {
            Logger.getInstance().logDebug(getName(), simEngine.getCurrentSimTime(), "there was a previous event not processed");
            Duration sinceLastPostedEvent = Duration.between(currentEvent.getPostedTime(), simEngine.getCurrentSimTime());
            double elapsed = sinceLastPostedEvent.getSeconds() + sinceLastPostedEvent.getNano() * 1e-9;
            double distance = Math.round(elapsed * speed);
            addTravel(distance);
        }
        speed = currentLane.getSpeed_limit();
        destinationInLane = currentLane.getFreeSpotPositionForCar(this);
        Duration timeToArrive = calculateTravelTime(positionInLane, destinationInLane);
        currentEvent = new ExpectedStopEvent(this, simEngine.getCurrentSimTime().plus(timeToArrive));
        simEngine.addEvent(currentEvent);
        setCarState(CarState.DRIVING);
    }

    public void stop() {
        speed = 0;
        setCarState(CarState.STOPPED);
    }


    public void update() {
        if (positionInLane == currentLane.getLength()) {
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "arrived at end of lane -> do next step");
            nextStep();
        } else if (positionInLane == currentLane.getFreeSpotPositionForCar(this)) {
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "arrived at free spot -> stop");
            Logger.getInstance().logDebug(getName(), simEngine.getCurrentSimTime(), "number of car in lane (including me): " + String.valueOf(currentLane.getCarQueue().size()));
            stop();
        } else {
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "car not arrived yet, updating ");
            // Let's correct it anyway, by driving to the real free spot
            drive();
        }
    }

    /**
     * Next is a method that defines what a car does when it is at the end of one lane
     */
    private void nextStep() {
        Node nextStep = currentLane.getDestination();
        if (nextStep instanceof Intersection) {
            // TODO: 07/01/2017 after we can create a method trafficSign.isOpen()
            /*
            * **********************************************************************************************************
            * 1. Check TrafficLight status
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
                stop();
                ((Zone) nextStep).addNewArrivedCar(this);
                setCarState(CarState.ARRIVED);
            }
        } else {
            Logger.getInstance().logWarning(getName(), simEngine.getCurrentSimTime(), "NextStep is nor an Intersection nor a Zone");
        }
    }

    private void changeLane(Lane nextLane) {
        // notify the previous car on the current lane before changing
        if (currentLane.getNextCar(this) != null) {
            Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "Updating next car");
            currentLane.getNextCar(this).update();
        }
        currentLane.removeCar(this);
        nextLane.addCar(this);
        currentLane = nextLane;
        Logger.getInstance().logInfo(getName(), simEngine.getCurrentSimTime(), "Changing Lane");
        positionInLane = 0;
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
}
