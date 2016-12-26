package simulation;

import java.time.LocalDateTime;

/**
 * Event class represents a simulation event.
 * It has:
 * - an associated action that must be implemented.
 * - a scheduledTime that corresponds to the time when the event will happen
 */
public abstract class Event implements Comparable<Event> {

    /*
    * ********************************************************************
    * ATTRIBUTES
    * ********************************************************************
    */

    protected LocalDateTime scheduledTime;
    private String description, creator;
    /*
    Constructor
     */
    protected Event(String creator, LocalDateTime scheduledTime, String description) {
        // TODO: 04/12/2016 add postedTime in parameters and attributes
        super();
        this.creator = creator;
        this.scheduledTime = scheduledTime;
        this.description = description;
    }

    /*
    Action to be done by the simulator engine
     */
    public abstract void doAction();

    /*
    Each event is comparable to another, by comparing their scheduled time
     */
    @Override
    public int compareTo(Event other) {
        return this.scheduledTime.compareTo(other.scheduledTime);
    }

    /*
    * ********************************************************************
    * GETTER AND SETTERS
    * ********************************************************************
    */
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String res;
        res = "EVENT:";
        res += "\t CREATOR: " + this.creator;
        res += "\t Description: " + this.description;
        res += "\t TIME TO BE HANDLED: " + this.scheduledTime.toString();
        return res;
    }

}
