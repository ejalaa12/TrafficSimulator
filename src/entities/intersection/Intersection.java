package entities.intersection;

import entities.car.Car;
import entities.lane.Lane;
import entities.traffic_signs.*;
import graph_network.Edge;
import graph_network.Node;
import logging.Logger;
import simulation.Entity;
import simulation.SimEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An class that implements a intersection.
 * An intersection is a Node of a Road Graph.
 *
 * It handles car arriving at an intersection.
 * It handles the traffic sign synchronization at an intersection
 */
public class Intersection extends Node implements Entity {

    private static final int maxCarInIntersection = 1;
    // create a hashmap that associates a car with the lane where it wants to go
    private HashMap<Lane, ArrayList<Car>> waitingCarsForLaneCorrespondences;
    private ArrayList<Car> priorityQueue;
    private List<Edge> connectedLanes;
    private List<TrafficLight> trafficLights;
    private int current_traffic_light_index;
    private ArrayList<Car> carsInsideIntersection;
    private SimEngine simEngine;

    public Intersection(String id, SimEngine simEngine) {
        super(id);
        this.simEngine = simEngine;
        carsInsideIntersection = new ArrayList<>();
        priorityQueue = new ArrayList<>();
        waitingCarsForLaneCorrespondences = new HashMap<>();
        trafficLights = new ArrayList<>();
        current_traffic_light_index = -1;
    }

