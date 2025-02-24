package MiniFireForce;

/**
 * Functional interface for logging events in the fire simulation system.
 */
@FunctionalInterface
public interface EventLogger {
    /**
     * Logs a message.
     *
     * @param message the message to log
     */
    void log(String message);
}