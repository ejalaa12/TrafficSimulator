package entities.intersection;

import entities.car.Car;
import entities.traffic_signs.StopSign;
import simulation.Event;

/**
 * Created by ejalaa on 14/01/2017.
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
