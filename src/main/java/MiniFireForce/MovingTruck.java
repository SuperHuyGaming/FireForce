package MiniFireForce;

public class MovingTruck {
    public enum State { TO_FIRE, EXTINGUISHING, RETURNING }

    private int stationID;
    private int fireID;
    private double startX, startY;
    private double destinationX, destinationY;
    private long startTime;
    private long travelDuration;
    private State state;

    public MovingTruck(int stationID, int fireID,
                       double startX, double startY,
                       double destinationX, double destinationY,
                       long travelDuration, State state) {
        this.stationID = stationID;
        this.fireID = fireID;
        this.startX = startX;
        this.startY = startY;
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.travelDuration = travelDuration;
        this.state = state;
        this.startTime = System.currentTimeMillis();
    }

    public void setState(State newState) {
        this.state = newState;
        this.startTime = System.currentTimeMillis();
    }

    public void setDestination(double x, double y) {
        this.destinationX = x;
        this.destinationY = y;
    }

    public void setTravelDuration(long durationMs) {
        this.travelDuration = durationMs;
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
            return destinationX;
        }
        double t = (System.currentTimeMillis() - startTime) / (double) travelDuration;
        if (t > 1) t = 1;
        return startX + (destinationX - startX) * t;
    }

    // Return current Y (linear interpolation)
    public double getCurrentY() {
        if (state == State.EXTINGUISHING) {
            return destinationY;
        }
        double t = (System.currentTimeMillis() - startTime) / (double) travelDuration;
        if (t > 1) t = 1;
        return startY + (destinationY - startY) * t;
    }
}
