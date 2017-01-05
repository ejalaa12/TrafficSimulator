package entities.car;

import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 27/12/2016.
 */
public class ChangingLaneEvent extends Event {

    private Car car;

    public ChangingLaneEvent(Car car, LocalDateTime scheduledTime) {
        super(car.getName(), scheduledTime, "Changing Lane");
        this.car = car;
    }

    @Override
    public void doAction() {
        car.goToNextLane();
    }
}
