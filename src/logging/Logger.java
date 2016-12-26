package logging;

import simulation.Event;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;

/**
 * A Logger which is a Singleton that allows to log all informations
 */
public class Logger {

    private static final DateTimeFormatter logicalDateTimeFormatter;
    private static final DateTimeFormatter logicalTimeFormatter;
    private static final DateTimeFormatter logicalDateFormatter;
    private static Logger instance = null;

    static {
        logicalTimeFormatter = DateTimeFormatter.ISO_TIME;
        logicalDateFormatter = DateTimeFormatter.ofPattern("EEE dd, MMM yyyy");

        DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();
        dtfb.parseCaseInsensitive();
        dtfb.append(logicalDateFormatter);
        dtfb.appendLiteral(" ");
        dtfb.append(logicalTimeFormatter);
        logicalDateTimeFormatter = dtfb.toFormatter();
    }

    private boolean on;

    // File for csv
    private boolean csvOn;
    private String csvFile;
    private FileWriter writer;

    private Logger() {
        // exist only to defeat instanciation
        on = true;
        csvOn = false;
        csvFile = "./results/logs.csv";
        try {
            writer = new FileWriter(csvFile);
            CSVUtils.writeLine(writer, Arrays.asList("date", "creator", "message"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void turnOff() {
        on = false;
    }

    public void turnOn() {
        on = true;
    }

    public void turnCsvOn() {
        csvOn = true;
    }

    public void turnCsvOff() {
        csvOn = false;
    }

    public void log(Event event) {
        mlog(event.getCreator(), event.getScheduledTime(), event.getDescription());
    }

    public void log(String creatorName, LocalDateTime logTime, String message) {
        mlog(creatorName, logTime, message);
    }

    private void mlog(String creatorName, LocalDateTime logTime, String message) {
        String timestamp = logicalDateTimeFormatter.format(logTime);
        String res = String.format("[%s] %-20s: %s", timestamp, creatorName, message);
        if (on) {
            System.out.println(res);
        }
        if (csvOn) {
            try {
                CSVUtils.writeLine(writer, Arrays.asList(timestamp, creatorName, message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(LocalDateTime logTime) {
        System.out.println(logicalDateTimeFormatter.format(logTime));
    }

    public void close() throws IOException {
        if (csvOn) {
            writer.flush();
            writer.close();
        }
    }
}
