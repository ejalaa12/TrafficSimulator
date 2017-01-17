package entities.traffic_signs;

import entities.car.Car;
import logging.Logger;
import simulation.Entity;

/**
 * Created by ejalaa on 04/01/2017.
 */
public abstract class TrafficSign implements Entity {

    protected Car waitingCar;

    public void registerCar(Car car) {
        if (waitingCar == car) {
            Logger.getInstance().logInfo(getName(), car.getName() + " was waiting...");
        } else if (waitingCar != null) {
            String msg = String.format("%s was already waiting, and you try to add %s", waitingCar.getName(), car.getName());
            Logger.getInstance().logWarning(getName(), msg);
            throw new IllegalStateException("A car was already waiting and you tried to register another one... not supposed to happen");
        } else {
            Logger.getInstance().logInfo(getName(), car.getName() + " registered...");
        }
        waitingCar = car;
    }

    public void unregisterCar(Car car) {
        if (waitingCar == null) {
            throw new IllegalStateException("No car was registered, how can we unregister any ?...");
        } else if (waitingCar != car) {
            throw new IllegalArgumentException("Trying to unregister a car different from the existing one....");
        } else {
            Logger.getInstance().logInfo(getName(), car.getName() + " unregistered...");
            waitingCar = null;
        }
    }

    public Car getWaitingCar() {
        return waitingCar;
    }
}
