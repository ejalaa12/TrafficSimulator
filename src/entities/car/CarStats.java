package entities.car;

import logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * A holder of the car statistics.
 * This is used to separate the statistics methods and parameter of a car.
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

    public void addNewStopTime() {
        stopTime = car.getSimEngine().getCurrentSimTime();
    }

    public void addNewRestartTime() {
        restartTime = car.getSimEngine().getCurrentSimTime();
        Duration stopDuration = Duration.between(stopTime, restartTime);
        stopDurations.add(stopDuration);
        Logger.getInstance().logStat(car.getName(), "Stop duration", stopDuration.toString());
        car.getCurrentLane().getStats().logNewCarStoppedMoreThan30Seconds(stopDuration);
//        if (stopDuration.compareTo(Duration.ofSeconds(30)) > 0) {
//            car.getCurrentLane().getStats().logNewCarStoppedMoreThan30Seconds(stopDuration);
//        }
        // reset stop time
        stopTime = null;
    }

    public void logFinish() {
        Logger.getInstance().logStat(car.getName(), "Total Travel Time", Duration.between(creation, arrival).toString());
        // calculate average stop time 'maybe not needed'
        Duration average = Duration.ZERO;
        if (!stopDurations.isEmpty()) {
            for (Duration d : stopDurations) {
                average = average.plus(d);
            }
            average.dividedBy(stopDurations.size());
        }
        if (average.compareTo(Duration.ZERO) > 0)
            Logger.getInstance().logStat(car.getName(), "Average stop duration", String.valueOf(average));

    }
}
