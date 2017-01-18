package entities.traffic_signs;

import entities.lane.Lane;
import simulation.SimEngine;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class TrafficLight extends TrafficSign {

    private TrafficLightState state;
    private Lane lane;
    private SimEngine simEngine;

    public TrafficLight(Lane lane, SimEngine simEngine) {
        this.state = TrafficLightState.GREEN;
        this.lane = lane;
        this.simEngine = simEngine;
    }

    @Override
    public void init() {
        getSimEngine().addEvent(new ChangeColorEvent(this, getSimEngine().getCurrentSimTime().plus(state.getDuration())));
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

    public Lane getLane() {
        return lane;
    }
}
