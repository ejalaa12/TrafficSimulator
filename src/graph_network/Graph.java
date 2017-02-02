package graph_network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Edge> getConnectionsThatArriveAt(Node node) {
        List<Edge> connections = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getDestination() == node) {
                connections.add(edge);
            }
        }
        return connections;
    }

    public List<Edge> getConnectionsThatGoFrom(Node node) {
        List<Edge> connections = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource() == node) {
                connections.add(edge);
            }
        }
        return connections;
    }


    public Node getNodeFromName(String name) {
        return nodes.stream().filter(x -> Objects.equals(x.getName(), name)).findFirst().orElse(null);
    }
}
