package entities.car;

import logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by ejalaa on 07/01/2017.
 */
public class ExpectedStopEvent extends CarEvent {

    private Car car;

    protected ExpectedStopEvent(Car car, LocalDateTime scheduledTime) {
        super(car.getName(), scheduledTime, "Car is supposed to do something now");
        this.car = car;
    }

    @Override
    public void doAction() {
        super.doAction();
        // because of the car has only be doing the current event (which is this one)
        Duration eventDuration = Duration.between(postedTime, scheduledTime);
        double elapsed = eventDuration.getSeconds() + eventDuration.getNano() * 1e-9;
        double distance = Math.round(elapsed * car.getSpeed());
        Logger.getInstance().logDebug("ExectedStopEvent", scheduledTime, "adding distance: " + distance);
        car.addTravel(distance);
        // If this event finally happens, then we check what to do next
        car.update();

    }
}
