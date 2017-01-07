package entities.car;

import simulation.Event;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by ejalaa on 07/01/2017.
 */
public class ExpectedStopEvent extends Event {

    private Car car;

    protected ExpectedStopEvent(Car car, LocalDateTime scheduledTime) {
        super(car.getName(), scheduledTime, "Car was supposed to stop now");
        this.car = car;
    }

    @Override
    public void doAction() {
        // because of the car has only be doing the current event (which is this one)
        Duration eventDuration = Duration.between(postedTime, scheduledTime);
        double elapsed = eventDuration.getSeconds() + eventDuration.getNano() * 1e-9;
        car.addTravel(Math.round(elapsed * car.getSpeed()));
        // If this event finally happens, then we check what to do next
        car.update();
    }
}
