package entities.zone;

import entities.RoadNetwork;
import entities.car.Car;
import entities.car.CarState;
import graph_network.Node;
import logging.LogLevel;
import logging.Logger;
import simulation.Entity;
import simulation.SimEngine;

import java.time.Duration;


/**
 * Created by ejalaa on 25/12/2016.
 */
public class Zone extends Node implements Entity{

    private ZoneStats stats;

    private SimEngine simEngine;
    private ZoneSchedule zoneSchedule;
    private ZonePreference zonePreference;
    private RoadNetwork roadNetwork;

    public Zone(String id, ZoneSchedule zoneSchedule, SimEngine simEngine, RoadNetwork roadNetwork) {
        super(id);
        this.zoneSchedule = zoneSchedule;
        this.simEngine = simEngine;
        this.roadNetwork = roadNetwork;
        stats = new ZoneStats();
    }


    @Override
    public void init() {
        if (zonePreference == null) {
            throw new IllegalStateException("zonePreferences was not set");
        }
        // time of the first car depends on the frequency
        // (we offset it by half so that the last car is still produced in the correct time period
        Duration firstCarOffset = getZoneSchedule().getCurrentFrequency(simEngine.getCurrentSimTime().toLocalTime()).dividedBy(2);
        simEngine.addEvent(new NewCarEvent(this, simEngine.getCurrentSimTime().plus(firstCarOffset)));
    }

    @Override
    public void logStats() {
        String string = String.format("%-20s: %d", getName(), getNumberOfProducedCars());
        System.out.println(string);

    }

    /*
    * ****************************************************************************************************************
    * Getter and Setters
    * ****************************************************************************************************************
    */

    public int getNumberOfProducedCars() {
        return stats.numberOfProducedCars;
    }

    public void setNumberOfProducedCars(int numberOfProducedCars) {
        this.stats.numberOfProducedCars = numberOfProducedCars;
    }

    public int getNumberOfCarArrived() {
        return stats.numberOfCarArrived;
    }

    public SimEngine getSimEngine() {
        return simEngine;
    }

    public ZoneSchedule getZoneSchedule() {
        return zoneSchedule;
    }

    public ZonePreference getZonePreference() {
        return zonePreference;
    }

    public void setZonePreference(ZonePreference zonePreference) {
        this.zonePreference = zonePreference;
    }

    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }

    public void addNewArrivedCar(Car car) {
        car.getCurrentLane().removeCar(car);
        car.stop();
        if (this == car.getDestination()) {
            Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "New Car arrived " + car.getName(), LogLevel.EVENT);
        } else {
            Logger.getInstance().logWarning(getName(), "New Car arrived " + car.getName() + " but not correct destination");
        }
        stats.numberOfCarArrived += 1;
        stats.totalDistanceTravelledByAllCars += car.getTotalTravelledDistance();
        stats.addCarFromZone(car.getSource());
        car.setCarState(CarState.ARRIVED);
    }

    public void addDismissedCar(Car car) {
        stats.numberOfDismissedCar += 1;
    }

    public int getNumberOfDismissedCar() {
        return stats.numberOfDismissedCar;
    }

    public ZoneStats getStats() {
        return stats;
    }
}
