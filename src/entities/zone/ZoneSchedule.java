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

    public ZoneSchedule(ArrayList<TimePeriod> timePeriods) {
        this.timePeriods = timePeriods;
    }

    Duration getCurrentFrequency(LocalTime currentTime) {
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
