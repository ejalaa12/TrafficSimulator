package entities.intersection;

import entities.car.Car;
import entities.lane.Lane;
import simulation.Event;

import java.time.Duration;

/**
 * An event to exit an intersection x seconds after entering it.
 */
public class ExitFromIntersectionEvent extends Event {

    private static final Duration durationInsideIntersection = Duration.ofSeconds(3);
    private Car car;
    private Intersection intersection;
    private Lane exitLane, originLane;

    public ExitFromIntersectionEvent(Intersection intersection, Car car, Lane originLane, Lane nextLane) {
        super(intersection.getName(), intersection.getSimEngine().getCurrentSimTime().plus(durationInsideIntersection), "");
        this.intersection = intersection;
        this.car = car;
        exitLane = nextLane;
        this.originLane = originLane;
        description = String.format("%s exits from intersection %s to lane: %s", car.getName(), intersection.getName(), exitLane.getId());
    }

    @Override
    public void doAction() {
        car.getOutOfIntersection(intersection, exitLane);

    }
}
