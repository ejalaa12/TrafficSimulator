package entities;

import entities.zone.TimePeriod;
import entities.zone.Zone;
import entities.zone.ZoneSchedule;
import simulation.Entity;
import simulation.SimEngine;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This class models the Coruscant crossroads
 */
public class CustomRoadNetwork0b3 extends RoadNetwork implements Entity {

    public Zone zone1, zone2;
    public Intersection intersection1, intersection2;
    public Road R1, R2, R3;

    public CustomRoadNetwork0b3(SimEngine simEngine) {
        super(simEngine);
        definition();
    }

    private void definition() {
        /*
        * ##############################################################################################################
        * Zones
        * ##############################################################################################################
        */

        // Zone 1 schedule
        ArrayList<TimePeriod> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(23, 59, 59, 9999999), 4));
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


//        zone1.setPreferredDestination(zone2);
//        zone2.setPreferredDestination(zone1);

        addArea(zone1);
        addArea(zone2);

        /*
        * ##############################################################################################################
        * Intersection
        * ##############################################################################################################
        */

        intersection1 = new Intersection("intersection1");
        addIntersection(intersection1);

        intersection2 = new Intersection("intersection2");
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
