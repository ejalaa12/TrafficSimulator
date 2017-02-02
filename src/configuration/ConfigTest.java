package configuration;

import entities.RoadNetwork;
import entities.zone.Zone;
import logging.LogLevel;
import logging.Logger;
import org.yaml.snakeyaml.Yaml;
import simulation.SimEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * A main to test loading a YAML configuration file and build a network from it.
 */
public class ConfigTest {

    public static void main(String args[]) throws FileNotFoundException {
        NetworkConfiguration netconf = null;
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(new File("./src/configuration/coruscant_bis.yml"));
        netconf = yaml.loadAs(input, NetworkConfiguration.class);
        System.out.println(netconf);

        /*
        * ##############################################################################################################
        * Simulation
        * ##############################################################################################################
        */
        Logger.getInstance().setLogLevel(LogLevel.EVENT);
        Logger.getInstance().turnOff();
        Logger.getInstance().turnCsvOn();
        LocalDateTime startSim = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime endSim = LocalDateTime.of(2000, 1, 2, 0, 3);

        SimEngine simEngine = new SimEngine(1, startSim, endSim);
        RoadNetwork network = netconf.buildNetworkFromConfig(simEngine);
        network.init();
        simEngine.loop();
        Logger.getInstance().close();

        System.out.println("Total Produced cars");
        for (Zone zone : network.getZones()) {
            System.out.println(String.format("%s: %d", zone.getName(), zone.getStats().numberOfProducedCars));
        }
        System.out.println(String.format("Number of events: %d", simEngine.getLoops()));
    }
}
