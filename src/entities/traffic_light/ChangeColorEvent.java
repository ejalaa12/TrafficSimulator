package entities.traffic_light;

import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class ChangeColorEvent extends Event {

    private TrafficLight trafficLight;

    protected ChangeColorEvent(TrafficLight trafficLight, LocalDateTime scheduledTime) {
        super(trafficLight.getName(), scheduledTime, "Changing Light color");
        this.trafficLight = trafficLight;
    }

    @Override
    public void doAction() {
        switch (trafficLight.getState()) {
            case GREEN:
                trafficLight.setState(TrafficLight.State.RED);
            case ORANGE:
                break;
            case RED:
                trafficLight.setState(TrafficLight.State.GREEN);
        }
    }
}
