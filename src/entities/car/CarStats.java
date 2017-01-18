package entities.car;

import logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by ejalaa on 18/01/2017.
 */
public class CarStats {

    public LocalDateTime creation, arrival;
    public Duration averageStopDuration;
    private Car car;
    private ArrayList<Duration> stopDurations;

    private LocalDateTime stopTime, restartTime;

    public CarStats(Car car) {
        this.car = car;
        stopDurations = new ArrayList<>();
        averageStopDuration = Duration.ZERO;
    }

    public void log() {
        String message = String.format("Total travel time %s", Duration.between(creation, arrival));
//        Logger.getInstance().logSpecial(car.getName(), message);
        for (Duration d :
                stopDurations) {
            averageStopDuration = averageStopDuration.plus(d);
        }
        if (stopDurations.size() > 0) {
            averageStopDuration.dividedBy(stopDurations.size());
        }
        String message2 = String.format("Average stop time %s", stopDurations);
//        String message2 = String.format("Average stop time %s", averageStopDuration);
        Logger.getInstance().logSpecial(car.getName(), message2);
    }

    public void addNewStopTime() {
        stopTime = car.getSimEngine().getCurrentSimTime();
    }

    public void addNewRestartTime() {
        restartTime = car.getSimEngine().getCurrentSimTime();
        stopDurations.add(Duration.between(stopTime, restartTime));
        // reset stop time
        stopTime = null;
    }
}
