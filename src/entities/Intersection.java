package entities;

import entities.car.Car;
import entities.traffic_light.StopSign;
import entities.traffic_light.TrafficLight;
import entities.traffic_light.TrafficLightState;
import entities.traffic_light.TrafficSign;
import graph_network.Node;
import logging.Logger;

import java.util.ArrayList;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class Intersection extends Node {

    private ArrayList<Car> waitingCars;

    public Intersection(String id) {
        super(id);
        waitingCars = new ArrayList<>();
    }

    public void registerCar(Car car) {
        // Register the car
        waitingCars.add(car);
        // If car can pursue it's path
        handle(car);

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
            Logger.getInstance().logInfo(getName(), "Stopping at RedLight");
        } else {
            tryToGetIntoIntersection(car);
        }

    }

    private void stopSignBehavior(Car car, StopSign stopSign) {
    }

    /**
     * For the moment we just change to the next lane like previously
     *
     * @param car the car to move
     */
    private void tryToGetIntoIntersection(Car car) {
        int indexOfNextNodeInPath = car.getPath().indexOf(car.getCurrentLane().getDestination()) + 1;
        Lane nextLane = car.getRoadNetwork().getLaneBetween(this, car.getPath().get(indexOfNextNodeInPath));
        if (nextLane.hasSpace()) {
            car.changeLane(nextLane);
            car.drive();
            waitingCars.remove(car);
        } else {
            String msg = "Next lane is full, waiting at intersection " + getName();
            Logger.getInstance().logInfo(getName(), msg);
            car.stop();
        }
    }

    public void notifyCarsWithNextLane(Lane nextLaneToCheck) {
        for (Car car : waitingCars) {
            if (car.isLaneNextStep(nextLaneToCheck)) {
                car.update();
                break;
            }
        }
    }
}
