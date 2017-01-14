package entities.traffic_light;

import entities.Intersection;
import entities.Lane;
import entities.car.Car;
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
    private Car waitingCar;

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

    public void registerCar(Car car) {
        if (waitingCar != null) {
            String msg = String.format("%s was already waiting, and you try to add %s", waitingCar.getName(), car.getName());
            Logger.getInstance().logWarning(getName(), msg);
        }
        Logger.getInstance().logInfo(getName(), car.getName() + " is waiting...");
        waitingCar = car;
    }

    public void notifyFirstCarInLaneToChangeLane() {
        if (waitingCar == null) {
            Logger.getInstance().logInfo(getName(), "No car waiting at red light");
        } else {
            boolean success = ((Intersection) waitingCar.getCurrentLane().getDestination()).tryToGetIntoIntersection(waitingCar);
            if (success) waitingCar = null;
        }
    }


    @Override
    public void logStats() {

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
