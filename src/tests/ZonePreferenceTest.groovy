package tests

import entities.zone.Zone
import logging.LogLevel
import logging.Logger
import simulation.SimEngine
import tests.custom_crossroads.CustomRoadNetwork3

import java.time.LocalDateTime

/**
 * A test suite to test that the correct number of cars are created.
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
        Logger.getInstance().setLogLevel(LogLevel.INFO)
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
        HashMap<Zone, Integer> total = new HashMap<>()
        Zone[] zones = [network.zone1, network.zone2, network.zone3]
        HashMap<Zone, Integer>[] stats = [network.zone1.stats.numberOfCarFromEachZone, network.zone2.stats.numberOfCarFromEachZone, network.zone3.stats.numberOfCarFromEachZone]
        for (Zone z : zones) {
            for (HashMap<Zone, Integer> stat : stats) {
                int tmp = 0
                if (stat.containsKey(z)) {
                    tmp = stat.get(z)
                }
                if (total.containsKey(z)) {
                    tmp += total.get(z)
                }
                total.put(z, tmp)
            }
        }
        println String.format("zone1 produced %s cars", network.zone1.getNumberOfProducedCars())
        println String.format("zone2 produced %s cars", network.zone2.getNumberOfProducedCars())
        println String.format("zone3 produced %s cars", network.zone3.getNumberOfProducedCars())
        println String.format("zone1 received %s cars", network.zone1.numberOfCarArrived)
        println String.format("zone2 received %s cars", network.zone2.numberOfCarArrived)
        println String.format("zone3 received %s cars", network.zone3.numberOfCarArrived)

        assertEquals(total.get(network.zone1), network.zone1.getNumberOfProducedCars())
        assertEquals(total.get(network.zone2), network.zone2.getNumberOfProducedCars())
        assertEquals(total.get(network.zone3), network.zone3.getNumberOfProducedCars())

    }
}
