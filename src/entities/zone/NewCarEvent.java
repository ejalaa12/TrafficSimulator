package entities.zone;

import entities.car.Car;
import simulation.Event;

import java.time.LocalDateTime;

/**
 * A New Car Event consist of creating a new Car Entity, and adding it to the simEngine
 */
public class NewCarEvent extends Event {

    private Zone zone;
    private Car createdCar;

    NewCarEvent(Zone zone, LocalDateTime scheduledTime) {
        super(zone.getName(), scheduledTime, "Creation of new car");
        this.zone = zone;
        // Update description with car name
        String description = "Creation of ";
        String carName = "Car" + zone.getName() + "-";
        carName += zone.getNumberOfProducedCars();
        description += carName;
        setDescription(description);
        // Create the car
        createdCar = new Car(carName, zone, zone.getPreferedDestination(), zone.getRoadNetwork());
    }

    @Override
    public void doAction() {
        createdCar.init();
        zone.setNumberOfProducedCars(zone.getNumberOfProducedCars() + 1);
        // next car event
        LocalDateTime nextCarEvent = zone.getSimEngine().getCurrentSimTime();
        nextCarEvent = nextCarEvent.plus(zone.getZoneSchedule().getCurrentFrequency(nextCarEvent.toLocalTime()));
        zone.getSimEngine().addEvent(new NewCarEvent(zone, nextCarEvent));
    }
}
