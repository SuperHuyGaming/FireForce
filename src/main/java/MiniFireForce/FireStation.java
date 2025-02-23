package MiniFireForce;

public class FireStation {
    private float xStation;
    private float yStation;
    private int trucks;
    private  int id = 0;
    private static int count;

    public FireStation(float xStation, float yStation, int trucks) {
        this.xStation = xStation;
        this.yStation = yStation;
        this.trucks = trucks;
        id= ++count;
    }

    public int getID() {
        return this.id;
    }

    public float getX() {
        return xStation;
    }

    public float getY() {
        return yStation;
    }

    public int getTrucks() {
        return trucks;
    }

    public double calculateDistance(float xFire, float yFire) {
        return Math.sqrt((xFire - xStation) * (xFire - xStation) + (yFire - yStation) * (yFire - yStation));
    }

    public boolean canDeploy() {
        return trucks > 0;
    }

    // Deploy the given number of trucks.
    // If not enough trucks are available, set trucks to 0 and return the extra needed.
    public int deployTruck(int requiredTrucks) {
        if (trucks < requiredTrucks) {
            int temp = requiredTrucks - trucks;
            trucks = 0;
            return temp;
        }
        trucks -= requiredTrucks;
        return 0;
    }

    public void retrieveTruck(int trucks) {
        this.trucks += trucks;
    }
}
