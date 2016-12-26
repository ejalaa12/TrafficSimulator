package graph_network;

import java.util.*;

/**
 * This class models a simple undirected graph
 */
public class Graph {
    private final List<Node> nodes;
    private final List<Edge> edges;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource() == node) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    public List<Edge> getConnections(Node node) {
        List<Edge> connections = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource() == node || edge.getDestination() == node) {
                connections.add(edge);
            }
        }
        return connections;
    }

}
