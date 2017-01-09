package entities.traffic_light;

import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class ChangeColorEvent extends Event {

    private TrafficLight trafficLight;

    public ChangeColorEvent(TrafficLight trafficLight, LocalDateTime scheduledTime) {
        super(trafficLight.getName(), scheduledTime, "Changing Light color");
        String description = getDescription();
        description += " > " + trafficLight.getState().getNextState();
        setDescription(description);
        this.trafficLight = trafficLight;
    }

    @Override
    public void doAction() {
        trafficLight.setState(trafficLight.getState().getNextState());
        if (trafficLight.getState() == TrafficLightState.GREEN) {
            trafficLight.notifyFirstCarInLaneToChangeLane();
        }
        // next changing state event
//        LocalDateTime nextTime = trafficLight.getSimEngine().getCurrentSimTime().plus(trafficLight.getFrequency());
//        trafficLight.getSimEngine().addEvent(new ChangeColorEvent(trafficLight, nextTime));
    }
}
