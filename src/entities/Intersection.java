package entities;

import entities.car.Car;
import entities.traffic_light.StopSign;
import entities.traffic_light.TrafficLight;
import entities.traffic_light.TrafficLightState;
import entities.traffic_light.TrafficSign;
import graph_network.Edge;
import graph_network.Node;
import logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class Intersection extends Node {

    private HashMap<Lane, ArrayList<Car>> waitingCarsForLaneCorrespondences;
    private List<Edge> connectedLanes;

    public Intersection(String id) {
        super(id);
        waitingCarsForLaneCorrespondences = new HashMap<>();
    }

    public void registerCar(Car car, Lane nextLane) {
        // Register the car with the lane where it want to go
        if (waitingCarsForLaneCorrespondences.containsKey(nextLane)) {
            if (waitingCarsForLaneCorrespondences.get(nextLane).contains(car)) {
                Logger.getInstance().logWarning(getId(), car.getName() + "Already registered, removing old");
                waitingCarsForLaneCorrespondences.get(nextLane).remove(car);
            }
            waitingCarsForLaneCorrespondences.get(nextLane).add(car);
            // If car can pursue it's path
            handle(car);
        } else {
            Logger.getInstance().logWarning(getId(), String.format("Lane %s is not connected to intersection %s", nextLane.getId(), getId()));
        }

    }

    private void handle(Car car) {
        TrafficSign trafficSign = car.getCurrentLane().getTrafficSign();
        // If no trafficSign then let the car pass if intersection is free
        if (trafficSign == null) {
            tryToGetIntoIntersection(car);
        } else {
            if (trafficSign instanceof TrafficLight) {
                trafficLightBehavior(car, (TrafficLight) trafficSign);
            } else if (trafficSign instanceof StopSign) {
                stopSignBehavior(car, (StopSign) trafficSign);
            }
        }
    }

    private void trafficLightBehavior(Car car, TrafficLight trafficLight) {
        if (trafficLight.getState() == TrafficLightState.RED) {
            car.stop();
            trafficLight.registerCar(car);
            Logger.getInstance().logEvent(car.getName(), "Stopping at RedLight");
        } else {
            tryToGetIntoIntersection(car);
        }

    }

    private void stopSignBehavior(Car car, StopSign stopSign) {
        car.stop();

        Logger.getInstance().logWarning(getId(), "Stop sign behavior not implemented");
    }

    /**
     * For the moment we just change to the next lane like previously
     *
     * @param car the car to move
     * @return true if insertion was successful
     */
    public boolean tryToGetIntoIntersection(Car car) {
        Lane nextLane = car.getNextLane();
        if (nextLane.hasSpace()) {
            car.changeLane(nextLane);
            car.drive();
            waitingCarsForLaneCorrespondences.get(nextLane).remove(car);
            return true;
        } else {
            String msg = "Next lane is full, waiting at intersection " + getName();
            Logger.getInstance().logInfo(getName(), msg);
            car.stop();
            return false;
        }
    }

    public void notifyCarsWithNextLane(Lane nextLaneToCheck) {
        // the order of the arraylist in the hashmap creates a priority queue
        waitingCarsForLaneCorrespondences.get(nextLaneToCheck).get(0).update();
    }

    public void addConnectedLanes(List<Edge> connectionsThatArriveAt) {
        connectedLanes = connectionsThatArriveAt;
        for (Edge edge : connectedLanes) {
            waitingCarsForLaneCorrespondences.put((Lane) edge, new ArrayList<>());
        }
    }
}
