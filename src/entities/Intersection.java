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
        if (connectedLanes == null)
            Logger.getInstance().logFatal(getName(), "Intersection was not connected during initialization");
        // Register the car with the lane where it want to go
        if (waitingCarsForLaneCorrespondences.containsKey(nextLane)) {
            if (waitingCarsForLaneCorrespondences.get(nextLane).contains(car)) {
//                Logger.getInstance().logWarning(getId(), car.getName() + "Already registered, removing old");
//                unregisterCar(car, nextLane);
                throw new IllegalStateException("the car to register was already registered");
            }
            waitingCarsForLaneCorrespondences.get(nextLane).add(car);
            Logger.getInstance().logInfo(getId(), car.getName() + " registered");
            // If car can pursue it's path
            handle(car);
        } else {
            Logger.getInstance().logWarning(getId(), String.format("Lane %s is not connected to intersection %s", nextLane.getId(), getId()));
            throw new NullPointerException("Trying to register a car to a lane that is not connected to this intersection");
        }
    }

    public void unregisterCar(Car car, Lane registeredLane) {
        if (!waitingCarsForLaneCorrespondences.containsKey(registeredLane)) {
            throw new NullPointerException("Trying to unregister from a lane that is not connected");
        } else if (!waitingCarsForLaneCorrespondences.get(registeredLane).contains(car)) {
            throw new NullPointerException("Trying to unregister a car that wasn't registered");
        } else {
            waitingCarsForLaneCorrespondences.get(registeredLane).remove(car);
        }
    }

    public void handle(Car car) {
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
        trafficLight.registerCar(car);
        if (trafficLight.getState() == TrafficLightState.RED) {
            car.stop();
            Logger.getInstance().logEvent(car.getName(), "Stopping at RedLight");
        } else {
            Logger.getInstance().logInfo(car.getName(), "Traffic light is green");
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
        Logger.getInstance().logInfo(car.getName(), "Trying to get into intersection");
        Lane nextLane = car.getNextLane();
        if (nextLane.hasSpace()) {
            Logger.getInstance().logInfo(car.getName(), "Intersection free");
            Lane tmp = car.getCurrentLane();
            car.changeLane(nextLane);
            if (tmp.hasTrafficSign())
                tmp.getTrafficSign().unregisterCar(car);
            car.drive();
            unregisterCar(car, nextLane);
            return true;
        } else {
            String msg = String.format("Next lane is full, %s waiting at intersection %s", car.getName(), getName());
            Logger.getInstance().logInfo(getName(), msg);
            car.stop();
            return false;
        }
    }

    public void notifyCarsWithNextLane(Lane nextLaneToCheck) {
        // the order of the arraylist in the hashmap creates a priority queue
        handle(waitingCarsForLaneCorrespondences.get(nextLaneToCheck).get(0));
    }

    public void addConnectedLanes(List<Edge> connectionsThatArriveAt) {
        connectedLanes = connectionsThatArriveAt;
        for (Edge edge : connectedLanes) {
            waitingCarsForLaneCorrespondences.put((Lane) edge, new ArrayList<>());
        }
    }
}
