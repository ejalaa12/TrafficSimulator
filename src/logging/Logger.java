package logging;

import com.sun.tools.javac.util.FatalError;
import simulation.Event;
import simulation.SimEngine;

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
    private String statCsvFile;
    private FileWriter writer;
    private FileWriter statWriter;
    private SimEngine simEngine;

    // attributes for filtering
    private ArrayList<String> creatorFilter;
    private ArrayList<String> messageFilters;

    private Logger() {
        // exist only to defeat instanciation
        on = true;
        mlogLevel = LogLevel.INFO;
        csvOn = false;
        csvFile = "./results/logs.csv";
        statCsvFile = "./results/logs_stat.csv";
        try {
            writer = new FileWriter(csvFile);
            CSVUtils.writeLine(writer, Arrays.asList("date", "creator", "message"));
            statWriter = new FileWriter(statCsvFile);
            CSVUtils.writeLine(statWriter, Arrays.asList("date", "creator", "message", "data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        creatorFilter = new ArrayList<>();
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    // TODO: 07/01/2017 addSimEngine to logger to avoid doing simEngine.getCurrentSimTime everywhere when logging

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
    * ##############################################################################################################
    * Log method with level of warning
    * ##############################################################################################################
    */
    /*
    * **************************************************************************************************************
    * DEBUG
    */

    public void logDebug(String creatorName, String message) {
        log(creatorName, simEngine.getCurrentSimTime(), message, LogLevel.DEBUG);
    }

    public void logDebug(String creatorName, LocalDateTime logTime, String message) {
        log(creatorName, logTime, message, LogLevel.DEBUG);
    }
    /*
    * **************************************************************************************************************
    * INFO
    */


    public void logInfo(String creatorName, String message) {
        log(creatorName, simEngine.getCurrentSimTime(), message, LogLevel.INFO);
    }

    public void logInfo(String creatorName, LocalDateTime logTime, String message) {
        log(creatorName, logTime, message, LogLevel.INFO);
    }
    
    /*
    * **************************************************************************************************************
    * EVENT
    */


    public void logEvent(String creatorName, String msg) {
        log(creatorName, simEngine.getCurrentSimTime(), msg, LogLevel.EVENT);
    }

    /*
    * **************************************************************************************************************
    * WARNING
    */

    public void logWarning(String creatorName, String message) {
        log(creatorName, simEngine.getCurrentSimTime(), message, LogLevel.WARNING);
    }

    public void logWarning(String creatorName, LocalDateTime logTime, String message) {
        log(creatorName, logTime, message, LogLevel.WARNING);
    }

    /*
    * **************************************************************************************************************
    * FATAL
    */

    public void logFatal(String creatorName, String message) {
        log(creatorName, simEngine.getCurrentSimTime(), message, LogLevel.FATAL);
        throw new FatalError("Something went wrong");
    }

    public void logFatal(String creatorName, LocalDateTime logTime, String message) {
        log(creatorName, logTime, message, LogLevel.FATAL);
        throw new FatalError("Something went wrong");
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
                if (creatorFilter.isEmpty()) System.out.println(res);
                else if (creatorFilter.contains(creatorName)) System.out.println(res);
            }
        }
        if (csvOn) {
            try {
                CSVUtils.writeLine(writer, Arrays.asList(timestamp, creatorName, message));
                if (logLevel == LogLevel.STATISTICS)
                    CSVUtils.writeLine(statWriter, Arrays.asList(timestamp, creatorName, message));
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

                statWriter.flush();
                statWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSimEngine(SimEngine simEngine) {
        this.simEngine = simEngine;
    }

    public void logStat(String creatorName, String stat_title, String message) {
        mlog(creatorName, simEngine.getCurrentSimTime(), stat_title + ", " + message, LogLevel.STATISTICS);

    }

    public void addCreatorFilter(String creatorFilter) {
        this.creatorFilter.add(creatorFilter);
    }

    public void addMessageFilter(String filter) {
        messageFilters.add(filter);
    }

}
