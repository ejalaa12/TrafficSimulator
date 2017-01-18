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
 * Created by ejalaa on 25/12/2016.
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

    /**
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
     * For the moment we just change to the next lane like previously
     *
     * @param car the car to move
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

    public void addCarInsideIntersection(Car car) {
        if (carsInsideIntersection.contains(car)) {
            Logger.getInstance().logWarning(getName(), car.getName() + " is already inside intersection " + getName());
            throw new IllegalStateException("car already in intersection");
        }
        carsInsideIntersection.add(car);
    }

    public void removeCarFromInsideIntersection(Car car, Lane originLane) {
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

    private boolean wasRegistered(Car car) {
        for (ArrayList<Car> cars : waitingCarsForLaneCorrespondences.values()) {
            if (cars.contains(car)) {
                return true;
            }
        }
        return false;
    }

    public TrafficLight getNextTrafficLight() {
        current_traffic_light_index += 1;
        current_traffic_light_index %= trafficLights.size();
        return trafficLights.get(current_traffic_light_index);
    }
}
