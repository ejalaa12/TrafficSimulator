package entities.car;

import logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * An event that happens when a car was supposed to stop at this point.
 * However, this event also checks if no new position was freed in the meantime.
 * If so, the car is updated, and goes to the next position.
 */
public class ExpectedStopEvent extends CarEvent {

    protected ExpectedStopEvent(Car car, LocalDateTime scheduledTime) {
        super(car, scheduledTime, car.getName() + " was supposed to do something now");
    }

    @Override
    public void doAction() {
        super.doAction();
        // because of the car has only be doing the current event (which is this one)
        // update the car position by adding the calculated travelled distance during since this event was posted
        Duration eventDuration = Duration.between(postedTime, scheduledTime);
        double elapsed = eventDuration.getSeconds() + eventDuration.getNano() * 1e-9;
        double distance = Math.round(elapsed * car.getSpeed());
        Logger.getInstance().logDebug("ExpectedStopEvent", scheduledTime, "adding distance: " + distance);
        car.addTravel(distance);
        // If this event finally happens, then we check what to do next
        car.update();

    }
}
