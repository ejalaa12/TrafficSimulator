package entities.traffic_signs;

import entities.Lane;
import entities.car.Car;
import simulation.SimEngine;

import java.time.Duration;

/**
 * Created by ejalaa on 13/01/2017.
 */
public class StopSign extends TrafficSign {

    private Lane lane;
    private SimEngine simEngine;
    private Duration stopDuration;
    private boolean waitFinish;

    public StopSign(Lane lane, SimEngine simEngine) {
        this.lane = lane;
        this.simEngine = simEngine;
        stopDuration = Duration.ofSeconds(3);
        waitFinish = false;
    }

    @Override
    public void init() {

    }

    @Override
    public void logStats() {

    }

    @Override
    public void unregisterCar(Car car) {
        super.unregisterCar(car);
        waitFinish = false;
    }

    @Override
    public String getName() {
        return "StopSign on " + lane.getId();
    }

    @Override
    public SimEngine getSimEngine() {
        return simEngine;
    }

    public Duration getStopDuration() {
        return stopDuration;
    }

    public boolean isWaitFinish() {
        return waitFinish;
    }

    public void setWaitFinish(boolean waitFinish) {
        this.waitFinish = waitFinish;
    }
}
