package tests

import entities.CustomRoadNetwork0
import logging.Logger
import simulation.SimEngine

import java.time.LocalDateTime

/**
 * Created by ejalaa on 06/01/2017.
 */
class CarTest extends groovy.util.GroovyTestCase {
    static LocalDateTime startSim = LocalDateTime.of(2000, 1, 1, 0, 0)
    static LocalDateTime endSim = LocalDateTime.of(2000, 1, 2, 0, 0)

    static SimEngine simEngine = new SimEngine(1, startSim, endSim)
    CustomRoadNetwork0 network = new CustomRoadNetwork0(simEngine, 1000, 10, 12)


    void test() {
        Logger.instance.turnOff()
        network.init()
        simEngine.loop()
        assert network.zone1.numberOfProducedCars == 10
//        assert network.zone2.numberOfCarArrived == 10
//
        assert network.zone2.numberOfProducedCars == 12
//        assert network.zone1.numberOfCarArrived == 12
    }

}
