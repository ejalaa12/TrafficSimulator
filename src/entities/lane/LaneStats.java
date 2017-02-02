package entities.lane;

import entities.car.Car;
import logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * This class contains attributes that we use only for statistics
 *
 * we separated it from Lane to make it clearer
 */
public class LaneStats {

    // The lane that needs statistics
    private Lane lane;

    private Duration blockedDuration;
    private boolean blocked;
    private LocalDateTime blockStart, blockEnd;
    private int numberOfCarsStoppedMoreThan30Seconds;

    public LaneStats(Lane lane) {
        this.lane = lane;
        blocked = false;
        numberOfCarsStoppedMoreThan30Seconds = 0;
    }


    public void wasJustBlocked(LocalDateTime dateTime) {
        blocked = true;
        blockStart = dateTime;
    }

    public void wasJustFreed(LocalDateTime dateTime) {
        if (blocked) {
            blocked = false;
            blockEnd = dateTime;
            blockedDuration = Duration.between(blockStart, blockEnd);
            Logger.getInstance().logStat(lane.getId(), "Blocked duration", blockedDuration.toString());
        }
    }

    public void logNumberOfCars(LocalDateTime dateTime) {
        int stoppedCars;
        int drivingCars;
        stoppedCars = 0;
        drivingCars = 0;
        for (Car car : lane.getCarQueue()) {
            if (car.isDriving()) drivingCars += 1;
            if (car.isStopped()) stoppedCars += 1;
        }
        Logger.getInstance().logStat(lane.getId(), "Stopped", String.valueOf(stoppedCars));
        Logger.getInstance().logStat(lane.getId(), "Driving", String.valueOf(drivingCars));
        Logger.getInstance().logStat(lane.getId(), "Total", String.valueOf(drivingCars + stoppedCars));
        Logger.getInstance().logStat(lane.getId(), "Percentage", String.valueOf((double) (drivingCars + stoppedCars) / lane.maxQueue()));

    }

    public void logNewCarStoppedMoreThan30Seconds(Duration stopDuration) {
        numberOfCarsStoppedMoreThan30Seconds += 1;
        Logger.getInstance().logStat(lane.getId(), "A car stop duration", stopDuration.toString());
        Logger.getInstance().logStat(lane.getId(), "Number of car stopped more than 30 seconds", String.valueOf(numberOfCarsStoppedMoreThan30Seconds));

    }
}
