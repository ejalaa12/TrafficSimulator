package entities.car;

import entities.Intersection;
import entities.Lane;
import entities.RoadNetwork;
import entities.traffic_light.TrafficLight;
import entities.traffic_light.TrafficLightState;
import entities.traffic_light.TrafficSign;
import entities.zone.Zone;
import graph_network.DijkstraAlgorithm;
import graph_network.Node;
import logging.LogLevel;
import logging.Logger;
import simulation.Entity;
import simulation.Event;
import simulation.SimEngine;

import java.time.Duration;
import java.time.LocalDateTime;
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
        LocalDateTime nextStopTime = driveTo(currentLane.getFreeSpotPositionForCar(this));
        currentEvent = new StopEvent(this, nextStopTime);
        simEngine.addEvent(currentEvent);

        // Log info
        String msg = "Driving to free spot: " + currentLane.getFreeSpotPositionForCar(this);
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
     * if possible (next lane is not full and light is green)
     * >>>>> car is added to the next lane queue
     * else:
     * >>>>> the car is dismissed (if it just started) or waits
     */
    public void goToNextLane() {
        if (step < path.size() - 1) {
            if (currentLane != null) {
                TrafficSign currentLaneTrafficSign = currentLane.getTrafficSign();
                if (currentLaneTrafficSign != null && currentLaneTrafficSign instanceof TrafficLight) {
                    if (((TrafficLight) currentLaneTrafficSign).getState() == TrafficLightState.RED) {
                        waitBecauseRedLight();
                        return;
                    }
                }
            }
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

    /**
     * Give the car a notification and the car behaves accordingly
     * 1. Log notification
     * 2. Un-post next event, if any (if the car has stopped its waiting for an event, so no next event)
     * 3. switch case:
     *  + if @GoToEndOfLane: @driveToEnd
     *  + if @GoToNextFreeSpot: @driveToFreeSpot
     *  + if @GreenLightSoChangeLane: @goToNextLane
     * @param notification
     */
    public void notifyCar(CarNotification notification) {
        String msg = "update: " + notification;
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
        if (currentEvent != null) {
            simEngine.removeEvent(currentEvent);
        }
        switch (notification) {
            case GoToEndOfLane:
                driveToEnd();
                break;
            case GoToNextFreeSpot:
                driveToFreeSpot();
                break;
            case GreenLightSoChangeLane:
                goToNextLane();
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
        Intersection currentIntersection = (Intersection) path.get(step);
        String msg = "Waiting at intersection " + currentIntersection.getName();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    private void waitBecauseRedLight() {
        stop();
        Intersection currentIntersection = (Intersection) path.get(step);
        String msg = "Waiting at red light " + currentIntersection.getName();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    public void stop() {
        updatePosition();
        speed = 0;
        carState = CarState.STOPPED;
        // now that the car is stopped, it should not have any planned next event so:
        currentEvent = null;
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
        logStats();
        // removing car from the lane
        // TODO: 28/12/2016 TRY without this line so that cars accumulate and we can see if algorithm works
        currentLane.removeCar(this);
        // TODO: 28/12/2016 add arrived car to the count of arrived cars
//        throw new NotImplementedException();
    }

    private void carDismissed() {
        // TODO: 27/12/2016 remove it from any list that contains it and do some stats
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "Car dismissed but not implemented", LogLevel.WARNING);
//        throw new NotImplementedException();
    }

    /*
    * ****************************************************************************************************************
    * getter and setters
    * ****************************************************************************************************************
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

    public int getStep() {
        return step;
    }

    public LinkedList<Node> getPath() {
        return path;
    }

    public Lane getCurrentLane() {
        return currentLane;
    }

    public CarState getCarState() {
        return carState;
    }

    public boolean isStopped() {
        return carState == CarState.STOPPED;
    }
}
