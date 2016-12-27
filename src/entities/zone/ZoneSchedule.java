package entities.zone;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * The ZoneSchedule contains all the timeSlot of the day.
 * It defines them correctly
 */
public class ZoneSchedule {

    private ArrayList<TimeSlot> timeSlots;

    public ZoneSchedule(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    Duration getCurrentFrequency(LocalTime currentTime) {
        return getCurrentTimeSlot(currentTime).getFrequency();
    }

    private TimeSlot getCurrentTimeSlot(LocalTime currentTime) {
        return timeSlots.stream().filter(x -> x.inTimeSlot(currentTime)).findFirst().orElse(null);
    }


    /*
    * ****************************************************************************************************************
    * Getter and setters
    * ****************************************************************************************************************
    */


}
