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
    private String name;

    public TrafficLight(Lane lane, SimEngine simEngine) {
        state = TrafficLightState.RED;
        this.lane = lane;
        this.simEngine = simEngine;
        this.name = "TrafficLight on " + lane.getId();
    }


    public TrafficLight(Lane lane, SimEngine simEngine, String name) {
        this(lane, simEngine);
        this.name = name;
    }

    @Override
    public void init() {
        // traffic light is initialized by intersections
//        getSimEngine().addEvent(new ChangeColorEvent(this, getSimEngine().getCurrentSimTime().plus(state.getDuration()).plus(offset)));
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
