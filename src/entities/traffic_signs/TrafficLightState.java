package entities.traffic_signs;

import java.time.Duration;

/**
 * An enumeration of the states of a traffic light
 * Contains the duration of a green light as well
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

    public Duration getDuration() {
        return Duration.ofSeconds(30);
    }
}
