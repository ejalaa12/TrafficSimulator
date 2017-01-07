package entities.car;

import simulation.Event;

import java.time.LocalDateTime;

/**
 * The particularity of car event is that they might not be processed
 */
public abstract class CarEvent extends Event {

    protected boolean processed;

    protected CarEvent(String creator, LocalDateTime scheduledTime, String description) {
        super(creator, scheduledTime, description);
        processed = false;
    }

    @Override
    public void doAction() {
        processed = true;
    }

    public boolean wasProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
