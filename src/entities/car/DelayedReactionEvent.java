package entities.car;

import java.time.Duration;

/**
 * Created by ejalaa on 07/01/2017.
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
