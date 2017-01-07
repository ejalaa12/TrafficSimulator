package entities;

import entities.car.Car;
import entities.traffic_light.TrafficSign;
import graph_network.Edge;
import graph_network.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a Lane which is an edge of the road network
 * A Lane has a list of car and a speed_limit
 */
public class Lane extends Edge {

    private final int distance_between_cars = 2;
    private List<Car> carQueue;
    private double speed_limit;
    private TrafficSign trafficSign;

    /**
     * @param id          the lane unique id
     * @param source      source of the lane
     * @param destination destination of the lane
     * @param length      lane's length
     */
    public Lane(String id, Node source, Node destination, int length, double speed_limit) {
        super(id, source, destination, length);
        carQueue = new ArrayList<>();
        this.speed_limit = speed_limit;
        this.trafficSign = null;    // trafficSign is null if not set
    }

    /**
     * Check if there is enough space for a new car on the road using the following formula:
     * Road_length - #Cars * (car_length + distance_between_cars) >= (car_length + distance_between_cars)
     *
     * @return true if enough space
     */
    public boolean hasSpace() {
        return getLength() - (carQueue.size() + 1) * (Car.length + distance_between_cars) >= 0;
    }

    /**
     * Add a car to car queue
     *
     * @param car the car to add
     */
    public void addCar(Car car) {
        carQueue.add(car);
    }

    /**
     * Remove a car from the queue
     *
     * @param car the car to remove
     */
    public void removeCar(Car car) {
        carQueue.remove(car);
        notifyOtherCars();
    }

    /**
     * Notifies all other cars in the Queue that a car has left the queue so they can update their behavior
     */
    private void notifyOtherCars() {
        for (Car car : carQueue) {
            car.update();
        }
    }

    /*
    * ****************************************************************************************************************
    * GETTER AND SETTER
    * ****************************************************************************************************************
    */

    /**
     * Returns the length of the road, which is the weight of the node
     *
     * @return road's length
     */
    public int getLength() {
        return getWeight();
    }

    /**
     * Returns the list of cars on this lane
     *
     * @return the list of cars on this lane
     */
    public List<Car> getCarQueue() {
        return carQueue;
    }

    /**
     * Returns the speed limit of the lane
     * @return the speed limit of the lane
     */
    public double getSpeed_limit() {
        return speed_limit;
    }

    /**
     * Calculates and returns the next free spot on the lane
     * We assume that even if cars are driving, the free spot is going to be the one
     * that is free when all cars are stopped in queue.
     *
     * We calculate the car spot according to it's position in the queue
     *
     * @return an integer corresponding to the position (in meters) where the free spot is
     */
    public int getFreeSpotPositionForCar(Car car) {
        int positionInQueue = carQueue.indexOf(car);
        return getLength() - positionInQueue * (distance_between_cars + Car.length);
    }

    /**
     * Returns the traffic sign that is on the end of this lane
     *
     * @return the traffic sign that is on the end of this lane
     */
    public TrafficSign getTrafficSign() {
        return trafficSign;
    }

    /**
     * Sets the traffic sign on this lane
     *
     * @param trafficSign the trafficSign to set for this lane
     */
    public void setTrafficSign(TrafficSign trafficSign) {
        this.trafficSign = trafficSign;
    }
}
