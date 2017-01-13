package entities.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ejalaa on 13/01/2017.
 */
public class ZonePreference {

    private HashMap<double[], Zone> preferenceTable;
    private Random random;
    private double cumul;
    private ArrayList<double[]> keys;

    public ZonePreference(Random random) {
        this.random = random;
        cumul = 0;
        keys = new ArrayList<>();
        preferenceTable = new HashMap<>();
    }

    public void addPreferences(ArrayList<Zone> zones, ArrayList<Double> percentages) {
        if (zones.size() == percentages.size()) {
            for (int i = 0; i < zones.size(); i++) {
                addPreference(zones.get(i), percentages.get(i));
            }
        } else {
            throw new IllegalArgumentException("Zones and percentages must have the same size");
        }
    }

    public void addPreference(Zone zone, double percentage) {
        cumul += percentage;
        cumul = mround(cumul); // this is to avoid floating point problems
        double key[] = {percentage, cumul};
        keys.add(key);
        if (cumul > 1f) {
            throw new IllegalArgumentException("Can't create preferences if the percentages don't add up to 1.");
        }
        // if everything is ok
        preferenceTable.put(key, zone);
    }

    public Zone getDestination() {
        if (cumul != 1) {
            throw new IllegalStateException(String.format("Not enough preferences, cumul of percentage(=%s)", cumul));
        }
        double r = random.nextDouble();
        if (r < keys.get(0)[1]) {
            return preferenceTable.get(keys.get(0));
        }
        double cumul1, cumul2;
        for (int i = 0; i < keys.size() - 1; i++) {
            cumul1 = keys.get(i)[1];
            cumul2 = keys.get(i + 1)[1];
            if (cumul1 < r && r <= cumul2) return preferenceTable.get(keys.get(i + 1));
        }
        return null;
    }

    private double sum(ArrayList<Double> array) {
        double total = 0;
        for (double el : array) {
            total += el;
        }
        return total;
    }

    /**
     * the problem is that we have floating error problems:
     * 0.1+0.2 gives 0.30000000000000004 instead of 0.3
     * So we round it to avoid this mistakes
     * In this case percentage are only values with maximum two decimals, so we loose no precision
     *
     * @param value the value to round
     * @return the rounded value
     */
    private double mround(double value) {
        return mround(value, 1e9);
    }

    private double mround(double value, double precision) {
        return (double) Math.round(value * precision) / precision;
    }

}
