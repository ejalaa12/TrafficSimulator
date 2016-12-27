package entities;

import entities.zone.Zone;
import graph_network.Graph;
import simulation.Entity;
import simulation.SimEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a road network based on a Graph.
 */
public class RoadNetwork extends Graph implements Entity {

    protected List<Road> roads;
    protected List<Zone> zones;
    protected List<Intersection> intersections;
    protected SimEngine simEngine;

    public RoadNetwork(SimEngine simEngine) {
        super();
        this.simEngine = simEngine;
        roads = new ArrayList<>();
        zones = new ArrayList<>();
        intersections = new ArrayList<>();
    }


    /**
     * Add the road (and its corresponding lanes)
     * to the list of roads (and to the list of edges)
     * @param road the road to add
     */
    public void addRoad(Road road) {
        roads.add(road);
        addEdge(road.getLane1());
        addEdge(road.getLane2());
    }

    /**
     * Add the zone to the list of zones and nodes
     * @param zone the zone to add
     */
    public void addArea(Zone zone) {
        zones.add(zone);
        addNode(zone);
    }

    /**
     * Add the intersection to the list of intersection and nodes
     * @param intersection the intersection to add
     */
    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
        addNode(intersection);
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

    public SimEngine getSimEngine() {
        return simEngine;
    }
}
