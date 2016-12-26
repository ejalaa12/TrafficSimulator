package entities;

import graph_network.Node;

/**
 * A Road consist of two lanes going in opposite direction
 */
public class Road {

    private Lane lane1, lane2;
    private String id;
    private int length;
    private float speed_limit;

    public Road(String id, Node node1, Node node2, int length, float speed_limit) {
        lane1 = new Lane(id+"a", node1, node2, length, speed_limit);
        lane2 = new Lane(id+"b", node2, node1, length, speed_limit);
        this.length = length;
        this.speed_limit = speed_limit;
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
}
