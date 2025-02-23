package MiniFireForce;

import java.time.LocalDateTime;

import javafx.util.Pair;

public class Fire {
    private float x;
    private float y;
    private int severity;
    private LocalDateTime time;
    private int spread = 0;
    private static int id = 0;

    /**
     * Constructor for Fire class
     * @param x        x coordinate
     * @param y        y coordinate
     * @param severity severity of the fire
     * @param time     time of the fire
     */
    public Fire(float x, float y, int severity, LocalDateTime time, int spread) {
        this.x = x;
        this.y = y;
        this.severity = severity;
        this.time = time;
        this.spread = spread;
        id++;
    }

    public int getID() {
        return this.id;
    }

    /**
     * Get the x-coordinate of the fire
     */
    public float getX() {
        return x;
    }

    /**
     * Get the y-coordinate of the fire.
     */
    public float getY() {
        return y;
    }
    /**
     * Get the coordinate xy.
     * @return a point (x,y)
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Pair<Float,Float> getXY() {
        return new Pair(x,y);
    }

    /**
     * Get the severity of the fire
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Get the time of the fire
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Set the x-coordinate of the fire
     */
    public void setX(float newX) {
        this.x = newX;
    }

    /**
     * Set the y-coordinate of the fire
     */
    public void setY(float newY) {
        this.y = newY;
    }

    /**
     * Set the time of the fire
     */
    public void setTime(LocalDateTime newTime) {
        this.time = newTime;
    }

    /**
     * Set the severity of the fire
     */
    public void updateSeverity(int newSev) {
        this.severity = newSev;
    }

    /**
     * Return true if the fire is active, otherwise
     */
    public boolean isActive() {
        return this.severity > 0;
    }

    /**
     * Reset the spread rate
     */
    public void resetSpread() {
        this.spread = 0;
    }

    /**
     * Increase spread
     */
    public void increaseSpread(int amount) {
        this.spread = Math.min(this.spread + amount, 100); // Increase the fire's spread percentage by the given amount
        // ensuring it does not exceed 100
    }

    /**
     * Get spread value
     */
    public int getSpread() {
        return this.spread;
    }
}
