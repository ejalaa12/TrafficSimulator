package entities;

import entities.zone.TimeSlot;
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

    private void definition() {

        ArrayList<TimeSlot> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimeSlot(LocalTime.of(0, 0), LocalTime.of(7, 0), 40));
        timeSlots1.add(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(9, 0), 300));
        timeSlots1.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0), 20));
        timeSlots1.add(new TimeSlot(LocalTime.of(17, 0), LocalTime.of(19, 0), 100));
        timeSlots1.add(new TimeSlot(LocalTime.of(19, 0), LocalTime.of(23, 59, 59, 99999), 20));
        ZoneSchedule zoneSchedule1 = new ZoneSchedule(timeSlots1);

        ArrayList<TimeSlot> timeSlots2 = new ArrayList<>();
        timeSlots2.add(new TimeSlot(LocalTime.of(0, 0), LocalTime.of(7, 0), 50));
        timeSlots2.add(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(9, 0), 200));
        timeSlots2.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0), 30));
        timeSlots2.add(new TimeSlot(LocalTime.of(17, 0), LocalTime.of(19, 0), 150));
        timeSlots2.add(new TimeSlot(LocalTime.of(19, 0), LocalTime.of(23, 59, 59, 99999), 30));
        ZoneSchedule zoneSchedule2 = new ZoneSchedule(timeSlots2);

        Zone zone1 = new Zone("zone1", zoneSchedule1, simEngine, this);
        Zone zone2 = new Zone("zone2", zoneSchedule2, simEngine, this);
        Zone zone3 = new Zone("zone3", zoneSchedule1, simEngine, this);
        Zone zone4 = new Zone("zone4", zoneSchedule1, simEngine, this);
        Zone zone5 = new Zone("zone5", zoneSchedule1, simEngine, this);
        Zone zone6 = new Zone("zone6", zoneSchedule1, simEngine, this);
        Zone zone7 = new Zone("zone7", zoneSchedule1, simEngine, this);

        zone1.setPreferedDestination(zone4);
        zone2.setPreferedDestination(zone5);
        zone3.setPreferedDestination(zone6);
        zone4.setPreferedDestination(zone1);
        zone5.setPreferedDestination(zone7);
        zone6.setPreferedDestination(zone3);
        zone7.setPreferedDestination(zone2);

        Intersection intersection1 = new Intersection("intersection1");
        Intersection intersection2 = new Intersection("intersection2");
        Intersection intersection3 = new Intersection("intersection3");
        Intersection intersection4 = new Intersection("intersection4");

        addArea(zone1);
        addArea(zone2);
        addArea(zone3);
        addArea(zone4);
        addArea(zone5);
        addArea(zone6);
        addArea(zone7);
        addIntersection(intersection1);
        addIntersection(intersection2);
        addIntersection(intersection3);
        addIntersection(intersection4);


        addRoad(new Road("R1", zone1, intersection1, 100, 50*1000/3600));
        addRoad(new Road("R2", intersection1, intersection2, 100, 50*1000/3600));
        addRoad(new Road("R3", intersection1, intersection4, 100, 50*1000/3600));
        addRoad(new Road("R4", zone2, intersection2, 100, 50*1000/3600));
        addRoad(new Road("R5", zone3, intersection2, 100, 50*1000/3600));
        addRoad(new Road("R6", zone5, intersection4, 100, 50*1000/3600));
        addRoad(new Road("R7", zone6, intersection4, 100, 50*1000/3600));
        addRoad(new Road("R8", zone7, intersection3, 100, 50*1000/3600));
        addRoad(new Road("R9", zone4, intersection3, 100, 50*1000/3600));
        addRoad(new Road("R10", intersection4, intersection3, 100, 50*1000/3600));
    }

    @Override
    public void init() {
        zones.get(0).init();
        zones.get(1).init();

//        for (Zone zone : zones) {
//            zone.init();
//        }
        // TODO: 26/12/2016 do same for intersection and roads
    }

    @Override
    public void printStats() {
        for (Zone zone : zones) {
            zone.printStats();
        }

    }

    @Override
    public String getName() {
        return null;
    }
}
