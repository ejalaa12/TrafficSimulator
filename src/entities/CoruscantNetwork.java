package entities;

import entities.zone.Zone;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ejalaa on 26/12/2016.
 */
public class CoruscantNetwork extends RoadNetwork {

    public CoruscantNetwork() {
        super();
        definition();
    }

    public void definition() {

        ArrayList<Integer> timeTable = new ArrayList<>(Arrays.asList(0, 7, 9, 17, 19));
        ArrayList<Integer> zone1freq = new ArrayList<>(Arrays.asList(40, 300, 20, 100, 20));

        ZoneSchedule zoneSchedule = new ZoneSchedule(timeTable, )

        createZoneFromPreferencesAndSchedule();
        Zone zone1 = new Zone("zone1");
        Zone zone2 = new Zone("zone2");
        Zone zone3 = new Zone("zone3");
        Zone zone4 = new Zone("zone4");
        Zone zone5 = new Zone("zone5");
        Zone zone6 = new Zone("zone6");
        Zone zone7 = new Zone("zone7");

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

}
