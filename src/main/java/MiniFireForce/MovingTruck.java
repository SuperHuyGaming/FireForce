package MiniFireForce;

public class MovingTruck {
    public enum State { TO_FIRE, EXTINGUISHING, RETURNING }

    private final int stationID;
    private final int fireID;
    private double startX, startY;
    private double destX, destY;
    private long startTime;
    private long travelDurationMs;
    private State state;

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

    public void setState(State newState) {
        this.state = newState;
        this.startTime = System.currentTimeMillis();
    }

    public void setDestination(double x, double y) {
        this.destX = x;
        this.destY = y;
    }

    public void setTravelDuration(long durationMs) {
        this.travelDurationMs = durationMs;
    }

    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public int getStationID() {
        return stationID;
    }

    public int getFireID() {
        return fireID;
    }

    public State getState() {
        return state;
    }

    // Return current X (linear interpolation)
    public double getCurrentX() {
        if (state == State.EXTINGUISHING) {
            return destX; // parked at the fire
        }
        double elapsed = System.currentTimeMillis() - startTime;
        double t = elapsed / (double) travelDurationMs;
        if (t > 1) t = 1;
        return startX + (destX - startX) * t;
    }

    // Return current Y (linear interpolation)
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
