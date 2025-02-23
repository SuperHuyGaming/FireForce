package MiniFireForce;

/**
 * The {@code MovingTruck} class represents a fire truck moving between a station and a fire.
 * It supports animation by tracking the truck’s position over time as it travels.
 */
public class MovingTruck {
    /**
     * Enumeration representing the different states of a moving truck.
     */
    public enum State { TO_FIRE, EXTINGUISHING, RETURNING }

    private final int stationID;
    private final int fireID;
    private double startX, startY;
    private double destX, destY;
    private long startTime;
    private long travelDurationMs;
    private State state;

    /**
     * Constructs a new {@code MovingTruck} instance.
     *
     * @param stationID       the ID of the fire station sending the truck
     * @param fireID          the ID of the fire the truck is responding to
     * @param startX          the x-coordinate of the truck’s starting position
     * @param startY          the y-coordinate of the truck’s starting position
     * @param destX           the x-coordinate of the fire's location
     * @param destY           the y-coordinate of the fire's location
     * @param travelDurationMs the estimated travel time in milliseconds
     * @param initialState    the initial state of the truck
     */
    public MovingTruck(int stationID, int fireID,
                       double startX, double startY,
                       double destX, double destY,
                       long travelDurationMs,
                       State initialState) {
        this.stationID = stationID;
        this.fireID = fireID;
        this.startX = startX;
        this.startY = startY;
        this.destX = destX;
        this.destY = destY;
        this.travelDurationMs = travelDurationMs;
        this.state = initialState;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Sets the truck's state and resets the travel timer.
     *
     * @param newState the new state of the truck
     */
    public void setState(State newState) {
        this.state = newState;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Sets the truck’s destination coordinates.
     *
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void setDestination(double x, double y) {
        this.destX = x;
        this.destY = y;
    }

    /**
     * Sets the truck’s travel duration in milliseconds.
     *
     * @param durationMs the new travel duration
     */
    public void setTravelDuration(long durationMs) {
        this.travelDurationMs = durationMs;
    }

    /**
     * Resets the truck’s start time to the current system time.
     */
    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    /** @return the ID of the station the truck belongs to */
    public int getStationID() {
        return stationID;
    }

    /** @return the ID of the fire the truck is responding to */
    public int getFireID() {
        return fireID;
    }

    /** @return the current state of the truck */
    public State getState() {
        return state;
    }

    /**
     * Computes the truck’s current x-coordinate using linear interpolation.
     *
     * @return the truck’s x-coordinate
     */
    public double getCurrentX() {
        if (state == State.EXTINGUISHING) {
            return destX;
        }
        double elapsed = System.currentTimeMillis() - startTime;
        double t = elapsed / (double) travelDurationMs;
        if (t > 1) t = 1;
        return startX + (destX - startX) * t;
    }

    /**
     * Computes the truck’s current y-coordinate using linear interpolation.
     *
     * @return the truck’s y-coordinate
     */
    public double getCurrentY() {
        if (state == State.EXTINGUISHING) {
            return destY;
        }
        double elapsed = System.currentTimeMillis() - startTime;
        double t = elapsed / (double) travelDurationMs;
        if (t > 1) t = 1;
        return startY + (destY - startY) * t;
    }
}
