package MiniFireForce;

public class FireStation {
    private float xStation;
    private float yStation;
    private int trucks;
    private static int counter = 0;
    private final int id;

    public FireStation(float xStation, float yStation, int trucks) {
        this.xStation = xStation;
        this.yStation = yStation;
        this.trucks = trucks;
        id = ++counter;
    }

    public int getID() {
        return id;
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
        double dx = xFire - xStation;
        double dy = yFire - yStation;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public boolean canDeploy() {
        return trucks > 0;
    }

    public int deployTruck(int requiredTrucks) {
        if (trucks < requiredTrucks) {
            int needed = requiredTrucks - trucks;
            trucks = 0;
            return needed;
        }
        trucks -= requiredTrucks;
        return 0;
    }

    public void retrieveTruck(int trucksReturned) {
        trucks += trucksReturned;
    }
}
