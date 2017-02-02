package entities.zone;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * The ZoneSchedule contains all the timeSlot of the day.
 * It defines them correctly
 */
public class ZoneSchedule {

    private ArrayList<TimePeriod> timePeriods;
    private Random random;

    public ZoneSchedule(ArrayList<Integer> periods, ArrayList<Integer> productionPerPeriod, Random random) {
        this.random = random;
        timePeriods = new ArrayList<>();
        for (int i = 0; i < periods.size() - 1; i++) {
            int productionForThisPeriod = productionPerPeriod.get(i);
            // randomized production is ± 5%
            int randomized_production = (int) (productionForThisPeriod * ((95 + random.nextInt(105 - 95 + 1)) / 100.));
            timePeriods.add(new TimePeriod(LocalTime.of(periods.get(i), 0), LocalTime.of(periods.get(i + 1), 0), randomized_production));
        }
        int productionForLastPeriod = productionPerPeriod.get(productionPerPeriod.size() - 1);
        int randomized_production = (int) (productionForLastPeriod * ((95 + random.nextInt(105 - 95 + 1)) / 100.));
        timePeriods.add(new TimePeriod(LocalTime.of(periods.get(periods.size() - 1), 0), LocalTime.of(23, 59, 59, 999999999), randomized_production));
    }

    public ZoneSchedule(ArrayList<TimePeriod> timePeriods, Random random) {
        this.random = random;
        this.timePeriods = timePeriods;
    }

    /*
    * ****************************************************************************************************************
    * Getter and setters
    * ****************************************************************************************************************
    */

    Duration getCurrentFrequency(LocalTime currentTime) {
        if (getCurrentTimeSlot(currentTime) == null)
            System.out.println("gnark");

        Duration realFrequency = getCurrentTimeSlot(currentTime).getFrequency();
        int randfact = 95 + random.nextInt(105 - 95 + 1);
        return realFrequency.multipliedBy(randfact).dividedBy(100);
    }


    private TimePeriod getCurrentTimeSlot(LocalTime currentTime) {
        return timePeriods.stream().filter(x -> x.inTimeSlot(currentTime)).findFirst().orElse(null);
    }


}
