package entities.zone;

import entities.RoadNetwork;
import entities.car.Car;
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

    private int numberOfProducedCars = 0;
    private int numberOfCarArrived = 0;
    private int numberOfDismissedCar = 0;
    private SimEngine simEngine;
    private ZoneSchedule zoneSchedule;
    // TODO: 27/12/2016 Do preferences (for the moment only one destination per zone)
    private Zone preferredDestination;
    private RoadNetwork roadNetwork;

    public Zone(String id, ZoneSchedule zoneSchedule, SimEngine simEngine, RoadNetwork roadNetwork) {
        super(id);
        this.zoneSchedule = zoneSchedule;
        this.simEngine = simEngine;
        this.roadNetwork = roadNetwork;
    }


    @Override
    public void init() {
        if (preferredDestination == null) {
            throw new IllegalStateException("preferredDestination was not set");
        }
        // time of the first car depends on the frequency
        Duration firstCarOffset = getZoneSchedule().getCurrentFrequency(simEngine.getCurrentSimTime().toLocalTime());
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
        return numberOfProducedCars;
    }

    public void setNumberOfProducedCars(int numberOfProducedCars) {
        this.numberOfProducedCars = numberOfProducedCars;
    }

    public int getNumberOfCarArrived() {
        return numberOfCarArrived;
    }

    public SimEngine getSimEngine() {
        return simEngine;
    }

    public ZoneSchedule getZoneSchedule() {
        return zoneSchedule;
    }

    public Zone getPreferredDestination() {
        return preferredDestination;
    }

    public void setPreferredDestination(Zone preferredDestination) {
        this.preferredDestination = preferredDestination;
    }

    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }

    public void addNewArrivedCar(Car car) {
        Logger.getInstance().log(getName(), simEngine.getCurrentSimTime(), "New Car arrived " + car.getName(), LogLevel.INFO);
        numberOfCarArrived += 1;
    }

    public void addDismissedCar(Car car) {
        numberOfDismissedCar += 1;
    }

    public int getNumberOfDismissedCar() {
        return numberOfDismissedCar;
    }
}
