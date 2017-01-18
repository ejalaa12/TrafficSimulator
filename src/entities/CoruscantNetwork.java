package entities;

import entities.intersection.Intersection;
import entities.lane.Lane;
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
 * This class models Coruscant Carrefour
 */
public class CoruscantNetwork extends RoadNetwork implements Entity {

    // to make it easier to access the correct zones
    private Zone zone1, zone2, zone3, zone4, zone5, zone6, zone7;

    public CoruscantNetwork(SimEngine simEngine) {
        super(simEngine);
        definition();
        connectIntersections();
    }

    private void connectIntersections() {
        for (Intersection intersection : intersections) {
            intersection.addConnectedLanes(getConnections(intersection));
        }
    }

    private void createZones() {

    }

    private void definition() {

        /*
        * ##############################################################################################################
        * Zones
        * ##############################################################################################################
        */

        ArrayList<Integer> periods = new ArrayList<>(Arrays.asList(0, 7, 9, 17, 19));

        ArrayList<Integer> carsPerPeriod1 = new ArrayList<>(Arrays.asList(40, 300, 20, 100, 20));
        ZoneSchedule zoneSchedule1 = createZoneSchedule(periods, carsPerPeriod1);

        ArrayList<Integer> carsPerPeriod2 = new ArrayList<>(Arrays.asList(50, 200, 30, 150, 30));
        ZoneSchedule zoneSchedule2 = createZoneSchedule(periods, carsPerPeriod2);

        ArrayList<Integer> carsPerPeriod3 = new ArrayList<>(Arrays.asList(30, 100, 20, 300, 15));
        ZoneSchedule zoneSchedule3 = createZoneSchedule(periods, carsPerPeriod3);

        ArrayList<Integer> carsPerPeriod4 = new ArrayList<>(Arrays.asList(20, 100, 30, 200, 50));
        ZoneSchedule zoneSchedule4 = createZoneSchedule(periods, carsPerPeriod4);

        ArrayList<Integer> carsPerPeriod5 = new ArrayList<>(Arrays.asList(30, 400, 20, 150, 20));
        ZoneSchedule zoneSchedule5 = createZoneSchedule(periods, carsPerPeriod5);

        ArrayList<Integer> carsPerPeriod6 = new ArrayList<>(Arrays.asList(20, 50, 30, 100, 10));
        ZoneSchedule zoneSchedule6 = createZoneSchedule(periods, carsPerPeriod6);

        ArrayList<Integer> carsPerPeriod7 = new ArrayList<>(Arrays.asList(30, 30, 10, 100, 10));
        ZoneSchedule zoneSchedule7 = createZoneSchedule(periods, carsPerPeriod7);


        zone1 = new Zone("zone1", zoneSchedule1, simEngine, this);
        zone2 = new Zone("zone2", zoneSchedule2, simEngine, this);
        zone3 = new Zone("zone3", zoneSchedule3, simEngine, this);
        zone4 = new Zone("zone4", zoneSchedule4, simEngine, this);
        zone5 = new Zone("zone5", zoneSchedule5, simEngine, this);
        zone6 = new Zone("zone6", zoneSchedule6, simEngine, this);
        zone7 = new Zone("zone7", zoneSchedule7, simEngine, this);

        // Preferences Zone1
        ArrayList<Integer> attractions1 = new ArrayList<>(Arrays.asList(5, 10, 10, 5, 35, 35));
        createPreference(zone1, attractions1);

        // Preferences Zone2
        ArrayList<Integer> attractions2 = new ArrayList<>(Arrays.asList(10, 5, 20, 20, 25, 20));
        createPreference(zone2, attractions2);

        // Preferences Zone3
        ArrayList<Integer> attractions3 = new ArrayList<>(Arrays.asList(15, 15, 20, 20, 20, 10));
        createPreference(zone3, attractions3);

        // Preferences Zone4
        ArrayList<Integer> attractions4 = new ArrayList<>(Arrays.asList(15, 10, 10, 20, 40, 5));
        createPreference(zone4, attractions4);

        // Preferences Zone5
        ArrayList<Integer> attractions5 = new ArrayList<>(Arrays.asList(10, 30, 10, 10, 10, 30));
        createPreference(zone5, attractions5);

        // Preferences Zone6
        ArrayList<Integer> attractions6 = new ArrayList<>(Arrays.asList(20, 10, 40, 10, 10, 10));
        createPreference(zone6, attractions6);

        // Preferences Zone7
        ArrayList<Integer> attractions7 = new ArrayList<>(Arrays.asList(20, 20, 20, 20, 10, 10));
        createPreference(zone7, attractions7);

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

        Intersection intersection1 = new Intersection("intersection1", simEngine);
        Intersection intersection2 = new Intersection("intersection2", simEngine);
        Intersection intersection3 = new Intersection("intersection3", simEngine);
        Intersection intersection4 = new Intersection("intersection4", simEngine);

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

        TrafficLight light1 = new TrafficLight(lanesWithTL[0], simEngine, "tl1");
        TrafficLight light2 = new TrafficLight(lanesWithTL[1], simEngine, "tl2");
        TrafficLight light3 = new TrafficLight(lanesWithTL[2], simEngine, "tl3");

        lanesWithTL[0].setTrafficSign(light1);
        lanesWithTL[1].setTrafficSign(light2);
        lanesWithTL[2].setTrafficSign(light3);

        /*
        * ##############################################################################################################
        * Stops
        * ##############################################################################################################
        */

        Lane lanesWithStop[] = new Lane[4];
        lanesWithStop[0] = r31.getLaneWithDestination(intersection1);
        lanesWithStop[1] = r31.getLaneWithDestination(intersection4);
        lanesWithStop[2] = r32.getLaneWithDestination(intersection4);
        lanesWithStop[3] = r2.getLaneWithDestination(intersection2);

        for (Lane lane : lanesWithStop) {
            lane.setTrafficSign(new StopSign(lane, simEngine));
        }

    }

