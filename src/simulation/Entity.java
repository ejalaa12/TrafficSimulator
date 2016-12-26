package simulation;

/**
 * An Entity creates Events that are going to be handled by the SimEngine
 */
public interface Entity {

    void init();

    void printStats();

    String getName();


}
