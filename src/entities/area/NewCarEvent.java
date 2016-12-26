package entities.area;

import entities.car.Car;
import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class NewCarEvent extends Event{

    private Area area;
    private int frequency = 30;
    private Car newCar;

    protected NewCarEvent(Area area, LocalDateTime scheduledTime) {
        super(area.getName(), scheduledTime, "Producing a new Car");
        this.area = area;
        String description = getDescription();
        setDescription(description + this.area.getNumberOfProducedCars());
        this.area.setNumberOfProducedCars(this.area.getNumberOfProducedCars() + 1);
        // new car source and destination
        // TODO: 26/12/2016 generate car's destination -- where ?
//        newCar = new Car(this, generateDestination())
    }

    @Override
    public void doAction() {
        // TODO: 26/12/2016 add car to the connected road of the area
        // TODO: 26/12/2016 check if the road queue is empty enough
        // TODO: 26/12/2016 once added, start the car to stop at the road empty spot (depends on queue)
        // once the queue is update it will notify the car to update the destination
        // Produce next event
        LocalDateTime nextCarTime = scheduledTime.plusSeconds(frequency);
        area.getSimEngine().addEvent(new NewCarEvent(area, nextCarTime));

    }
}
