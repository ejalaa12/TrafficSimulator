package entities.lane;

import entities.car.Car;
import logging.Logger;

import java.util.List;

/**
 * Created by ejalaa on 17/01/2017.
 */
public class LaneStats {


    public static void log(Lane lane) {
        List<Car> carQueue = lane.getCarQueue();
        int stoppedCars = countStoppedCars(carQueue);
        int drivingCars = countDrivingCars(carQueue);
        String message = String.format("%d, %d, %s", stoppedCars, drivingCars, lane.getState());
        if (lane.getState() == Lane.LaneState.full)
            Logger.getInstance().logStat(lane.getId(), message);
        Logger.getInstance().logStat(lane.getId(), message);
        if (carQueue.size() > 0.1 * lane.maxQueue()) Logger.getInstance().logStat(lane.getId(), "bloque !");
    }

    private static int countStoppedCars(List<Car> carQueue) {
        int total = 0;
        for (Car car : carQueue) {
            if (car.isStopped()) total += 1;
        }
        return total;
    }

    private static int countDrivingCars(List<Car> carQueue) {
        int total = 0;
        for (Car car : carQueue) {
            if (car.isDriving()) total += 1;
        }
        return total;
    }

}
