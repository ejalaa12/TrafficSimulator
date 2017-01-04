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
public class SimpleNetwork extends RoadNetwork implements Entity {

    public SimpleNetwork(SimEngine simEngine) {
        super(simEngine);
        definition();
    }

    private void definition() {

        ArrayList<TimeSlot> timeSlots1 = new ArrayList<>();
        timeSlots1.add(new TimeSlot(LocalTime.of(0, 0), LocalTime.of(7, 0), 2000));
        timeSlots1.add(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(9, 0), 3));
        timeSlots1.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0), 1));
        timeSlots1.add(new TimeSlot(LocalTime.of(17, 0), LocalTime.of(19, 0), 3));
        timeSlots1.add(new TimeSlot(LocalTime.of(19, 0), LocalTime.of(23, 59, 59, 99999), 1));
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

        zone1.setPreferedDestination(zone2);
        zone2.setPreferedDestination(zone1);

        Intersection intersection1 = new Intersection("intersection1");

        addArea(zone1);
        addArea(zone2);
        addIntersection(intersection1);


        addRoad(new Road("R1", zone1, intersection1, 3000, 50 * 1000 / 3600.));
        addRoad(new Road("R2", zone2, intersection1, 3000, 50 * 1000 / 3600.));
    }

    @Override
    public void init() {
        zones.get(0).init();
//        zones.get(1).init();

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
