import entities.CoruscantNetwork;
import entities.car.CarGenerator;
import simulation.SimEngine;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        /*
        * ****************************************************************************************************************
        * Simulation times
        * ****************************************************************************************************************
        */
        LocalDateTime startSim = LocalDateTime.of(2000, 1, 1, 10, 00);
        LocalDateTime endSim = LocalDateTime.of(2000, 1, 1, 10, 40);

        SimEngine simEngine = new SimEngine(1, startSim, endSim);

        /*
        * ****************************************************************************************************************
        * Carrefour Coruscant
        * ****************************************************************************************************************
        */
        CoruscantNetwork carrefour = new CoruscantNetwork();

        /*
        * ****************************************************************************************************************
        * Car generator
        * ****************************************************************************************************************
        */
        CarGenerator carGenerator = new CarGenerator(simEngine, carrefour);
        carGenerator.init();
        /*
        * ****************************************************************************************************************
        * Simulation
        * ****************************************************************************************************************
        */
        simEngine.loop();


    }
}
