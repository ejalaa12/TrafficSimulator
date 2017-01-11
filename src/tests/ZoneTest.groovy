package tests

import entities.CustomRoadNetwork0
import logging.LogLevel
import logging.Logger
import simulation.SimEngine

import java.time.LocalDateTime

/**
 * Test suite for testing the simulation
 */
class ZoneTest extends GroovyTestCase {

    LocalDateTime startSim
    LocalDateTime endSim
    SimEngine simEngine
    CustomRoadNetwork0 network

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        commonSimulation()
    }

    void commonSimulation() {
        startSim = LocalDateTime.of(2000, 1, 1, 0, 0)
        endSim = LocalDateTime.of(2000, 1, 2, 0, 0)
        simEngine = new SimEngine(1, startSim, endSim)
        Logger.getInstance().setLogLevel(LogLevel.WARNING)
        // Simple road network
        network = new CustomRoadNetwork0(simEngine, 1000, 10, 12)
        network.init()

        simEngine.loop()
    }

    void testSimTimes() {
        assert endSim.isAfter(startSim)
    }

    void testCarProduction() {
        assertEquals(10, network.getZone1().getNumberOfProducedCars())
        assertEquals(12, network.getZone2().getNumberOfProducedCars())
    }

    void testCarReception() {
        // because the last car didn't have time to arrived
        assertEquals(10 - 1, network.getZone2().getNumberOfCarArrived())
        assertEquals(12 - 1, network.getZone1().getNumberOfCarArrived())
    }

    void testCarTravel() {
        int totalD = network.getZone1().stats.totalDistanceTravelledByAllCars
        int totalC = network.getZone1().stats.numberOfCarArrived
        print "distance travelled by " + totalC + " cars arrived:" + totalD
        assertEquals(1000, totalD / totalC)
    }

}
