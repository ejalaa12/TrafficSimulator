package logging;

/**
 * An enumeration of the possible log levels.
 * This enumeration also includes the color for printing this level of log
 */
public enum LogLevel {

//    ANSI_RESET = "\u001B[0m";
//    ANSI_BLACK = "\u001B[30m";
//    ANSI_RED = "\u001B[31m";
//    ANSI_GREEN = "\u001B[32m";
//    ANSI_YELLOW = "\u001B[33m";
//    ANSI_BLUE = "\u001B[34m";
//    ANSI_PURPLE = "\u001B[35m";
//    ANSI_CYAN = "\u001B[36m";
//    ANSI_WHITE = "\u001B[37m";


    // !! WORKS ONLY IF THE TERMINAL SUPPORT COLORING !!
    DEBUG("\u001B[32m"),
    INFO("\u001B[37m"),
    EVENT("\u001B[0m"),
    WARNING("\u001B[33m"),
    FATAL("\u001B[31m"),
    STATISTICS("\u001B[36m");


    private String color;

    LogLevel(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
