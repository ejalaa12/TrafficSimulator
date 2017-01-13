package entities.zone;

import java.util.HashMap;

/**
 * Created by ejalaa on 11/01/2017.
 */
public class ZoneStats {

    public int numberOfProducedCars = 0;
    public int numberOfCarArrived = 0;
    public int numberOfDismissedCar = 0;

    public int totalDistanceTravelledByAllCars = 0;

    public HashMap<Zone, Integer> numberOfCarFromEachZone = new HashMap<>();

    public void addCarFromZone(Zone zone_source) {
        int tmp = 1;
        if (numberOfCarFromEachZone.containsKey(zone_source)) {
            tmp += numberOfCarFromEachZone.get(zone_source);
        }
        numberOfCarFromEachZone.put(zone_source, tmp);
    }

}
