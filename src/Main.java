import entities.CustomRoadNetwork0b3;
import entities.Lane;
import entities.traffic_light.ChangeColorEvent;
import entities.traffic_light.TrafficLight;
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
        LocalDateTime endSim = LocalDateTime.of(2000, 1, 2, 0, 30);

        SimEngine simEngine = new SimEngine(1, startSim, endSim);


        /*
        * ****************************************************************************************************************
        * Junction
        * ****************************************************************************************************************
        */
//        CoruscantNetwork crossroads = new CoruscantNetwork(simEngine);
//        CustomRoadNetwork1 crossroads = new CustomRoadNetwork1(simEngine);
        CustomRoadNetwork0b3 crossroads = new CustomRoadNetwork0b3(simEngine);
//        CustomRoadNetwork0 crossroads = new CustomRoadNetwork0(simEngine,1000, 10, 10);
        crossroads.init();

        /*
        * ##############################################################################################################
        * add some entities/event to test
        * ##############################################################################################################
        */
        Lane laneWithTrafficLight = crossroads.R2.getLaneWithDestination(crossroads.intersection2);
        laneWithTrafficLight.setTrafficSign(new TrafficLight(laneWithTrafficLight, simEngine));
        simEngine.addEvent(new ChangeColorEvent((TrafficLight) laneWithTrafficLight.getTrafficSign(), LocalDateTime.of(2000, 1, 1, 0, 0, 10)));
        simEngine.addEvent(new ChangeColorEvent((TrafficLight) laneWithTrafficLight.getTrafficSign(), LocalDateTime.of(2000, 1, 1, 19, 0, 0)));
//        simEngine.addEvent(new NewCarEvent((Zone) crossroads.getNodes().get(0), LocalDateTime.of(2000, 1, 1, 0, 2, 1), "Car1"));
//        simEngine.addEvent(new NewCarEvent((Zone) crossroads.getNodes().get(0), LocalDateTime.of(2000, 1, 1, 0, 2, 10), "Car2"));
//        simEngine.addEvent(new NewCarEvent((Zone) crossroads.getNodes().get(0), LocalDateTime.of(2000, 1, 1, 0, 3, 53), "Car3"));
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
