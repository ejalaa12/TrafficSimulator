package entities.zone;

import entities.RoadNetwork;
import graph_network.Node;
import simulation.Entity;
import simulation.SimEngine;


/**
 * Created by ejalaa on 25/12/2016.
 */
public class Zone extends Node implements Entity{

    private int numberOfProducedCars = 0;
    private SimEngine simEngine;
    private ZoneSchedule zoneSchedule;
    // TODO: 27/12/2016 Do preferences (for the moment only one destination per zone)
    private Zone preferedDestination;
    private RoadNetwork roadNetwork;

    public Zone(String id, ZoneSchedule zoneSchedule, SimEngine simEngine, RoadNetwork roadNetwork) {
        super(id);
        this.zoneSchedule = zoneSchedule;
        this.simEngine = simEngine;
        this.roadNetwork = roadNetwork;
    }


    @Override
    public void init() {
        if (preferedDestination == null) {
            throw new IllegalStateException("preferedDestination was not set");
        }
        simEngine.addEvent(new NewCarEvent(this, simEngine.getCurrentSimTime()));
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

    public SimEngine getSimEngine() {
        return simEngine;
    }

    public ZoneSchedule getZoneSchedule() {
        return zoneSchedule;
    }

    public Zone getPreferedDestination() {
        return preferedDestination;
    }

    public void setPreferedDestination(Zone preferedDestination) {
        this.preferedDestination = preferedDestination;
    }

    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }
}
