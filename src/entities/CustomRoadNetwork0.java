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
public class CustomRoadNetwork0 extends RoadNetwork implements Entity {

    private int length, zone1Cars, zone2Cars;
    private Zone zone1, zone2;

    public CustomRoadNetwork0(SimEngine simEngine, int length, int zone1Cars, int zone2Cars) {
        super(simEngine);
        this.length = length;
        this.zone1Cars = zone1Cars;
        this.zone2Cars = zone2Cars;
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
        timeSlots1.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(23, 59, 59, 99999), zone1Cars));
        ZoneSchedule zoneSchedule1 = new ZoneSchedule(timeSlots1);
        // Zone 1
        Zone zone1 = new Zone("zone1", zoneSchedule1, simEngine, this);
        // Zone 2 schedule
        ArrayList<TimePeriod> timeSlots2 = new ArrayList<>();
        timeSlots2.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(23, 59, 59, 99999), zone2Cars));
        ZoneSchedule zoneSchedule2 = new ZoneSchedule(timeSlots2);
        // zone 2
        Zone zone2 = new Zone("zone2", zoneSchedule2, simEngine, this);
        // Preferences
        zone1.setPreferredDestination(zone2);
        zone2.setPreferredDestination(zone1);

        addArea(zone1);
        addArea(zone2);

        /*
        * ##############################################################################################################
        * Roads
        * ##############################################################################################################
        */

        addRoad(new Road("R1", zone1, zone2, length, 50 * 1000 / 3600.));
        this.zone1 = zone1;
        this.zone2 = zone2;

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

    public Zone getZone1() {
        return zone1;
    }

    public Zone getZone2() {
        return zone2;
    }
}
