package simulation;

import logging.LogLevel;
import logging.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The simulator engine that handles the list of event
 */
public class SimEngine {

    private static final String className = "SimEngine";
    private static final long MAX_LOOPS = 1000000L;

    private ArrayList<Event> events;
    private LocalDateTime startSimTime, currentSimTime, endSimTime;
    private int loops;
    private Random random;

    public SimEngine(long seed, LocalDateTime startSimTime, LocalDateTime endSimTime) {
        this.events = new ArrayList<>();
        this.random = new Random(seed);
        this.startSimTime = startSimTime;
        this.currentSimTime = startSimTime;
        this.endSimTime = endSimTime;
        this.loops = 0;
        Logger.getInstance().log(className, this.currentSimTime, "Simulation initialized !", LogLevel.INFO);
        Logger.getInstance().setSimEngine(this);
    }

    /**
     * Add event to the event queue and sort it to have event in chronological order
     *
     * @param newEvent the event to add
     */
    public void addEvent(Event newEvent) {
        if (!this.events.contains(newEvent)) {
            newEvent.setPostedTime(currentSimTime);
            this.events.add(newEvent);
            Collections.sort(this.events);
        } else {
            Logger.getInstance().log(newEvent.getCreator(), currentSimTime, "Trying to add already existing event (dismissed)", LogLevel.WARNING);
        }
    }

    /**
     * Remove an event from the queue
     *
     * @param event
     */
    public void removeEvent(Event event) {
        if (events.contains(event)) {
            events.remove(event);
        } else {
            Logger.getInstance().log(className, currentSimTime, "Trying to remove non-existing event", LogLevel.WARNING);
        }
    }

    /**
     * Simulation loop
     */
    public void loop() {
        Logger.getInstance().log(className, this.currentSimTime, "Simulation started !", LogLevel.EVENT);
        while (!simHasEnded()) {
            simStep();
        }
        Logger.getInstance().log(className, this.currentSimTime, "Simulation has ended !", LogLevel.EVENT);
        Logger.getInstance().log(className, this.currentSimTime, "Number of events: " + loops, LogLevel.EVENT);
    }

    private void simStep() {
        // Get first element
        Event currentEvent = this.events.get(0);
        this.events.remove(0);
        // break if the event is after endSimTime
        if (currentEvent.getScheduledTime().isAfter(endSimTime)) {
            return;
        }
        Logger.getInstance().log(currentEvent);
        // Update simulation time with current event time
        currentSimTime = currentEvent.getScheduledTime();
        // Do the action of this event and get all generated events
        currentEvent.doAction();
        // Increment loops
        this.loops += 1;
    }

    private boolean simHasEnded() {
        return events.isEmpty() || currentSimTime.isAfter(endSimTime) || loops >= MAX_LOOPS;
    }

    public LocalDateTime getCurrentSimTime() {
        return currentSimTime;
    }

    public Random getRandom() {
        return random;
    }
}
