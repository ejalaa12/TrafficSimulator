package entities.car;

import entities.RoadNetwork;
import simulation.Event;

import java.time.LocalDateTime;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class NewCarEvent extends Event{

    private int frequency = 30;
    private Car newCar;

    protected NewCarEvent(CarGenerator carGenerator, LocalDateTime scheduledTime, RoadNetwork roadNetwork) {
        super(carGenerator.getName(), scheduledTime, "Producing Car: ");
        String carID = "Car" + carGenerator.getNumberOfCarGenerated();
        // new car source and destination
        newCar = new Car(carID, roadNetwork.getZone(1), roadNetwork.getZone(2), roadNetwork);
        // update event description
        String description = getDescription();
        setDescription(description + carID);
        // Update number of car generated
        carGenerator.setNumberOfCarGenerated(carGenerator.getNumberOfCarGenerated() + 1);
    }

    @Override
    public void doAction() {
        // TODO: 26/12/2016 add car to the connected road of the zone
        // TODO: 26/12/2016 check if the road queue is empty enough
        // TODO: 26/12/2016 once added, start the car to stop at the road empty spot (depends on queue)
        // once the queue is update it will notify the car to update the destination
        // Produce next event
        LocalDateTime nextCarTime = scheduledTime.plusSeconds(frequency);
//        zone.getSimEngine().addEvent(new NewCarEvent(this, zone, nextCarTime));

    }
}
