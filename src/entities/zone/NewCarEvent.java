package entities.zone;

import entities.car.Car;
import simulation.Event;

import java.time.LocalDateTime;

/**
 * A New Car Event consist of creating a new Car Entity, and adding it to the simEngine
 */
public class NewCarEvent extends Event {

    private Zone source, destination;
    private Car createdCar;

    public NewCarEvent(Zone source, LocalDateTime scheduledTime) {
        super(source.getName(), scheduledTime, "Creation of new car");
        this.source = source;

        // car destination
        destination = source.getZonePreference().getDestination();
        //
        String carName = String.format("Car-%d (%s)", source.getNumberOfProducedCars(), source.getName());
        // Update description with car name
        description = "Creation of ";
        description += carName + String.format("(%s -> %s)", source.getName(), destination.getName());
        // Create the car
        createdCar = new Car(carName, source, destination, source.getRoadNetwork(), source.getSimEngine());

    }

    public NewCarEvent(Zone source, LocalDateTime scheduledTime, String carName) {
        super(source.getName(), scheduledTime, "Creation of new car");
        this.source = source;

        // car destination
        destination = source.getZonePreference().getDestination();
        // Update description with car name
        description = "Creation of ";
        description += carName + String.format("(%s -> %s)", source.getName(), destination.getName());
        // Create the car
        createdCar = new Car(carName, source, destination, source.getRoadNetwork(), source.getSimEngine());

    }

    @Override
    public void doAction() {
        createdCar.init();
        source.setNumberOfProducedCars(source.getNumberOfProducedCars() + 1);
        // next car event
        LocalDateTime nextCarEvent = source.getSimEngine().getCurrentSimTime();
        nextCarEvent = nextCarEvent.plus(source.getZoneSchedule().getCurrentFrequency(nextCarEvent.toLocalTime()));
        source.getSimEngine().addEvent(new NewCarEvent(source, nextCarEvent));
    }

    public Car getCreatedCar() {
        return createdCar;
    }
}
