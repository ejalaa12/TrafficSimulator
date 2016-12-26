package entities;

import entities.area.Area;
import graph_network.DijkstraAlgorithm;
import graph_network.Node;
import simulation.SimEngine;

import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Created by ejalaa on 25/12/2016.
 */
public class DemoRoads {


    public static void main(String args[]) {
        demo();
    }

    public static void demo() {
        RoadGraph roadGraph = new RoadGraph();
        LocalDateTime startSim = LocalDateTime.of(2000, 1, 1, 10, 20);
        LocalDateTime endSim = LocalDateTime.of(2000, 1, 1, 10, 40);
        SimEngine simEngine = new SimEngine(1, startSim, endSim);

        Area zone1 = new Area("zone1", simEngine);
        Area zone2 = new Area("zone2", simEngine);
        Area zone3 = new Area("zone3", simEngine);
        Area zone4 = new Area("zone4", simEngine);
        Area zone5 = new Area("zone5", simEngine);
        Area zone6 = new Area("zone6", simEngine);
        Area zone7 = new Area("zone7", simEngine);

        Intersection intersection1 = new Intersection("intersection1");
        Intersection intersection2 = new Intersection("intersection2");
        Intersection intersection3 = new Intersection("intersection3");
        Intersection intersection4 = new Intersection("intersection4");

        roadGraph.addArea(zone1);
        roadGraph.addArea(zone2);
        roadGraph.addArea(zone3);
        roadGraph.addArea(zone4);
        roadGraph.addArea(zone5);
        roadGraph.addArea(zone6);
        roadGraph.addArea(zone7);
        roadGraph.addIntersection(intersection1);
        roadGraph.addIntersection(intersection2);
        roadGraph.addIntersection(intersection3);
        roadGraph.addIntersection(intersection4);


        roadGraph.addRoad(new Road("R1", zone1, intersection1, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R2", intersection1, intersection2, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R3", intersection1, intersection4, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R4", zone2, intersection2, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R5", zone3, intersection2, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R6", zone5, intersection4, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R7", zone6, intersection4, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R8", zone7, intersection3, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R9", zone4, intersection3, 100, 50*1000/3600));
        roadGraph.addRoad(new Road("R10", intersection4, intersection3, 100, 50*1000/3600));


        System.out.println("Results: zone1 to zone7");
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(roadGraph);
        dijkstra.execute(zone2);
        LinkedList<Node> path = dijkstra.getPath(zone7);

        if (path == null) {
            System.out.println("Path not found");
        }
        for (Node node : path) {
            System.out.println(node);
        }
    }

}
