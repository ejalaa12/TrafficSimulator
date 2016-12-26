package graph_network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Michael Levet
 * @date June 09, 2015
 */
public class DemoGraph {

    private Graph graph = new Graph();

    private void addDoubleEdge(String laneId, Node node1, Node node2, int length) {
        Edge lane1 = new Edge(laneId+"a", node1, node2, length);
        Edge lane2 = new Edge(laneId+"b", node2, node1, length);
        graph.addEdge(lane1);
        graph.addEdge(lane2);
    }

    public static void main(String[] args) {
        DemoGraph dg = new DemoGraph();
        dg.demo();
    }

    public void demo() {

        Node zone1 = new Node("zone1");
        Node zone2 = new Node("zone2");
        Node zone3 = new Node("zone3");
        Node zone4 = new Node("zone4");
        Node zone5 = new Node("zone5");
        Node zone6 = new Node("zone6");
        Node zone7 = new Node("zone7");

        Node intersection1 = new Node("intersection1");
        Node intersection2 = new Node("intersection2");
        Node intersection3 = new Node("intersection3");
        Node intersection4 = new Node("intersection4");

        graph.addNode(zone1);
        graph.addNode(zone2);
        graph.addNode(zone3);
        graph.addNode(zone4);
        graph.addNode(zone5);
        graph.addNode(zone6);
        graph.addNode(zone7);
        graph.addNode(intersection1);
        graph.addNode(intersection2);
        graph.addNode(intersection3);
        graph.addNode(intersection4);

        addDoubleEdge("R1", zone1, intersection1, 100);
        addDoubleEdge("R2", intersection1, intersection2, 100);
        addDoubleEdge("R3", intersection1, intersection4, 100);
        addDoubleEdge("R4", zone2, intersection2, 100);
        addDoubleEdge("R5", zone3, intersection2, 100);
        addDoubleEdge("R6", zone5, intersection4, 100);
        addDoubleEdge("R7", zone6, intersection4, 100);
        addDoubleEdge("R8", zone7, intersection3, 100);
        addDoubleEdge("R9", zone4, intersection3, 100);
        addDoubleEdge("R10", intersection4, intersection3, 100);

        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(zone1);
        LinkedList<Node> path = dijkstra.getPath(zone7);
        System.out.println("HHH: " + graph.getNeighbors(intersection1));

        for (Node node : path) {
            System.out.println(node);
        }
    }
}
