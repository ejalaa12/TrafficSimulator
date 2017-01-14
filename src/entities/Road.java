package entities;

import graph_network.Node;

/**
 * A Road consist of two lanes going in opposite direction
 */
public class Road {

    private Lane lane1, lane2;
    private Node connectedNode1, connectedNode2;
    private String id;
    private int length;
    private double speed_limit;

    public Road(String id, Node node1, Node node2, int length, double speed_limit) {
        lane1 = new Lane(id+"a", node1, node2, length, speed_limit);
        lane2 = new Lane(id+"b", node2, node1, length, speed_limit);
        connectedNode1 = node1;
        connectedNode2 = node2;
        this.length = length;
        this.speed_limit = speed_limit;
    }

    public boolean connects(Node node1, Node node2) {
        return (node1 == connectedNode1 && node2 == connectedNode2) || (node2 == connectedNode1 && node1 == connectedNode2);
    }

    /*
    * ****************************************************************************************************************
    * GETTER AND SETTER
    * ****************************************************************************************************************
    */

    public Lane getLane1() {
        return lane1;
    }

    public Lane getLane2() {
        return lane2;
    }

    public Lane getLaneWithDestination(Node node) {
        if (node != connectedNode1 && node != connectedNode2) {
            String error = String.format("This road (%s) is not connected to the node (%s)", id, node.getName());
            throw new IllegalArgumentException(error
            );
        }
        return lane1.getDestination().equals(node) ? lane1 : lane2;
    }

    public Lane getLaneWithSource(Node node) {
        return lane1.getSource().equals(node) ? lane1 : lane2;
    }
}
