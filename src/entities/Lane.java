package entities;

import entities.car.Car;
import entities.car.CarNotification;
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
    }

    /**
     * Check if there is enough space for a new car on the road using the following formula:
     * Road_length - #Cars * (car_length + distance_between_cars) > (car_length + distance_between_cars)
     *
     * @return true if enough space
     */
    public boolean hasSpace() {
        return getLength() - (carQueue.size() + 1) * (Car.length + distance_between_cars) > 0;
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
        if (!carQueue.isEmpty()) {
            carQueue.get(0).notifyCar(CarNotification.GoToEndOfLane);
            int i;
            for (i = 1; i < carQueue.size(); i++) {
                carQueue.get(i).notifyCar(CarNotification.GoToNextFreeSpot);
            }
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

    public List<Car> getCarQueue() {
        return carQueue;
    }

    public double getSpeed_limit() {
        return speed_limit;
    }

    public int getFreeSpotPosition() {
        // - We assume that even if cars are driving, the free spot is going to be the one
        //          that is free when all cars are stopped in queue
        // - The free spot is the one to be taken by the last added car, thus the -1 (because the last added is already in the list)
        return getLength() - (carQueue.size() - 1) * (distance_between_cars + Car.length);
    }

    public boolean isFree() {
        return carQueue.isEmpty();
    }
}
