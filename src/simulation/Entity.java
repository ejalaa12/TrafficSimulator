package simulation;

/**
 * An Entity creates Events that are going to be handled by the SimEngine
 */
public interface Entity {

    /**
     * Initialize the Entity (generally by adding the first event of this entity to the simEngine)
     */
    void init();

    /**
     * Print statistics about the entity
     */
    void logStats();

    /**
     * Each entity has a name
     *
     * @return entity name
     */
    String getName();


}
