package entities.traffic_signs;

import entities.lane.Lane;
import simulation.SimEngine;

import java.time.Duration;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class TrafficLight extends TrafficSign {

    private TrafficLightState state;
    private Lane lane;
    private SimEngine simEngine;
    private Duration offset;
    private String name;

    public TrafficLight(Lane lane, SimEngine simEngine) {
        this.state = TrafficLightState.GREEN;
        this.lane = lane;
        this.simEngine = simEngine;
        offset = Duration.ZERO;
        this.name = "TrafficLight on " + lane.getId();
    }

    public TrafficLight(Lane lane, SimEngine simEngine, int delay) {
        this.state = TrafficLightState.GREEN;
        this.lane = lane;
        this.simEngine = simEngine;
        offset = Duration.ofSeconds(delay);
        this.name = "TrafficLight on " + lane.getId();
    }

    public TrafficLight(Lane lane, SimEngine simEngine, int delay, String name) {
        this(lane, simEngine, delay);
        this.name = name;
    }

    @Override
    public void init() {
        getSimEngine().addEvent(new ChangeColorEvent(this, getSimEngine().getCurrentSimTime().plus(state.getDuration()).plus(offset)));
    }

    @Override
    public void logStats() {

    }

    @Override
    public String getName() {
        return name;
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

    public Lane getLane() {
        return lane;
    }
}
