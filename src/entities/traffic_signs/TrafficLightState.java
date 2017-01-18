package entities.traffic_signs;

import java.time.Duration;

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

    static {
        GREEN.duration = Duration.ofSeconds(30);
        RED.duration = Duration.ofSeconds(69);
    }

    private TrafficLightState nextState;
    private Duration duration;

    public TrafficLightState getNextState() {
        return nextState;
    }

    public Duration getDuration() {
        return duration;
    }
}
