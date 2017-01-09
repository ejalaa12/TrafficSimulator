package entities;

import entities.traffic_light.TrafficLight;
import entities.zone.TimePeriod;
import entities.zone.Zone;
import entities.zone.ZoneSchedule;
import simulation.Entity;
import simulation.SimEngine;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This class models Coruscant Carrefour
 */
public class CoruscantNetwork extends RoadNetwork implements Entity {

    public CoruscantNetwork(SimEngine simEngine) {
        super(simEngine);
        definition();
    }

    private void createZones() {

    }

    private void definition() {

        /*
        * ##############################################################################################################
        * Zones
        * ##############################################################################################################
        */

        ArrayList<TimePeriod> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(7, 0), 40));
        timeSlots1.add(new TimePeriod(LocalTime.of(7, 0), LocalTime.of(9, 0), 300));
        timeSlots1.add(new TimePeriod(LocalTime.of(9, 0), LocalTime.of(17, 0), 20));
        timeSlots1.add(new TimePeriod(LocalTime.of(17, 0), LocalTime.of(19, 0), 100));
        timeSlots1.add(new TimePeriod(LocalTime.of(19, 0), LocalTime.of(23, 59, 59, 99999), 20));
        ZoneSchedule zoneSchedule1 = new ZoneSchedule(timeSlots1);

        ArrayList<TimePeriod> timeSlots2 = new ArrayList<>();
        timeSlots2.add(new TimePeriod(LocalTime.of(0, 0), LocalTime.of(7, 0), 50));
        timeSlots2.add(new TimePeriod(LocalTime.of(7, 0), LocalTime.of(9, 0), 200));
        timeSlots2.add(new TimePeriod(LocalTime.of(9, 0), LocalTime.of(17, 0), 30));
        timeSlots2.add(new TimePeriod(LocalTime.of(17, 0), LocalTime.of(19, 0), 150));
        timeSlots2.add(new TimePeriod(LocalTime.of(19, 0), LocalTime.of(23, 59, 59, 99999), 30));
        ZoneSchedule zoneSchedule2 = new ZoneSchedule(timeSlots2);

        Zone zone1 = new Zone("zone1", zoneSchedule1, simEngine, this);
        Zone zone2 = new Zone("zone2", zoneSchedule2, simEngine, this);
        Zone zone3 = new Zone("zone3", zoneSchedule1, simEngine, this);
        Zone zone4 = new Zone("zone4", zoneSchedule1, simEngine, this);
        Zone zone5 = new Zone("zone5", zoneSchedule1, simEngine, this);
        Zone zone6 = new Zone("zone6", zoneSchedule1, simEngine, this);
        Zone zone7 = new Zone("zone7", zoneSchedule1, simEngine, this);

        zone1.setPreferredDestination(zone4);
        zone2.setPreferredDestination(zone5);
        zone3.setPreferredDestination(zone6);
        zone4.setPreferredDestination(zone1);
        zone5.setPreferredDestination(zone7);
        zone6.setPreferredDestination(zone3);
        zone7.setPreferredDestination(zone2);

        addArea(zone1);
        addArea(zone2);
        addArea(zone3);
        addArea(zone4);
        addArea(zone5);
        addArea(zone6);
        addArea(zone7);
        /*
        * ##############################################################################################################
        * Intersections
        * ##############################################################################################################
        */

        Intersection intersection1 = new Intersection("intersection1");
        Intersection intersection2 = new Intersection("intersection2");
        Intersection intersection3 = new Intersection("intersection3");
        Intersection intersection4 = new Intersection("intersection4");

        addIntersection(intersection1);
        addIntersection(intersection2);
        addIntersection(intersection3);
        addIntersection(intersection4);

        /*
        * ##############################################################################################################
        * Roads
        * ##############################################################################################################
        */

        Road r11 = new Road("R1.1", zone1, intersection1, 3000, 50 * 1000 / 3600.);
        Road r12 = new Road("R1.2", intersection1, intersection2, 1300, 50 * 1000 / 3600.);
        Road r13 = new Road("R1.3", zone3, intersection2, 2000, 50 * 1000 / 3600.);
        Road r2 = new Road("R2", zone2, intersection2, 4500, 50 * 1000 / 3600.);
        Road r21 = new Road("R2.1", zone5, intersection4, 4500, 50 * 1000 / 3600.);
        Road r22 = new Road("R2.2", intersection4, intersection3, 800, 50 * 1000 / 3600.);
        Road r23 = new Road("R2.3", zone4, intersection3, 1400, 50 * 1000 / 3600.);
        Road r31 = new Road("R3.1", intersection1, intersection4, 3500, 50 * 1000 / 3600.);
        Road r32 = new Road("R3.2", zone6, intersection4, 1000, 50 * 1000 / 3600.);
        Road r4 = new Road("R4", zone7, intersection3, 3000, 50 * 1000 / 3600.);

        addRoad(r11);
        addRoad(r12);
        addRoad(r13);
        addRoad(r2);
        addRoad(r21);
        addRoad(r22);
        addRoad(r23);
        addRoad(r31);
        addRoad(r32);
        addRoad(r4);
        /*
        * ##############################################################################################################
        * Traffic Signs
        * ##############################################################################################################
        */

        Lane lanesWithTL[] = new Lane[3];
        lanesWithTL[0] = r23.getLaneWithDestination(intersection3);
        lanesWithTL[1] = r4.getLaneWithDestination(intersection3);
        lanesWithTL[2] = r22.getLaneWithDestination(intersection3);

        for (Lane lane : lanesWithTL) {
            lane.setTrafficSign(new TrafficLight(lane, simEngine));
        }

        /*
        * ##############################################################################################################
        * Stops
        * ##############################################################################################################
        */

    }

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
