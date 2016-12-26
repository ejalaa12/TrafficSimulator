package entities;

import entities.area.Area;
import graph_network.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class RoadGraph extends Graph {

    private List<Road> roads;
    private List<Area> areas;
    private List<Intersection> intersections;

    public RoadGraph() {
        super();
        roads = new ArrayList<>();
        areas = new ArrayList<>();
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
     * Add the area to the list of areas and nodes
     * @param area the area to add
     */
    public void addArea(Area area) {
        areas.add(area);
        addNode(area);
    }

    /**
     * Add the intersection to the list of intersection and nodes
     * @param intersection the intersection to add
     */
    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
        addNode(intersection);
    }


}
