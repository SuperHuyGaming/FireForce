package MiniFireForce;

/**
 * Represents a fire station in the simulation.
 * Fire stations store fire trucks and deploy them when needed.
 */
public class FireStation {
    private float xStation;
    private float yStation;
    private int trucks;
    private static int counter = 0;
    private final int id;

    /**
     * Creates a fire station at a specified location with a number of trucks.
     *
     * @param xStation the x-coordinate of the station
     * @param yStation the y-coordinate of the station
     * @param trucks   the number of fire trucks available
     */
    public FireStation(float xStation, float yStation, int trucks) {
        this.xStation = xStation;
        this.yStation = yStation;
        this.trucks = trucks;
        id = ++counter;
    }

    /** @return the unique ID of the fire station */
    public int getID() { return id; }

    /** @return the x-coordinate of the fire station */
    public float getX() { return xStation; }

    /** @return the y-coordinate of the fire station */
    public float getY() { return yStation; }

    /** @return the number of available fire trucks */
    public int getTrucks() { return trucks; }

    /**
     * Calculates the distance from the fire station to a given fire location.
     *
     * @param xFire the x-coordinate of the fire
     * @param yFire the y-coordinate of the fire
     * @return the Euclidean distance to the fire
     */
    public double calculateDistance(float xFire, float yFire) {
        double dx = xFire - xStation;
        double dy = yFire - yStation;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if the fire station has at least one available fire truck.
     *
     * @return {@code true} if at least one truck is available, otherwise {@code false}
     */
    public boolean canDeploy() { return trucks > 0; }

    /**
     * Deploys fire trucks for fire suppression.
     * If not enough trucks are available, deploys all and returns the shortfall.
     *
     * @param requiredTrucks the number of trucks needed
     * @return the number of additional trucks needed
     */
    public int deployTruck(int requiredTrucks) {
        if (trucks < requiredTrucks) {
            int needed = requiredTrucks - trucks;
            trucks = 0;
            return needed;
        }
        trucks -= requiredTrucks;
        return 0;
    }

    /**
     * Returns fire trucks back to the station.
     *
     * @param trucksReturned the number of trucks returning
     */
    public void retrieveTruck(int trucksReturned) {
        trucks += trucksReturned;
    }
}
