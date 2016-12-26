package entities.car;

import entities.RoadNetwork;
import entities.zone.Zone;
import graph_network.DijkstraAlgorithm;
import graph_network.Node;
import simulation.Entity;

import java.util.LinkedList;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class Car implements Entity {

    private Zone source, destination;
    private static final int length = 4;
    private float speed;
    private LinkedList<Node> path;
    private String Id;

    public Car(String Id, Zone source, Zone destination, RoadNetwork roadNetwork) {
        this.source = source;
        this.destination = destination;
        speed = 0;
        // Calculate path
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(roadNetwork);
        dijkstraAlgorithm.execute(source);
        path = dijkstraAlgorithm.getPath(destination);
    }

    @Override
    public void init() {

    }

    @Override
    public void printStats() {

    }

    @Override
    public String getName() {
        return null;
    }
}
