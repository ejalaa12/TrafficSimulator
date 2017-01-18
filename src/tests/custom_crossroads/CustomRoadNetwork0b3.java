package tests.custom_crossroads;

import entities.RoadNetwork;
import entities.intersection.Intersection;
import entities.lane.Road;
import entities.traffic_signs.StopSign;
import entities.traffic_signs.TrafficLight;
import entities.traffic_signs.TrafficLightState;
import entities.zone.TimePeriod;
import entities.zone.Zone;
import entities.zone.ZonePreference;
import entities.zone.ZoneSchedule;
import simulation.Entity;
import simulation.SimEngine;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class models the Coruscant crossroads
 */
public class CustomRoadNetwork0b3 extends RoadNetwork implements Entity {

    public Zone zone1, zone2;
    public Intersection intersection1, intersection2;
    public Road R1, R2, R3;
    public TrafficLight tl;
    public StopSign stop;

    public CustomRoadNetwork0b3(SimEngine simEngine) {
        super(simEngine);
        definition();
        connectIntersections();
    }

    private void connectIntersections() {
        for (Intersection intersection : intersections) {
            intersection.addConnectedLanes(getConnections(intersection));
        }
    }

    private void definition() {
        /*
        * ##############################################################################################################
        * Zones
        * ##############################################################################################################
        */

        // Zone 1 schedule
        ArrayList<TimePeriod> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(23, 59, 59, 9999999), 4000));
        ZoneSchedule zoneSchedule1 = new ZoneSchedule(timeSlots1);
        // Zone 1
        zone1 = new Zone("zone1", zoneSchedule1, simEngine, this);
        // Zone 2 schedule
        ArrayList<TimePeriod> timeSlots2 = new ArrayList<>();
        timeSlots2.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(23, 59, 59, 99999999), 2));
        ZoneSchedule zoneSchedule2 = new ZoneSchedule(timeSlots2);
        // zone 2
        zone2 = new Zone("zone2", zoneSchedule2, simEngine, this);
        // Preferences

        // Preferences Zone1
        ZonePreference zonePreference1 = new ZonePreference(simEngine.getRandom());
        ArrayList<Zone> zones1 = new ArrayList<>(Arrays.asList(new Zone[]{zone2}));
        ArrayList<Double> percentages1 = new ArrayList<>(Arrays.asList(new Double[]{1.}));
        zonePreference1.addPreferences(zones1, percentages1);
        zone1.setZonePreference(zonePreference1);

        // Preferences Zone2
        ZonePreference zonePreference2 = new ZonePreference(simEngine.getRandom());
        ArrayList<Zone> zones2 = new ArrayList<>(Arrays.asList(new Zone[]{zone1}));
        ArrayList<Double> percentages2 = new ArrayList<>(Arrays.asList(new Double[]{1.}));
        zonePreference2.addPreferences(zones2, percentages2);
        zone2.setZonePreference(zonePreference2);

        addArea(zone1);
        addArea(zone2);

        /*
        * ##############################################################################################################
        * Intersection
        * ##############################################################################################################
        */

        intersection1 = new Intersection("intersection1", simEngine);
        addIntersection(intersection1);

        intersection2 = new Intersection("intersection2", simEngine);
        addIntersection(intersection2);
        /*
        * ##############################################################################################################
        * Roads
        * ##############################################################################################################
        */

        R1 = new Road("R1", zone1, intersection1, 100, 50 * 1000 / 3600.);
        R2 = new Road("R2", intersection1, intersection2, 16, 50 * 1000 / 3600.);
        R3 = new Road("R3", intersection2, zone2, 300, 50 * 1000 / 3600.);
        addRoad(R1);
        addRoad(R2);
        addRoad(R3);
        
        /*
        * ##############################################################################################################
        * TrafficLights
        * ##############################################################################################################
        */

        tl = new TrafficLight(R2.getLaneWithDestination(intersection2), simEngine);
        tl.setState(TrafficLightState.RED);
        R2.getLaneWithDestination(intersection2).setTrafficSign(tl);

        stop = new StopSign(R1.getLaneWithDestination(intersection1), simEngine);
        R1.getLaneWithDestination(intersection1).setTrafficSign(stop);
    }

    /**
     * For testing purposes, we don't initialize everything
     */
    @Override
    public void init() {
//        super.init();
        zone1.init();
        initTrafficLights();
    }

    @Override
    public void logStats() {
        for (Zone zone : zones) {
            zone.logStats();
        }
        for (Road road : roads) {
            System.out.format("%s queue: %d -- ", road.getLane1().getId(), road.getLane1().getCarQueue().size());
            System.out.format("%s queue: %d", road.getLane2().getId(), road.getLane2().getCarQueue().size());
            System.out.println();
        }
    }

    @Override
    public String getName() {
        return null;
    }

    public Zone getZone1() {
        return zone1;
    }

    public Zone getZone2() {
        return zone2;
    }
}
