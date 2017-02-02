package entities.traffic_signs;

import entities.car.Car;
import entities.lane.Lane;
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
        // add some randomness to the stop duration ±1s
        int offset = simEngine.getRandom().nextInt(2) * 2 - 1;
        stopDuration = Duration.ofSeconds(4).plusSeconds(offset);
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
