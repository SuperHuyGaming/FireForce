package MiniFireForce;

public class MovingTruck {
    public enum State { TO_FIRE, EXTINGUISHING, RETURNING }
    
    private int stationID;
    private int fireID;
    private double startX, startY;
    private double destinationX, destinationY;
    private long startTime;       // in milliseconds
    private long travelDuration;  // in milliseconds
    private State state;
    
    public MovingTruck(int stationID, int fireID, double startX, double startY,
                       double destinationX, double destinationY, long travelDuration, State state) {
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
    
    public void setDestination(double newDestinationX, double newDestinationY) {
        this.destinationX = newDestinationX;
        this.destinationY = newDestinationY;
    }
    
    public void setTravelDuration(long travelDuration) {
        this.travelDuration = travelDuration;
    }
    
    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void setFireID(int newFireID) {
        this.fireID = newFireID;
    }
    
    public double getCurrentX() {
        if(state == State.EXTINGUISHING) {
            return destinationX;
        }
        double t = (System.currentTimeMillis() - startTime) / (double) travelDuration;
        if (t > 1) t = 1;
        return startX + (destinationX - startX) * t;
    }
    
    public double getCurrentY() {
        if(state == State.EXTINGUISHING) {
            return destinationY;
        }
        double t = (System.currentTimeMillis() - startTime) / (double) travelDuration;
        if (t > 1) t = 1;
        return startY + (destinationY - startY) * t;
    }
    
    public boolean hasArrived() {
        return System.currentTimeMillis() - startTime >= travelDuration;
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
}
