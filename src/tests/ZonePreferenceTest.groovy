package tests

import entities.CustomRoadNetwork3
import logging.LogLevel
import logging.Logger
import simulation.SimEngine

import java.time.LocalDateTime

/**
 * Created by ejalaa on 13/01/2017.
 */
class ZonePreferenceTest extends GroovyTestCase {

    LocalDateTime startSim
    LocalDateTime endSim
    SimEngine simEngine
    CustomRoadNetwork3 network

    void setUp() {
        super.setUp()
        startSim = LocalDateTime.of(2000, 1, 1, 0, 0)
        endSim = LocalDateTime.of(2000, 1, 2, 0, 0)
        simEngine = new SimEngine(1, startSim, endSim)
        Logger.getInstance().setLogLevel(LogLevel.WARNING)
        // Simple road network
        network = new CustomRoadNetwork3(simEngine)
    }

    void tearDown() {

    }

    void testAddPreference() {
//        ZonePreference zonepref = new ZonePreference(simEngine.getRandom())
//        zonepref.addPreference(network.zone1, 0.01)
    }

    void testGetDestination() {
//        ZonePreference zonepref = new ZonePreference(simEngine.getRandom())
//        zonepref.addPreference(network.zone1, 1)
//        network.zone1.setZonePreference(zonepref)
//        println zonepref.getDestination()
    }

    void testDestinationsAreWellFollowed() {
        network.init()
        simEngine.loop()


    }
}
