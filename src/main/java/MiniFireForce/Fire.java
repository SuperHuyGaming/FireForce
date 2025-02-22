package MiniFireForce;
import java.time.LocalDateTime;

import javafx.util.Pair;
public class Fire {
    private float x;
    private float y;
    private int severity;
    private LocalDateTime time;
    private static int id;

    /**
     * Constructor for Fire class
     * @param x        x coordinate
     * @param y        y coordinate
     * @param severity severity of the fire
     * @param time     time of the fire
     */
    public Fire(float x, float y, int severity, LocalDateTime time) {
        this.x = x;
        this.y = y;
        this.severity = severity;
        this.time = time;
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
     * Remove the fire
     */
    public void removeFire() {
        this.severity = 0;
        this.time = null;
        this.x = 0;
        this.y = 0;
    }
}
