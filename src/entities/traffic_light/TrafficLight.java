package entities.traffic_light;

import entities.Lane;
import simulation.Entity;
import simulation.SimEngine;

import java.time.Duration;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class TrafficLight extends TrafficSign implements Entity {

    private State state;
    // TODO: 01/01/2017 change frequency so that green and red light don't have same duration
    private Duration frequency;
    private Lane lane;
    private SimEngine simEngine;

    public TrafficLight(Lane lane, SimEngine simEngine) {
        this.state = State.GREEN;
        this.lane = lane;
        this.simEngine = simEngine;
        this.frequency = Duration.ofMinutes(1);
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
        // FIXME: 01/01/2017 maybe find a cleaner to get the simEngine because lane don't always have cars
        return simEngine;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Duration getFrequency() {
        return frequency;
    }

    public enum State {
        GREEN, ORANGE, RED
    }
}
