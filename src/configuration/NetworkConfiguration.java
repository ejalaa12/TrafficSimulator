package configuration;

import entities.RoadNetwork;
import entities.intersection.Intersection;
import entities.lane.Lane;
import entities.lane.Road;
import entities.traffic_signs.StopSign;
import entities.traffic_signs.TrafficLight;
import entities.zone.Zone;
import entities.zone.ZonePreference;
import simulation.SimEngine;

import java.util.ArrayList;

/**
 * This class is used by SnakeYaml to load and parse a Yaml file into a RoadNetwork object.
 */
public class NetworkConfiguration {

    public String name;
    public ArrayList<ZoneCfg> zones;
    public ArrayList<RoadCfg> roads;
    public ArrayList<Integer> periods;
    public ArrayList<String> intersections;
    public ArrayList<TrafficSignCfg> signs;

    public RoadNetwork buildNetworkFromConfig(SimEngine simEngine) {
        RoadNetwork network = new RoadNetwork(simEngine);
        // Create zones
        for (ZoneCfg zoneCfg : zones) {
            Zone tmp = Zone.fromConfig(zoneCfg, periods, simEngine, network);
            network.addArea(tmp);
        }
        // Set zones preferences from zonecfg
        for (Zone zone : network.getZones()) {
            ZonePreference zonePref = new ZonePreference(simEngine.getRandom());
            for (PreferenceCfg pref : zone.getCfg().preferences) {
                zonePref.addPreference(network.getZoneFromName(pref.zone), pref.attraction);
            }
            zone.setZonePreference(zonePref);
        }
        // Create intersection
        for (String intersection : intersections) {
            Intersection i = new Intersection(intersection, simEngine);
            network.addIntersection(i);
        }
        // Create roads
        for (RoadCfg cfg : roads) {
            Road r = new Road(cfg.name, network.getNodeFromName(cfg.z1), network.getNodeFromName(cfg.z2), cfg.length, cfg.getSpeed_limitInMeterPerSec());
            network.addRoad(r);
        }
        for (TrafficSignCfg signCfg : signs) {
            Lane lane = network.getRoadByName(signCfg.road).getLaneWithDestination(network.getNodeFromName(signCfg.intersection));
            switch (signCfg.type) {
                case "Stop":
                    StopSign stop = new StopSign(lane, simEngine);
                    lane.setTrafficSign(stop);
                    break;
                case "TrafficLight":
                    TrafficLight trafficLight = new TrafficLight(lane, simEngine);
                    lane.setTrafficSign(trafficLight);
                    break;
            }
        }
        return network;
    }

    @Override
    public String toString() {
        String title = "Network Configuration: " + name;
        title += "\n-----------------------\n";
        String zones_str = "Zones:\n";
        for (ZoneCfg zone :
                zones) {
            zones_str += " - " + zone.toString() + "\n";
        }
        return title + zones_str;
    }

    /*
    * ##############################################################################################################
    * Class to hold configurations
    * ##############################################################################################################
    */

    /**
     * A Holder for the Traffic sign configuration.
     */
    public static class TrafficSignCfg {

        public String type;
        public String road;
        public String intersection;
    }

    /**
     * A holder for the Zone configuration.
     */
    public static class ZoneCfg {
        public String name;
        public ArrayList<PreferenceCfg> preferences;
        public ArrayList<Integer> production;

        @Override
        public String toString() {
            int total_real_production = 0;
            for (int prod :
                    production) {
                total_real_production += prod;
            }
            String str = String.format("%s(%d) :", name, total_real_production);
            String r = "Preferences: ";
            for (PreferenceCfg pref :
                    preferences) {
                r += pref.toString();
            }
            return str + r;
        }

    }

    /**
     * A Holder for a Road configuration
     */
    public static class RoadCfg {
        public String name;
        public String z1, z2;
        public int length;
        public double speed_limit;

        @Override
        public String toString() {
            return String.format("%s that connects %s and %s", name, z1, z2);
        }

        public double getSpeed_limitInMeterPerSec() {
            return speed_limit * 1000 / 3600.;
        }
    }

    /**
     * A Holder for a Preference Configuration.
     */
    public static class PreferenceCfg {

        public String zone;
        public double attraction;

        @Override
        public String toString() {
            return String.format("%s (%.2f)", zone, attraction);
        }
    }
}