    private void createPreference(Zone main_zone, ArrayList<Integer> attractions) {
        ArrayList<Zone> all_zones = new ArrayList<>(Arrays.asList(zone1, zone2, zone3, zone4, zone5, zone6, zone7));
        ArrayList<Zone> destinations = new ArrayList<>();
        for (Zone zone : all_zones) {
            if (zone != main_zone) destinations.add(zone);
        }
        ArrayList<Double> percentages = new ArrayList<>();
        for (int att : attractions) {
            percentages.add(att / 100.);
        }
        if (percentages.size() != destinations.size()) {
            throw new IllegalStateException("destination size must be equal to percentages");
        }

        ZonePreference zonePreference = new ZonePreference(simEngine.getRandom());
        zonePreference.addPreferences(destinations, percentages);
        main_zone.setZonePreference(zonePreference);
    }

    private ZoneSchedule createZoneSchedule(ArrayList<Integer> periods, ArrayList<Integer> carsPerPeriod1) {
        ArrayList<TimePeriod> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimePeriod(LocalTime.of(periods.get(0), 0), LocalTime.of(periods.get(1), 0), carsPerPeriod1.get(0)));
        timeSlots1.add(new TimePeriod(LocalTime.of(periods.get(1), 0), LocalTime.of(periods.get(2), 0), carsPerPeriod1.get(1)));
        timeSlots1.add(new TimePeriod(LocalTime.of(periods.get(2), 0), LocalTime.of(periods.get(3), 0), carsPerPeriod1.get(2)));
        timeSlots1.add(new TimePeriod(LocalTime.of(periods.get(3), 0), LocalTime.of(periods.get(4), 0), carsPerPeriod1.get(3)));
        timeSlots1.add(new TimePeriod(LocalTime.of(periods.get(4), 0), LocalTime.of(23, 59, 59, 999999999), carsPerPeriod1.get(4)));

        return new ZoneSchedule(timeSlots1);
    }

    @Override
    public void init() {
        super.init();
        for (Intersection intersection :
                intersections) {
            intersection.init();
        }
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
