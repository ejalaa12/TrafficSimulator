package entities.zone;

import java.time.Duration;
import java.time.LocalTime;

/**
 * A TimePeriod represents a time slot in the day where the traffic is different
 */
public class TimePeriod {

    private LocalTime start, end;
    private int nbOfCars;
    private Duration slotDuration;

    public TimePeriod(LocalTime start, LocalTime end, int nbOfCars) {
        this.start = start;
        this.end = end;
        this.nbOfCars = nbOfCars;
        slotDuration = Duration.between(this.start, this.end);
    }

    boolean inTimeSlot(LocalTime time) {
        return time == start || (time.isAfter(start) && time.isBefore(end));
    }

    Duration getFrequency() {
        return slotDuration.dividedBy(nbOfCars);
    }
}
