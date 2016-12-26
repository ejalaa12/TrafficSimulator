package entities;

import entities.area.Area;
import entities.car.Car;
import graph_network.Edge;
import graph_network.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a Lane which is an edge of the road network
 * A Lane has a list of car and a speed_limit
 */
public class Lane extends Edge{

    private List<Car> carQueue;
    private float speed_limit;

    /**
     *
     * @param id the lane unique id
     * @param source source of the lane
     * @param destination destination of the lane
     * @param length lane's length
     */
    public Lane(String id, Node source, Node destination, int length, float speed_limit) {
        super(id, source, destination, length);
        carQueue = new ArrayList<>();
        this.speed_limit = speed_limit;
    }

    /*
    * ****************************************************************************************************************
    * GETTER AND SETTER
    * ****************************************************************************************************************
    */

}
