package tests.CarAlone;

import entities.RoadNetwork;
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
 * A custom crossroad for testing purposes
 * Z1----I1----I2----I3----I4----Z2
 *                  ^     ^
 *                 STOP   TL
 */
public class CustomCrossroad extends RoadNetwork implements Entity {

    public int length, zone1Cars, zone2Cars;
    public Zone zone1, zone2;
    public Road R1, R2, R3, R4, R5;
    public Intersection intersection1, intersection2, intersection3, intersection4;
    public StopSign stopSign;
    public TrafficLight trafficLight;

    public CustomCrossroad(SimEngine simEngine, int length, int zone1Cars, int zone2Cars) {
        super(simEngine);
        this.length = length;
        this.zone1Cars = zone1Cars;
        this.zone2Cars = zone2Cars;
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
        zone1 = createZoneWithTimePeriod("zone1", 0, 0, 23, 59, 59, 9999, zone1Cars);
        zone2 = createZoneWithTimePeriod("zone2", 0, 0, 23, 59, 59, 9999, zone2Cars);

        // Preferences
        setPreference(zone1, zone2, 1.);
        setPreference(zone2, zone1, 1.);

        addArea(zone1);
        addArea(zone2);

        /*
        * ##############################################################################################################
        * Intersections
        * ##############################################################################################################
        */
        intersection1 = new Intersection("intersection1", simEngine);
        intersection2 = new Intersection("intersection2", simEngine);
        intersection3 = new Intersection("intersection3", simEngine);
        intersection4 = new Intersection("intersection4", simEngine);
        addIntersection(intersection1);
        addIntersection(intersection2);
        addIntersection(intersection3);
        addIntersection(intersection4);

        /*
        * ##############################################################################################################
        * Roads
        * ##############################################################################################################
        */
        R1 = new Road("R1", zone1, intersection1, length, 50 * 1000 / 3600.);
        R2 = new Road("R2", intersection1, intersection2, length, 50 * 1000 / 3600.);
        R3 = new Road("R3", intersection2, intersection3, length, 50 * 1000 / 3600.);
        R4 = new Road("R4", intersection3, intersection4, length, 50 * 1000 / 3600.);
        R5 = new Road("R4", intersection4, zone2, length, 50 * 1000 / 3600.);

        addRoad(R1);
        addRoad(R2);
        addRoad(R3);
        addRoad(R4);
        addRoad(R5);

        /*
        * ##############################################################################################################
        * Traffic Signs
        * ##############################################################################################################
        */

        Lane laneWithStop = R3.getLaneWithDestination(intersection3);
        stopSign = new StopSign(laneWithStop, simEngine);
        laneWithStop.setTrafficSign(stopSign);

        Lane laneWithTL = R4.getLaneWithDestination(intersection4);
        trafficLight = new TrafficLight(laneWithTL, simEngine);
        trafficLight.setState(TrafficLightState.RED);
        laneWithTL.setTrafficSign(trafficLight);

    }

    private void setPreference(Zone zone, Zone zone_pref, double percentage) {
        ZonePreference zonePreference = new ZonePreference(simEngine.getRandom());
        ArrayList<Zone> prefs = new ArrayList<>(Arrays.asList(new Zone[]{zone_pref}));
        ArrayList<Double> percentages = new ArrayList<>(Arrays.asList(new Double[]{percentage}));
        zonePreference.addPreferences(prefs, percentages);
        zone.setZonePreference(zonePreference);
    }

    private Zone createZoneWithTimePeriod(String name, int h0, int m0, int h1, int m1, int s1, int ns1, int nCars) {
        // Zone 1 schedule
        ArrayList<TimePeriod> timeSlots = new ArrayList<>();
        timeSlots.add(new TimePeriod(LocalTime.of(h0, m0), LocalTime.of(h1, m1, s1, ns1), nCars));
        ZoneSchedule zoneSchedule = new ZoneSchedule(timeSlots);
        // Zone 1
        return new Zone(name, zoneSchedule, simEngine, this);
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
