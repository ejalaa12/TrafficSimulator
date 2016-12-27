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
import java.util.LinkedList;

/**
 * This class models a car that moves in a roadNetwork
 */
public class Car implements Entity {

    public static final int length = 4;
    private Zone source, destination;
    private double speed;
    // Simulator Engine
    private SimEngine simEngine;
    private LinkedList<Node> path;
    private String Id;
    // A car has a knowledge of the roadNetwork
    private RoadNetwork roadNetwork;
    // position in network
    private Lane currentLane;
    private int step;

    // event list
    // we need to keep track of this event since it might change due to outside events
    private Event nextChangingLaneEvent;


    public Car(String Id, Zone source, Zone destination, RoadNetwork roadNetwork, SimEngine simEngine) {

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
        // car is created, and starts in one zone so we skip it and get directly to the first lane (if possible)
        currentLane = roadNetwork.getLaneBetween(path.getFirst(), path.get(1));
        if (currentLane.hasSpace()) {
            currentLane.addCar(this);
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "Added to " + currentLane.getId(), LogLevel.INFO);
            driveOnLane(currentLane);
            step = 1;
        } else {
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "Could not get into lane because it's full", LogLevel.WARNING);
            carDismissed();
        }
    }

    public void nextStep() {
        // this can also be checked by checking if step == path.size()
        // because the last step MUST be a Zone
        if (path.get(step) instanceof Zone) {
            arrived();
            return;
        }
        // Get next Lane
        Lane nextLane = roadNetwork.getLaneBetween(path.get(step), path.get(step + 1));
        // Add car to next lane if free, else stay in Line (does nothing)
        if (nextLane.hasSpace()) {
            currentLane.removeCar(this);
            nextLane.addCar(this);
            currentLane = nextLane;
            step += 1;
            driveOnLane(currentLane);
        }

    }


    /*
    * ****************************************************************************************************************
    * State changing
    * ****************************************************************************************************************
    */

    private void driveOnLane(Lane lane) {
        // for now we go instantly from 0 to max_speed
        speed = lane.getSpeed_limit();
        int destination = lane.getFreeSpotPosition();
        long dt = Math.round(destination / speed);
        Duration timeToArrive = Duration.ofSeconds(dt);
        nextChangingLaneEvent = new ChangingLaneEvent(this, simEngine.getCurrentSimTime().plus(timeToArrive));
        simEngine.addEvent(nextChangingLaneEvent);
    }

    private void goToNextLane() {
        if (step < path.size()) {
            Lane nextLane = roadNetwork.getLaneBetween(path.get(step), path.get(step + 1));
            if (nextLane.hasSpace()) {
                currentLane.removeCar(this);
                nextLane.addCar(this);
                currentLane = nextLane;
                step += 1;
            } else {
                waitBecauseNextLaneFull();
            }
        } else {
            arrived();
        }
    }

    private void waitBecauseNextLaneFull() {
        Intersection currentIntersection = (Intersection) path.get(step + 1);
        String msg = "Waiting at intersection " + currentIntersection.getName();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
    }

    private void arrived() {
        // TODO: 27/12/2016 implement it
        if (path.getLast() instanceof Zone) {
//        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "Car arrived", Logger.LogLevel.INFO);
        } else {
            String msg = "Finished path but last node is not Zone...";
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.WARNING);
        }
//        throw new NotImplementedException();
    }

    private void carDismissed() {
        // TODO: 27/12/2016 implement it
        throw new NotImplementedException();
    }

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
}
