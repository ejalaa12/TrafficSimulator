package entities.traffic_light;

import entities.Lane;
import entities.car.Car;
import entities.car.CarNotification;
import entities.car.ChangingLaneEvent;
import logging.LogLevel;
import logging.Logger;
import simulation.Entity;
import simulation.SimEngine;

import java.time.Duration;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class TrafficLight extends TrafficSign implements Entity {

    private TrafficLightState state;
    // TODO: 01/01/2017 change frequency so that green and red light don't have same duration
    private Duration frequency;
    private Lane lane;
    private SimEngine simEngine;

    public TrafficLight(Lane lane, SimEngine simEngine) {
        this.state = TrafficLightState.GREEN;
        this.lane = lane;
        this.simEngine = simEngine;
        this.frequency = Duration.ofMinutes(2);
    }

    @Override
    public void init() {
        getSimEngine().addEvent(new ChangeColorEvent(this, getSimEngine().getCurrentSimTime().plus(frequency)));
    }

    public void notifyFirstCarInLaneToChangeLane() {
        // TODO: 05/01/2017 log number of car stopped in lane
        long nocs = lane.getCarQueue().stream().filter(Car::isStopped).count();
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "car stopped: "+nocs, LogLevel.INFO);
        if (lane.getCarQueue().isEmpty()) {
            String msg = lane.getId() + "'s car queue is empty, no car to notify";
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), msg, LogLevel.INFO);
        } else {
            lane.getCarQueue().get(0).notifyCar(CarNotification.GreenLightSoChangeLane);
//            simEngine.addEvent(new ChangingLaneEvent(lane.getCarQueue().get(0), simEngine.getCurrentSimTime()));
        }
    }

    @Override
    public void printStats() {

    }

    @Override
    public String getName() {
        return "TrafficLight on " + lane.getId();
    }

    @Override
    public SimEngine getSimEngine() {
        return simEngine;
    }

    public TrafficLightState getState() {
        return state;
    }

    public void setState(TrafficLightState state) {
        this.state = state;
    }

    public Duration getFrequency() {
        return frequency;
    }
}
