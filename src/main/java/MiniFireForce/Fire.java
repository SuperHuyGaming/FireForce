package MiniFireForce;

import java.util.Calendar;

public class Fire {
    private int x;
    private int y;
    private String severity;
    private Calendar time;

    /**
     * Constructor for Fire class
     *
     * @param x        x coordinate
     * @param y        y coordinate
     * @param severity severity of the fire
     * @param time     time of the fire
     */
    public Fire(int x, int y, String severity, Calendar time) {
        this.x = x;
        this.y = y;
        this.severity = severity;
        this.time = time;
    }

    /**
     * Get the x-coordinate of the fire
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y-coordinate of the fire
     */
    public int getY() {
        return y;
    }

    /**
     * Get the severity of the fire
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Get the time of the fire
     */
    public Calendar getTime() {
        return time;
    }

    /**
     * Set the x-coordinate of the fire
     */
    public void setX(int newX) {
        this.x = newX;
    }

    /**
     * Set the y-coordinate of the fire
     */
    public void setY(int newY) {
        this.y = newY;
    }

    /**
     * Set the time of the fire
     */
    public void setTime(Calendar newTime) {
        this.time = newTime;
    }

    /**
     * Set the severity of the fire
     */
    public void updateSeverity(String newSev) {
        this.severity = newSev;
    }
}
