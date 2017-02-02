package tests.custom_crossroads;

import entities.RoadNetwork;
import entities.intersection.Intersection;
import entities.lane.Lane;
import entities.lane.Road;
import entities.traffic_signs.TrafficLight;
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
public class CustomRoadNetwork3 extends RoadNetwork implements Entity {

    public Zone zone1, zone2, zone3;
    public Intersection intersection;
    public Road R1, R2, R3;

    public CustomRoadNetwork3(SimEngine simEngine) {
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
        * Zones & schedules & preferences
        * ##############################################################################################################
        */
        /*
        * **************************************************************************************************************
        * zone 1
        */

        // Zone 1 schedule
        ArrayList<TimePeriod> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(12, 0), 100));
        timeSlots1.add(new TimePeriod(LocalTime.of(12, 0), LocalTime.of(23, 59, 59, 99999), 1));
        ZoneSchedule zoneSchedule1 = new ZoneSchedule(timeSlots1, simEngine.getRandom());

        // Zone 1
        zone1 = new Zone("zone1", zoneSchedule1, simEngine, this);

        /*
        * **************************************************************************************************************
        * zone 2
        */

        // Zone 2 schedule
        ArrayList<TimePeriod> timeSlots2 = new ArrayList<>();
        timeSlots2.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(12, 0), 2));
        timeSlots2.add(new TimePeriod(LocalTime.of(12, 0), LocalTime.of(23, 59, 59, 99999), 2));
        ZoneSchedule zoneSchedule2 = new ZoneSchedule(timeSlots2, simEngine.getRandom());
        // zone 2
        zone2 = new Zone("zone2", zoneSchedule2, simEngine, this);

        // Zone 3 schedule
        ArrayList<TimePeriod> timeSlots3 = new ArrayList<>();
        timeSlots3.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(12, 0), 3));
        timeSlots3.add(new TimePeriod(LocalTime.of(12, 0), LocalTime.of(23, 59, 59, 99999), 3));
        ZoneSchedule zoneSchedule3 = new ZoneSchedule(timeSlots3, simEngine.getRandom());
        // zone 3
        zone3 = new Zone("zone3", zoneSchedule3, simEngine, this);

        /*
        * **************************************************************************************************************
        * Preferences
        */

        // Preferences Zone1
        ZonePreference zonePreference1 = new ZonePreference(simEngine.getRandom());
        ArrayList<Zone> zones1 = new ArrayList<>(Arrays.asList(new Zone[]{zone2, zone3}));
        ArrayList<Double> percentages1 = new ArrayList<>(Arrays.asList(new Double[]{0.5, 0.5}));
        zonePreference1.addPreferences(zones1, percentages1);
        zone1.setZonePreference(zonePreference1);

        // Preferences Zone1
        ZonePreference zonePreference2 = new ZonePreference(simEngine.getRandom());
        ArrayList<Zone> zones2 = new ArrayList<>(Arrays.asList(new Zone[]{zone1, zone3}));
        ArrayList<Double> percentages2 = new ArrayList<>(Arrays.asList(new Double[]{0.1, 0.9}));
        zonePreference2.addPreferences(zones2, percentages2);
        zone2.setZonePreference(zonePreference2);

        // Preferences Zone1
        ZonePreference zonePreference3 = new ZonePreference(simEngine.getRandom());
        ArrayList<Zone> zones3 = new ArrayList<>(Arrays.asList(new Zone[]{zone1, zone2}));
        ArrayList<Double> percentages3 = new ArrayList<>(Arrays.asList(new Double[]{0.3, 0.7}));
        zonePreference3.addPreferences(zones3, percentages3);
        zone3.setZonePreference(zonePreference3);

        /*
        * **************************************************************************************************************
        * Adding them to the network
        */

        addArea(zone1);
        addArea(zone2);
        addArea(zone3);

        /*
        * ##############################################################################################################
        * Intersection
        * ##############################################################################################################
        */

        intersection = new Intersection("intersection", simEngine);
        addIntersection(intersection);


        /*
        * ##############################################################################################################
        * Roads
        * ##############################################################################################################
        */

        R1 = new Road("R1", zone1, intersection, 100, 50 * 1000 / 3600.);
        R2 = new Road("R2", zone2, intersection, 200, 50 * 1000 / 3600.);
        R3 = new Road("R3", zone3, intersection, 300, 50 * 1000 / 3600.);
        addRoad(R1);
        addRoad(R2);
        addRoad(R3);

        /*
        * ##############################################################################################################
        * Traffic Signs
        * ##############################################################################################################
        */

        Lane laneWithTrafficLight = R1.getLaneWithDestination(intersection);
        laneWithTrafficLight.setTrafficSign(new TrafficLight(laneWithTrafficLight, simEngine));
    }

    /**
     * For testing purposes, we don't initialize everything
     */
    @Override
    public void init() {
        super.init();
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
}
