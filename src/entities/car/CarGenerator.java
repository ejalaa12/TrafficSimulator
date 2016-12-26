package entities.car;

import entities.RoadNetwork;
import simulation.Entity;
import simulation.SimEngine;

/**
 * Created by ejalaa on 26/12/2016.
 */
public class CarGenerator implements Entity {

    private SimEngine simEngine;
    private RoadNetwork roadNetwork;
    private int numberOfCarGenerated;

    public CarGenerator(SimEngine simEngine, RoadNetwork roadNetwork) {
        this.simEngine = simEngine;
        this.roadNetwork = roadNetwork;
        this.numberOfCarGenerated = 0;
    }

    @Override
    public void init() {
        simEngine.addEvent(new NewCarEvent(this, simEngine.getCurrentSimTime(), roadNetwork));
    }

    @Override
    public void printStats() {

    }

    @Override
    public String getName() {
        return "Car Generator";
    }

    public int getNumberOfCarGenerated() {
        return numberOfCarGenerated;
    }

    public void setNumberOfCarGenerated(int numberOfCarGenerated) {
        this.numberOfCarGenerated = numberOfCarGenerated;
    }
}
