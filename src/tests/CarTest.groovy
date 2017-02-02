package tests

import logging.Logger
import simulation.SimEngine
import tests.custom_crossroads.CustomRoadNetwork0

import java.time.LocalDateTime

/**
 * A tentative to test some behavior of the simulation (also learning how a testCase works).
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
