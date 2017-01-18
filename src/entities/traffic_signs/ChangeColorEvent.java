package entities.traffic_signs;

import entities.intersection.Intersection;
import entities.zone.Zone;
import simulation.Event;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by ejalaa on 01/01/2017.
 */
public class ChangeColorEvent extends Event {

    private TrafficLight trafficLight;

    public ChangeColorEvent(TrafficLight trafficLight, LocalDateTime scheduledTime) {
        super(trafficLight.getName(), scheduledTime, "Changing Light color");
        String description = getDescription();
        description += " " + trafficLight.getState() + " > " + trafficLight.getState().getNextState();
        setDescription(description);
        this.trafficLight = trafficLight;
    }

    @Override
    public void doAction() {
        trafficLight.setState(trafficLight.getState().getNextState());
        // update waiting car
        if (trafficLight.getWaitingCar() != null)
            ((Intersection) trafficLight.getLane().getDestination()).handle(trafficLight.getWaitingCar());
        // next changing state event
        if (trafficLight.getState() == TrafficLightState.GREEN) {
            LocalDateTime nextTime = trafficLight.getSimEngine().getCurrentSimTime().plus(trafficLight.getState().getDuration());
            trafficLight.getSimEngine().addEvent(new ChangeColorEvent(trafficLight, nextTime));
        } else if (trafficLight.getState() == TrafficLightState.RED) {
            Intersection intersection = ((Intersection) trafficLight.getLane().getDestination());
            TrafficLight nextLight = intersection.getNextTrafficLight();
            LocalDateTime nextTime = trafficLight.getSimEngine().getCurrentSimTime().plus(Duration.ofSeconds(3));
            trafficLight.getSimEngine().addEvent(new ChangeColorEvent(nextLight, nextTime));
        }
    }
}