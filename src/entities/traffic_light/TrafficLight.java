package entities.traffic_light;

import entities.Lane;
import simulation.Entity;
import simulation.SimEngine;

import java.time.Duration;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class TrafficLight implements Entity {

    private State state;
    // TODO: 01/01/2017 change frequency so that green and red light don't have same duration
    private Duration frequency;
    private Lane lane;

    public TrafficLight(Lane lane) {
        this.state = State.GREEN;
        this.lane = lane;
        this.frequency = Duration.ofMinutes(10);
    }

    @Override
    public void init() {
        getSimEngine().addEvent(new ChangeColorEvent(this, getSimEngine().getCurrentSimTime().plus(frequency)));
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
        // FIXME: 01/01/2017 maybe find a cleaner to get the simEngine
        return lane.getCarQueue().get(0).getSimEngine();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        GREEN, ORANGE, RED
    }
}
