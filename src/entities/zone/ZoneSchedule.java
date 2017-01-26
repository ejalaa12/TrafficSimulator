package entities.zone;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * The ZoneSchedule contains all the timeSlot of the day.
 * It defines them correctly
 */
public class ZoneSchedule {

    private ArrayList<TimePeriod> timePeriods;

    public ZoneSchedule(ArrayList<Integer> periods, ArrayList<Integer> productionPerPeriod) {
        timePeriods = new ArrayList<>();
        for (int i = 0; i < periods.size() - 1; i++) {
            timePeriods.add(new TimePeriod(LocalTime.of(periods.get(i), 0), LocalTime.of(periods.get(i + 1), 0), productionPerPeriod.get(i)));
        }
        timePeriods.add(new TimePeriod(LocalTime.of(periods.get(periods.size() - 1), 0), LocalTime.of(23, 59, 59, 999999999), productionPerPeriod.get(productionPerPeriod.size() - 1)));

    }

    public ZoneSchedule(ArrayList<TimePeriod> timePeriods) {
        this.timePeriods = timePeriods;
    }

    Duration getCurrentFrequency(LocalTime currentTime) {
        if (getCurrentTimeSlot(currentTime) == null)
            System.out.println("gnark");
        return getCurrentTimeSlot(currentTime).getFrequency();
    }

    private TimePeriod getCurrentTimeSlot(LocalTime currentTime) {
        return timePeriods.stream().filter(x -> x.inTimeSlot(currentTime)).findFirst().orElse(null);
    }


    /*
    * ****************************************************************************************************************
    * Getter and setters
    * ****************************************************************************************************************
    */


}
