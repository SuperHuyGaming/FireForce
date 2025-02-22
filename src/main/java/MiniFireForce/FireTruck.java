package MiniFireForce;

public class FireTruck {
    private static int id = 0;
    private int velocity;
    private int extinguishTime;

    public FireTruck(int velocity, int extinguishTime) {
        this.velocity = velocity;
        this.extinguishTime = extinguishTime;
        id++;
    }

    public int getVelocity() {
        return this.velocity;
    }

    public int extinguishTime() {
        return this.extinguishTime;
    }

    public double calculateTravelTime(FireStation station, Fire fire, double truckSpeed) {
        double distance = station.calculateDistance(fire.getX(), fire.getY());
        return distance / truckSpeed;  // time = distance / speed
    }

    public double calculateExtinguishingTime(Fire fire, int numTrucks) {
        return (fire.getSeverity() * 5.0) / numTrucks; // More trucks = shorter time
    }
    
    


}
