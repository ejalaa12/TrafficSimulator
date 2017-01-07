import entities.SimpleNetwork;
import entities.zone.NewCarEvent;
import entities.zone.Zone;
import logging.LogLevel;
import logging.Logger;
import simulation.SimEngine;

import java.time.LocalDateTime;

public class Main {


    public static void test() {
        System.out.println(50 * 1000 / 3600.);
    }

    public static void main(String[] args) {
        Logger.getInstance().setLogLevel(LogLevel.INFO);
        /*
        * ##############################################################################################################
        * Simulation times
        * ##############################################################################################################
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
//        OneRoadNetwork crossroads = new OneRoadNetwork(simEngine,1000, 10, 10);
        crossroads.init();

        /*
        * ##############################################################################################################
        * add some entities/event to test
        * ##############################################################################################################
        */
        simEngine.addEvent(new NewCarEvent((Zone) crossroads.getNodes().get(0), LocalDateTime.of(2000, 1, 1, 0, 2, 1), "Car1"));
        simEngine.addEvent(new NewCarEvent((Zone) crossroads.getNodes().get(0), LocalDateTime.of(2000, 1, 1, 0, 2, 10), "Car2"));
//        for (int i = 0; i < 50; i++) {
//            simEngine.addEvent(new NewCarEvent((Zone) crossroads.getNodes().get(0), LocalDateTime.of(2000, 1, 1, 0, 21, 20)));
//        }
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
