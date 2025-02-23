package MiniFireForce;

/**
 * Represents a fire truck that can travel to fires and extinguish them.
 */
public class FireTruck {
    private static int id = 0;
    private int velocity;
    private int extinguishTime;

    /**
     * Creates a fire truck with specified velocity and extinguishing time.
     *
     * @param velocity       the speed of the fire truck
     * @param extinguishTime the time required to extinguish a fire
     */
    public FireTruck(int velocity, int extinguishTime) {
        this.velocity = velocity;
        this.extinguishTime = extinguishTime;
        id++;
    }

    /** @return the velocity of the fire truck */
    public int getVelocity() { return this.velocity; }

    /** @return the time required to extinguish a fire */
    public int extinguishTime() { return this.extinguishTime; }

    /**
     * Calculates the travel time for a fire truck to reach a fire.
     *
     * @param station    the fire station where the truck is deployed from
     * @param fire       the fire to reach
     * @param truckSpeed the speed of the truck
     * @return the travel time in seconds
     */
    public double calculateTravelTime(FireStation station, Fire fire, double truckSpeed) {
        double distance = station.calculateDistance(fire.getX(), fire.getY());
        return distance / truckSpeed;
    }

    /**
     * Calculates the extinguishing time based on the fire severity and number of trucks.
     *
     * @param fire       the fire to extinguish
     * @param numTrucks  the number of trucks deployed
     * @return the estimated extinguishing time in seconds
     */
    public double calculateExtinguishingTime(Fire fire, int numTrucks) {
        return (fire.getSeverity() * 5.0) / numTrucks;
    }
}
