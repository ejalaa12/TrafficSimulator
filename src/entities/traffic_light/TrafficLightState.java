package entities.traffic_light;

/**
 * Created by ejalaa on 05/01/2017.
 */
public enum TrafficLightState {
    // Since no acceleration is implemented orange makes no sense (we can stop instantaneously)
    GREEN,
    //        ORANGE,
    RED;

    private TrafficLightState nextState;

    static {
        GREEN.nextState = RED;
//            GREEN.nextState = ORANGE;
//            ORANGE.nextState = RED;
        RED.nextState = GREEN;
    }

    public TrafficLightState getNextState() {
        return nextState;
    }
}
