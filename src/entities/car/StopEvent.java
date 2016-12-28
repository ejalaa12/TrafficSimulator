package entities.car;

import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 27/12/2016.
 */
public class StopEvent extends Event {

    private Car car;

    public StopEvent(Car car, LocalDateTime scheduledTime) {
        super(car.getName(), scheduledTime, "Stops");
        this.car = car;
    }

    @Override
    public void doAction() {
        car.stop();
    }
}
