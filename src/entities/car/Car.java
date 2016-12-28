package entities.car;

import entities.Intersection;
import entities.Lane;
import entities.RoadNetwork;
import entities.zone.Zone;
import graph_network.DijkstraAlgorithm;
import graph_network.Node;
import logging.LogLevel;
import logging.Logger;
import simulation.Entity;
import simulation.Event;
import simulation.SimEngine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;

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
    // position in network
    private Lane currentLane;
    private int step;
    // position in lane
    private double position;

    // keeping track currentEvent so we can remove it from simEngine
    private Event currentEvent;


    public Car(String Id, Zone source, Zone destination, RoadNetwork roadNetwork, SimEngine simEngine) {
        carState = CarState.CREATED;
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
        // Position in network
        step = 0;
    }

    /*
    * ****************************************************************************************************************
    * Behaviors
    * ****************************************************************************************************************
    */

    @Override
    public void init() {
        goToNextLane();
    }

    private LocalDateTime driveTo(int destination) {
        updatePosition();
        speed = currentLane.getSpeed_limit();
        carState = CarState.DRIVING;
        double drivingDistance = destination - position;
        // To avoid the rounding problem that leads to incorrect position and durations
        double time = drivingDistance / speed;
        int timeSec = (int) time;
        long timeNano = (long) ((time - timeSec) * 1e9);
        Duration timeToArrive = Duration.ofSeconds(timeSec, timeNano);
        return simEngine.getCurrentSimTime().plus(timeToArrive);
    }

    public void driveToFreeSpot() {
        LocalDateTime nextStopTime = driveTo(currentLane.getFreeSpotPosition());
        currentEvent = new StopEvent(this, nextStopTime);
        simEngine.addEvent(currentEvent);

        // Log info
        String msg = "Driving to free spot: " + currentLane.getFreeSpotPosition();
        msg += " on lane " + currentLane.getId();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    public void driveToEnd() {
        LocalDateTime nextChangeLaneTime = driveTo(currentLane.getLength());
        currentEvent = new ChangingLaneEvent(this, nextChangeLaneTime);
        simEngine.addEvent(currentEvent);

        // Log info
        String msg = "Driving to end of lane " + currentLane.getId();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    /**
     * While car has not reached path.last()
     * if possible (next lane is not full)
     * >>>>> car is added to the next lane queue
     * else:
     * >>>>> the car is dismissed (if it just started) or waits
     */
    public void goToNextLane() {
        if (step < path.size() - 1) {
            Lane nextLane = roadNetwork.getLaneBetween(path.get(step), path.get(step + 1));
            if (nextLane.isFree()) {
                switchToLane(nextLane);
                driveToEnd();
            } else if (nextLane.hasSpace()) {
                switchToLane(nextLane);
                driveToFreeSpot();
            } else {
                if (currentLane == null) {
                    carDismissed();
                } else {
                    waitBecauseNextLaneFull();
                }
            }
        } else {
            arrived();
        }
    }


    private void updatePosition() {
        // for right after initialisation
        if (currentEvent == null) {
            position = 0;
        } else {
            Duration sinceLastInstruction = Duration.between(currentEvent.getPostedTime(), simEngine.getCurrentSimTime());
            double elapsed = sinceLastInstruction.getSeconds() + sinceLastInstruction.getNano() * 1e-9;
            position += Math.round(elapsed * speed);
        }
    }

    /*
    * ****************************************************************************************************************
    * State changing
    * ****************************************************************************************************************
    */

    public void notifyCar(CarNotification notification) {
        String msg = "update: " + notification;
        switch (notification) {
            case GoToEndOfLane:
                Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
                // unpost current event
                simEngine.removeEvent(currentEvent);
                driveToEnd();
                break;
            case GoToNextFreeSpot:
                Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
                // unpost current event
                simEngine.removeEvent(currentEvent);
                driveToFreeSpot();
                break;
        }
    }

    private void switchToLane(Lane nextLane) {
        if (currentLane != null) // for first time
            currentLane.removeCar(this);
        nextLane.addCar(this);
        currentLane = nextLane;
        step += 1;
        // Because the car has just arrived on the lane
        position = 0;
        speed = 0;
        // Log info
        String msg = "Arrived on lane: " + currentLane.getId();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    private void waitBecauseNextLaneFull() {
        stop();
        Intersection currentIntersection = (Intersection) path.get(step + 1);
        String msg = "Waiting at intersection " + currentIntersection.getName();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    public void stop() {
        updatePosition();
        speed = 0;
        carState = CarState.STOPPED;
        // Log info
        String msg = "Stop: " + position;
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    private void arrived() {
        carState = CarState.ARRIVED;
        if (path.getLast() instanceof Zone) {
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "Car arrived", LogLevel.INFO);
        } else {
            String msg = "Finished path but last node is not Zone...";
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.WARNING);
        }
        // removing car from the lane
        // commenting this line can be interesting (just to play with git)
        currentLane.removeCar(this);
        // TODO: 28/12/2016 add arrived car to the count of arrived cars
//        throw new NotImplementedException();
    }

    private void carDismissed() {
        // TODO: 27/12/2016 remove it from any list that contains it and do some stats
        throw new NotImplementedException();
    }

    /*
    * ****************************************************************************************************************
    * getter and setters
    * ****************************************************************************************************************
    */

    @Override
    public void printStats() {

    }

    @Override
    public String getName() {
        return Id;
    }

    public int getStep() {
        return step;
    }

    public LinkedList<Node> getPath() {
        return path;
    }

    public Lane getCurrentLane() {
        return currentLane;
    }
}
