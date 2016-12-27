package entities.car;

import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 27/12/2016.
 */
public class ChangingLaneEvent extends Event {

    private Car car;

    protected ChangingLaneEvent(Car car, LocalDateTime scheduledTime) {
        super(car.getName(), scheduledTime, "Changing Lane");
        this.car = car;
        // If arrived changed the Event description
        if (car.getStep() == car.getPath().size() - 1) {
            setDescription("Changing Lane > Car arrived");
        }
    }

    @Override
    public void doAction() {
        car.nextStep();
    }
}
