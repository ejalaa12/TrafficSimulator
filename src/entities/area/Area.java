package entities.area;

import graph_network.Node;
import simulation.Entity;
import simulation.SimEngine;


/**
 * Created by ejalaa on 25/12/2016.
 */
public class Area extends Node implements Entity{

    private SimEngine simEngine;
    private int numberOfProducedCars = 0;

    public Area(String id, SimEngine simEngine) {
        super(id);
        this.simEngine = simEngine;
    }


    @Override
    public void init() {
        simEngine.addEvent(new NewCarEvent(this, simEngine.getCurrentSimTime()));
    }

    @Override
    public void printStats() {

    }

    // TODO: 26/12/2016 scheduling car creation events

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

}
