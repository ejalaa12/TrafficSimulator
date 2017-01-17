package tests.CarAlone

import logging.LogLevel
import logging.Logger
import simulation.SimEngine

import java.time.LocalDateTime

/**
 * A test suite to check that a cars works correctly
 */
class CarAloneTest extends GroovyTestCase {

    LocalDateTime startSim
    LocalDateTime endSim
    SimEngine simEngine
    private CustomCrossroad network

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        startSim = LocalDateTime.of(2000, 1, 1, 0, 0)
        endSim = LocalDateTime.of(2000, 1, 2, 0, 0)
        simEngine = new SimEngine(1, startSim, endSim)
        Logger.getInstance().setLogLevel(LogLevel.INFO)
        // Simple road network
        network = new CustomCrossroad(simEngine, 100, 1, 0)
    }

    void testCarDrive() {
        network.init()
        simEngine.loop()

        // car 0 must arrive at 13:00:10 at zone2

    }
}
