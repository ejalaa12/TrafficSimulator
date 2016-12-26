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

    public Zone(String id) {
        super(id);
    }


    @Override
    public void init() {
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


}
