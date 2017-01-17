package entities.traffic_signs;

import entities.Lane;
import simulation.SimEngine;

import java.time.Duration;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class TrafficLight extends TrafficSign {

    private TrafficLightState state;
    // TODO: 01/01/2017 change frequency so that green and red light don't have same duration
    private Duration frequency;
    private Lane lane;
    private SimEngine simEngine;

    public TrafficLight(Lane lane, SimEngine simEngine) {
        this.state = TrafficLightState.GREEN;
        this.lane = lane;
        this.simEngine = simEngine;
        this.frequency = Duration.ofMinutes(20);
    }

    @Override
    public void init() {
        getSimEngine().addEvent(new ChangeColorEvent(this, getSimEngine().getCurrentSimTime().plus(frequency)));
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

    public Lane getLane() {
        return lane;
    }
}
