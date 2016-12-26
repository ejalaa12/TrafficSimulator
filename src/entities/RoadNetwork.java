package entities;

import entities.zone.Zone;
import entities.zone.ZonePreferences;
import graph_network.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class RoadNetwork extends Graph {

    private List<Road> roads;
    private List<Zone> zones;
    private List<Intersection> intersections;
    private ZonePreferences zonePreferences;

    public RoadNetwork() {
        super();
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


    public Zone getRandomZone() {
        return null;
    }

    /**
     *
     * @param i the index of the zone to get
     * @return the zone at the specified index
     */
    public Zone getZone(int i) {
        return zones.get(i);
    }
}
