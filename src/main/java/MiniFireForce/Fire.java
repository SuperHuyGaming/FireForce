package MiniFireForce;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Represents a fire event in the simulation.
 * Fires have coordinates, severity levels, and timestamps.
 */
public class Fire {
    private float x;
    private float y;
    private int severity;
    private LocalDateTime time;
    private static int count = 0;
    private final int id;

    /**
     * Creates a fire with given location, severity, and time.
     *
     * @param x        the x-coordinate of the fire
     * @param y        the y-coordinate of the fire
     * @param severity the severity level (1-10)
     * @param time     the timestamp of the fire
     */
    public Fire(float x, float y, int severity, LocalDateTime time) {
        this.x = x;
        this.y = y;
        this.severity = severity;
        this.time = time;
        id = ++count;
    }

    /** @return the unique ID of the fire */
    public int getID() { return id; }

    /** @return the x-coordinate of the fire */
    public float getX() { return x; }

    /** @return the y-coordinate of the fire */
    public float getY() { return y; }

    /** @return the coordinates of the fire as a Map entry */
    public Map.Entry<Float, Float> getXY() { return new AbstractMap.SimpleEntry<>(x, y); }

    /** @return the severity level of the fire */
    public int getSeverity() { return severity; }

    /** @return the timestamp of the fire */
    public LocalDateTime getTime() { return time; }

    /**
     * Sets a new x-coordinate for the fire.
     *
     * @param newX the new x-coordinate
     */
    public void setX(float newX) { this.x = newX; }

    /**
     * Sets a new y-coordinate for the fire.
     *
     * @param newY the new y-coordinate
     */
    public void setY(float newY) { this.y = newY; }

    /**
     * Sets a new timestamp for the fire.
     *
     * @param newTime the new timestamp
     */
    public void setTime(LocalDateTime newTime) { this.time = newTime; }

    /**
     * Updates the severity level of the fire.
     *
     * @param newSev the new severity level
     */
    public void updateSeverity(int newSev) { this.severity = newSev; }

    /**
     * Increases the severity of the fire randomly by 0 or 1.
     * Ensures severity does not exceed 10.
     */
    public void spreadFire() {
        if (severity < 10) {
            severity += (int) (Math.random() * 2);
        }
    }
}
