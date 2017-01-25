package entities.lane;

import entities.car.Car;
import entities.car.DelayedReactionEvent;
import entities.traffic_signs.TrafficSign;
import graph_network.Edge;
import graph_network.Node;
import logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private LaneState state;
    private LaneStats stats;

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
        state = LaneState.empty;
        stats = new LaneStats(this);
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
        updateLaneState(car.getSimEngine().getCurrentSimTime());
    }

    /**
     * Remove a car from the queue
     *
     * @param car the car to remove
     */
    public void removeCar(Car car) {
        carQueue.remove(car);
        updateLaneState(car.getSimEngine().getCurrentSimTime());
        // Since a car is removed from the lane we can update the following one
        updateNextCar(car);
    }

    /**
     * Updates the state of the lane to empty, free or full
     */
    private void updateLaneState(LocalDateTime dateTime) {
        if (hasSpace()) {
            if (carQueue.isEmpty()) state = LaneState.empty;
            else state = LaneState.free;
        } else state = LaneState.full;
        // If more than 75% of the lane is occupied than log it as full
        if (carQueue.size() > maxQueue() - 2) stats.wasJustBlocked(dateTime);
        else stats.wasJustFreed(dateTime);
        stats.logNumberOfCars(dateTime);
    }

    /**
     * Update the next car if any
     *
     * @param car the car that creates this notification
     */
    private void updateNextCar(Car car) {
        Car nextCar = getNextCar(car);
        if (nextCar != null) {
            Logger.getInstance().logInfo(getId(), "Updating next car: " + nextCar.getName());
            if (nextCar.isStopped()) {
                car.getSimEngine().addEvent(new DelayedReactionEvent(nextCar, Duration.ofSeconds(2)));
            } else if (nextCar.isDriving()) {
                nextCar.update();
            }
        }
    }

    /**
     * Returns true if this car is the last one in the list of cars
     *
     * @param car the car to check
     * @return true if car is last
     */
    public boolean isLastCar(Car car) {
        return (carQueue.get(carQueue.size() - 1) == car);
    }

    /**
     * Return the maximum number of car possible on this lane
     *
     * @return the maximum number of car possible on this lane
     */
    public int maxQueue() {
        return getLength() / (Car.length + distance_between_cars);
    }

    /**
     * Returns the length of the road, which is the weight of the node
     *
     * @return road's length
     */
    public int getLength() {
        return getWeight();
    }


    /*
    * ****************************************************************************************************************
    * GETTER AND SETTER
    * ****************************************************************************************************************
    */

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

    /**
     * Get the car following the one in parameter
     *
     * @param car the current car
     * @return the car following the current car
     */
    private Car getNextCar(Car car) {
        if (carQueue.indexOf(car) == carQueue.size() - 1) {
            return null;
        } else {
            return carQueue.get(carQueue.indexOf(car) + 1);
        }
    }

    /**
     * Returns true if a this lane has a traffic sign
     *
     * @return true if a this lane has a traffic sign
     */
    public boolean hasTrafficSign() {
        return trafficSign != null;
    }

    /**
     * Returns the state of the lane
     *
     * @return the state of the lane
     */
    public LaneState getState() {
        return state;
    }

    public LaneStats getStats() {
        return stats;
    }

    enum LaneState {
        empty,  // when there is no car
        free,   // when there is room
        full    // when there is no room for more cars
    }
}
