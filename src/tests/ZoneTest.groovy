package tests

import entities.CustomRoadNetwork3
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
    CustomRoadNetwork3 network

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        commonSimulation()
    }

    void commonSimulation() {
        startSim = LocalDateTime.of(2000, 1, 1, 0, 0)
        endSim = LocalDateTime.of(2000, 1, 2, 0, 0)
        simEngine = new SimEngine(1, startSim, endSim)
        Logger.getInstance().setLogLevel(LogLevel.DEBUG)
        // Simple road network
//        network = new CustomRoadNetwork0(simEngine, 1000, 10, 12)
        network = new CustomRoadNetwork3(simEngine)
        network.init()

        simEngine.loop()
    }

    void testSimTimes() {
        assert endSim.isAfter(startSim)
    }

    void testCarProduction() {
        assertEquals(2, network.zone1.getNumberOfProducedCars())
        assertEquals(4, network.zone2.getNumberOfProducedCars())
        assertEquals(6, network.zone3.getNumberOfProducedCars())
    }

    void testCarReception() {
        int totalCarProduced = network.zone1.getNumberOfProducedCars()
        totalCarProduced += network.zone2.getNumberOfProducedCars()
        totalCarProduced += network.zone3.getNumberOfProducedCars()
        int totalCarArrived = network.zone1.getNumberOfCarArrived()
        totalCarArrived += network.zone2.getNumberOfCarArrived()
        totalCarArrived += network.zone3.getNumberOfCarArrived()

        // because the last car didn't have time to arrived
        assertEquals(totalCarProduced, totalCarArrived)
    }

    void testCarTravel() {
        int totalD = network.zone1.stats.totalDistanceTravelledByAllCars
        int totalC = network.zone1.stats.numberOfCarArrived
        print "distance travelled by " + totalC + " cars arrived:" + totalD
        assertEquals(400, totalD / totalC)
    }

}
