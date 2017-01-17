package entities.traffic_signs;

/**
 * Created by ejalaa on 05/01/2017.
 */
public enum TrafficLightState {
    // Since no acceleration is implemented orange makes no sense (we can stop instantaneously)
    GREEN,
    //        ORANGE,
    RED;

    static {
        GREEN.nextState = RED;
//            GREEN.nextState = ORANGE;
//            ORANGE.nextState = RED;
        RED.nextState = GREEN;
    }

    private TrafficLightState nextState;

    public TrafficLightState getNextState() {
        return nextState;
    }
}
