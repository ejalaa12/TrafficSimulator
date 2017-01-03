import entities.SimpleNetwork;
import logging.LogLevel;
import logging.Logger;
import simulation.SimEngine;

import java.time.LocalDateTime;

public class Main {


    public static void test() {
        System.out.println(50 * 1000 / 3600.);
    }

    public static void main(String[] args) {
        test();
        Logger.getInstance().setLogLevel(LogLevel.INFO);
        Logger.getInstance().turnCsvOn();
        /*
        * ****************************************************************************************************************
        * Simulation times
        * ****************************************************************************************************************
        */
        LocalDateTime startSim = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime endSim = LocalDateTime.of(2000, 1, 1, 0, 30);

        SimEngine simEngine = new SimEngine(1, startSim, endSim);

        /*
        * ****************************************************************************************************************
        * Junction
        * ****************************************************************************************************************
        */
        SimpleNetwork crossroads = new SimpleNetwork(simEngine);
        crossroads.init();

        /*
        * ****************************************************************************************************************
        * Simulation
        * ****************************************************************************************************************
        */
        simEngine.loop();


        /*
        * ****************************************************************************************************************
        * results
        * ****************************************************************************************************************
        */
//        crossroads.logStats();

        Logger.getInstance().close();
    }
}