    /**
     * Register a car to the intersection
     * A registered car is added to a dictionary
     * We can update any registered car if an event happens inside the intersection
     *
     * @param car      the car to register
     * @param nextLane the lane where the car wants to go
     */
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
            priorityQueue.add(car);
            Logger.getInstance().logInfo(getId(), car.getName() + " registered");
            // If car can pursue it's path
            handle(car);
        } else {
            Logger.getInstance().logWarning(getId(), String.format("Lane %s is not connected to intersection %s", nextLane.getId(), getId()));
            throw new NullPointerException("Trying to register a car to a lane that is not connected to this intersection");
        }
    }

    /**
     * Unregister a car from the intersection
     * This happends when a car enters the intersection.
     * @param car the car to unregister
     * @param registeredLane the lane where the car wanted to go
     */
    public void unregisterCar(Car car, Lane registeredLane) {
        if (!waitingCarsForLaneCorrespondences.containsKey(registeredLane)) {
            throw new NullPointerException("Trying to unregister from a lane that is not connected");
        } else if (!waitingCarsForLaneCorrespondences.get(registeredLane).contains(car)) {
            throw new NullPointerException("Trying to unregister a car that wasn't registered");
        } else {
            waitingCarsForLaneCorrespondences.get(registeredLane).remove(car);
            priorityQueue.remove(car);
        }
    }

    /**
     * Handles a car when it is registered
     * Handling a car depends on the presence of a traffic sign at the intersection:
     * If no traffic sign:
     *  @see entities.intersection.Intersection#tryToGetIntoIntersection(Car)
     * If stop sign:
     *  @see entities.intersection.Intersection#stopSignBehavior(Car, StopSign)
     * If traffic light:
     *  @see entities.intersection.Intersection#trafficLightBehavior(Car, TrafficLight)
     *
     * @param car the car to handle
     */
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

    /**
     * This implements the behavior to follow when there is a traffic light at the intersection
     * If the light is red: stop
     * else:
     * @see entities.intersection.Intersection#tryToGetIntoIntersection(Car)
     *
     *
     * @param car the car that is at the intersection
     * @param trafficLight the traffic light on this intersection
     */
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

    /**
     * This implements the behavior to follow when there is a stop at the intersection
     * The idea is that a car always stops for 3 seconds at a stop sign
     * Then it is registered to the stop
     * If the car is registered it can try to enter the intersection if the intersection is free
     *
     * @param car
     * @param stopSign
     */
    private void stopSignBehavior(Car car, StopSign stopSign) {
        car.stop();
        if (stopSign.getWaitingCar() == car) {
            if (stopSign.isWaitFinish())
                tryToGetIntoIntersection(car);
        } else {
            Logger.getInstance().logInfo(car.getName(), "Stop and wait at stop");
            stopSign.registerCar(car);
            simEngine.addEvent(new WaitAtStopEvent(stopSign, this, car));
        }
//        Logger.getInstance().logWarning(getId(), "Stop sign behavior not implemented");
    }

    /**
     * When there is no traffic sign, or the light is green or the car has waited at a stop:
     * We can try to get into the intersection.
     * If the intersection is free, it can enter, and leave the intersection after X seconds
     * @see entities.intersection.ExitFromIntersectionEvent
     * Otherwise, the car will stop and wait for an event that frees the intersection
     *
     *
     * @param car the car which want to get into the intersection
     * @return true if insertion was successful
     */
    public boolean tryToGetIntoIntersection(Car car) {
        Logger.getInstance().logInfo(car.getName(), "Trying to get into intersection: " + getName());
        Lane nextLane = car.getNextLane();
        // in french driving system you don't get inside an intersection if the next lane is not free
        if (nextLane.hasSpace() && carsInsideIntersection.size() < maxCarInIntersection) {
            Logger.getInstance().logInfo(car.getName(), "Intersection free");
            Lane tmp = car.getCurrentLane();
            if (tmp.hasTrafficSign())
                tmp.getTrafficSign().unregisterCar(car);
            unregisterCar(car, nextLane);
            car.getIntoIntersection(this);
            simEngine.addEvent(new ExitFromIntersectionEvent(this, car, tmp, nextLane));
//            car.changeLane(nextLane);
//            car.drive();
            return true;
        } else if (carsInsideIntersection.size() >= maxCarInIntersection) {
            String msg = String.format("Intersection %s is busy", getName());
            Logger.getInstance().logInfo(getName(), msg);
            car.stop();
            return false;
        } else {
            String msg = String.format("Next lane is full, %s waiting at intersection %s", car.getName(), getName());
            Logger.getInstance().logInfo(getName(), msg);
            car.stop();
            return false;
        }
    }

    /**
     * add a car inside the intersection
     * @param car the car to insert
     */
    public void addCarInsideIntersection(Car car) {
        if (carsInsideIntersection.contains(car)) {
            Logger.getInstance().logWarning(getName(), car.getName() + " is already inside intersection " + getName());
            throw new IllegalStateException("car already in intersection");
        }
        carsInsideIntersection.add(car);
    }

    /**
     * Removes a car from within the intersection
     *
     * @param car the car to remove
     */
    public void removeCarFromInsideIntersection(Car car) {
        if (!carsInsideIntersection.contains(car)) {
            Logger.getInstance().logWarning(getName(), car.getName() + " wasn't in intersection " + getName());
            throw new IllegalStateException("car is not in intersection");
        }
        carsInsideIntersection.remove(car);
        if (!priorityQueue.isEmpty()) {
            handle(priorityQueue.get(0));
        }
    }

    /**
     * Notifies a car that wants to go to a specified lane
     *
     * @param destinationLane the destination lane from which we get the waiting list
     */
    public void notifyCarsWithNextLane(Lane destinationLane) {
        // the order of the arraylist in the hashmap creates a priority queue
        handle(waitingCarsForLaneCorrespondences.get(destinationLane).get(0));
    }

    /**
     * This methods set the lanes that arrives at the interesection
     * This is useful to create the dictionary of registered car and where they come from.
     * It is also useful to deduce the list of traffic sign that are on this intersection
     * @param connectionsThatArriveAt the list of lane(or edges) that arrive at this intersection(or node)
     */
    public void addConnectedLanes(List<Edge> connectionsThatArriveAt) {
        connectedLanes = connectionsThatArriveAt;
        for (Edge edge : connectedLanes) {
            waitingCarsForLaneCorrespondences.put((Lane) edge, new ArrayList<>());
            if (edge.getDestination() == this){
                if ((((Lane) edge).getTrafficSign() instanceof TrafficLight)) {
                    trafficLights.add((TrafficLight) ((Lane) edge).getTrafficSign());
                    current_traffic_light_index = 0;
                }
            }
        }
    }

    /**
     * Initializing an intersection consist of initializing the traffic lights that are on this intersection
     */
    @Override
    public void init() {
        //no initialization, event are only created when car get into intersection
        if (!trafficLights.isEmpty()) {
            getSimEngine().addEvent(new ChangeColorEvent(trafficLights.get(current_traffic_light_index), getSimEngine().getCurrentSimTime()));
        }
    }

    @Override
    public void logStats() {

    }

    @Override
    public SimEngine getSimEngine() {
        return simEngine;
    }

    /**
     *
     * @param car the car to check
     * @return true if the car was registered to this intersection
     */
    private boolean wasRegistered(Car car) {
        for (ArrayList<Car> cars : waitingCarsForLaneCorrespondences.values()) {
            if (cars.contains(car)) {
                return true;
            }
        }
        return false;
    }

    /**
     * When a light turns red on intersection, the next light must turn green 3 seconds after.
     * This methods allows us to get the next traffic light.
     * @return the next traffic light
     */
    public TrafficLight getNextTrafficLight() {
        current_traffic_light_index += 1;
        current_traffic_light_index %= trafficLights.size();
        return trafficLights.get(current_traffic_light_index);
    }
}
