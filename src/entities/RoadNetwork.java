package entities;

import entities.intersection.Intersection;
import entities.traffic_signs.TrafficLight;
import entities.traffic_signs.TrafficSign;
import entities.zone.Zone;
import graph_network.Graph;
import graph_network.Node;
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

    /**
     * Returns the lane between two nodes of the road network
     *
     * @param start start node
     * @param end   end node
     * @return the lane that connects both
     */
    public Lane getLaneBetween(Node start, Node end) {
        for (Road road : roads) {
            if (road.connects(start, end)) {
                return road.getLaneWithSource(start);
            }
        }
        throw new NoSuchFieldError("no lane between " + start.toString() + "and " + end.toString());
    }

    /**
     * Initiate all zones and traffic lights
     */
    @Override
    public void init() {
        initZones();
        initTrafficLights();
        // no need to init stops, they only create events when cars arrive
    }

    /**
     * Loops through all roads to get the traffic lights and init them
     */
    protected void initTrafficLights() {
        for (Road road : roads) {
            // traffic light on lane 1
            TrafficSign tfs = road.getLane1().getTrafficSign();
            if (tfs instanceof TrafficLight) tfs.init();
            // traffic light on lane 2
            tfs = road.getLane2().getTrafficSign();
            if (tfs instanceof TrafficLight) tfs.init();
        }
    }

    /**
     * Loops through all zones and init them
     */
    protected void initZones() {
        for (Zone zone : zones) {
            zone.init();
        }
    }

    @Override
    public void logStats() {

    }

    @Override
    public String getName() {
        return null;
    }

    public SimEngine getSimEngine() {
        return simEngine;
    }

    public List<Road> getRoads() {
        return roads;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }
}
