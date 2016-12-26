package entities;

import entities.zone.Zone;
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
        RoadNetwork roadNetwork = new RoadNetwork();
        LocalDateTime startSim = LocalDateTime.of(2000, 1, 1, 10, 20);
        LocalDateTime endSim = LocalDateTime.of(2000, 1, 1, 10, 40);
        SimEngine simEngine = new SimEngine(1, startSim, endSim);

        Zone zone1 = new Zone("zone1");
        Zone zone2 = new Zone("zone2");
        Zone zone3 = new Zone("zone3");
        Zone zone4 = new Zone("zone4");
        Zone zone5 = new Zone("zone5");
        Zone zone6 = new Zone("zone6");
        Zone zone7 = new Zone("zone7");

        Intersection intersection1 = new Intersection("intersection1");
        Intersection intersection2 = new Intersection("intersection2");
        Intersection intersection3 = new Intersection("intersection3");
        Intersection intersection4 = new Intersection("intersection4");

        roadNetwork.addArea(zone1);
        roadNetwork.addArea(zone2);
        roadNetwork.addArea(zone3);
        roadNetwork.addArea(zone4);
        roadNetwork.addArea(zone5);
        roadNetwork.addArea(zone6);
        roadNetwork.addArea(zone7);
        roadNetwork.addIntersection(intersection1);
        roadNetwork.addIntersection(intersection2);
        roadNetwork.addIntersection(intersection3);
        roadNetwork.addIntersection(intersection4);


        roadNetwork.addRoad(new Road("R1", zone1, intersection1, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R2", intersection1, intersection2, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R3", intersection1, intersection4, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R4", zone2, intersection2, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R5", zone3, intersection2, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R6", zone5, intersection4, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R7", zone6, intersection4, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R8", zone7, intersection3, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R9", zone4, intersection3, 100, 50*1000/3600));
        roadNetwork.addRoad(new Road("R10", intersection4, intersection3, 100, 50*1000/3600));


        System.out.println("Results: zone2 to zone7");
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(roadNetwork);
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
