package entities.intersection;

import entities.car.Car;
import entities.traffic_signs.StopSign;
import simulation.Event;

/**
 * An event of waiting at a stop.
 *
 * Every time a car arrives at a stop, it must wait at least ~3 seconds before
 * trying to get into an intersection
 */
public class WaitAtStopEvent extends Event {

    private StopSign stopSign;
    private Car car;
    private Intersection intersection;

    public WaitAtStopEvent(StopSign stopSign, Intersection intersection, Car car) {
        super(car.getName(), stopSign.getSimEngine().getCurrentSimTime().plus(stopSign.getStopDuration()), "");
        this.stopSign = stopSign;
        this.car = car;
        this.intersection = intersection;
        description = "Has waited and now try again";
    }

    @Override
    public void doAction() {
        stopSign.setWaitFinish(true);
        if (intersection.tryToGetIntoIntersection(car)) {
            stopSign.setWaitFinish(false);
        }
    }
}
