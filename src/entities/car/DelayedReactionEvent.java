package entities.car;

import java.time.Duration;

/**
 * An event that simulates a reaction time of a car.
 * Such as the delay before restarting a car when we notice that the car in front has started.
 */
public class DelayedReactionEvent extends CarEvent {

    private Duration delay;

    public DelayedReactionEvent(Car car, Duration delay) {
        super(car, car.getSimEngine().getCurrentSimTime().plus(delay), car.getName() + " reacts with a delay");
        this.delay = delay;
    }

    @Override
    public void doAction() {
        super.doAction();
        car.update();
    }
}
