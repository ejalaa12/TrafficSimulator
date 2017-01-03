package logging;

import simulation.Entity;
import simulation.Event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Logger which is a Singleton that allows to log all messages
 */
public class Logger {

    private static final DateTimeFormatter logicalDateTimeFormatter;
    private static final DateTimeFormatter logicalTimeFormatter;
    private static final DateTimeFormatter logicalDateFormatter;
    private static Logger instance = null;

    static {
        logicalTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        logicalDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
//        logicalDateFormatter = DateTimeFormatter.ofPattern("EEE dd, MMM yyyy");

        DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();
        dtfb.parseCaseInsensitive();
        dtfb.append(logicalDateFormatter);
        dtfb.appendLiteral(" ");
        dtfb.append(logicalTimeFormatter);
        logicalDateTimeFormatter = dtfb.toFormatter();
    }

    private LogLevel mlogLevel;
    private boolean on;

    // File for csv
    private boolean csvOn;
    private String csvFile;
    private ArrayList<String> statFiles;
    private FileWriter writer;

    private Logger() {
        // exist only to defeat instanciation
        on = true;
        mlogLevel = LogLevel.INFO;
        csvOn = false;
        csvFile = "./results/logs.csv";
        statFiles = new ArrayList<>();
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

    public void setLogLevel(LogLevel logLevel) {
        mlogLevel = logLevel;
    }

    /*
    * ****************************************************************************************************************
    * Log methods
    * ****************************************************************************************************************
    */
    public void log(Event event) {
        mlog(event.getCreator(), event.getScheduledTime(), event.getDescription(), LogLevel.EVENT);
    }

    public void log(String creatorName, LocalDateTime logTime, String message, LogLevel logLevel) {
        mlog(creatorName, logTime, message, logLevel);
    }

    private void mlog(String creatorName, LocalDateTime logTime, String message, LogLevel logLevel) {
        String timestamp = logicalDateTimeFormatter.format(logTime);
        String res = String.format("%s[%-10s] [%s] %-20s: %s", logLevel.getColor(), logLevel, timestamp, creatorName, message);
        if (on) {
            if (logLevel.ordinal() >= mlogLevel.ordinal()) {
                System.out.println(res);
            }
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

    public void close() {
        if (csvOn) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void logStat(Entity creator, String[] statisticsMessages) {
        String csvStatFile = String.format("./results/logs_%s.csv", creator.getClass().getName());
        if (!statFiles.contains(csvStatFile))
            statFiles.add(csvStatFile);
        String timestamp = logicalDateTimeFormatter.format(creator.getSimEngine().getCurrentSimTime());
        FileWriter stat_writer;
        File stat_writer_file = new File(csvStatFile);
        try {
            // FIXME: 03/01/2017 filewriter either overrides a file with an empty or append to an old file
            if (!statFiles.contains(stat_writer_file)) {
                stat_writer = new FileWriter(csvStatFile, false);
            } else {
                stat_writer = new FileWriter(csvStatFile, true);
            }
            for (String msg : statisticsMessages) {
                CSVUtils.writeLine(stat_writer, Arrays.asList(timestamp, creator.getName(), msg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
