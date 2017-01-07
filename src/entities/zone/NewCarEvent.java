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

    public NewCarEvent(Zone zone, LocalDateTime scheduledTime) {
        super(zone.getName(), scheduledTime, "Creation of new car");
        this.zone = zone;

        String carName = String.format("Car-%d (%s)", zone.getNumberOfProducedCars(), zone.getName());
        // Update description with car name
        description = "Creation of ";
        description += carName + String.format("(%s -> %s)", zone.getName(), zone.getPreferredDestination().getName());
        // Create the car
        createdCar = new Car(carName, zone, zone.getPreferredDestination(), zone.getRoadNetwork(), zone.getSimEngine());

    }

    public NewCarEvent(Zone zone, LocalDateTime scheduledTime, String carName) {
        super(zone.getName(), scheduledTime, "Creation of new car");
        this.zone = zone;

        // Update description with car name
        description = "Creation of ";
        description += carName + String.format("(%s -> %s)", zone.getName(), zone.getPreferredDestination().getName());
        // Create the car
        createdCar = new Car(carName, zone, zone.getPreferredDestination(), zone.getRoadNetwork(), zone.getSimEngine());

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
