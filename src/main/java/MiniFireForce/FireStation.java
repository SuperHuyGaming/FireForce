package MiniFireForce;

public class FireStation {
    private float xStation;
    private float yStation;
    private int trucks;

    // private PriorityQueue<Integer> queue;
    private static int id;

    // Take the location algorithm of Google Maps to estimate the distance

    public FireStation(float xStation, float yStation, int trucks) {
        this.xStation = xStation;
        this.yStation = yStation;
        this.trucks = trucks;
        id++;
    }

    //Getters
    public int getID() {
        return FireStation.id;
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

    // Return the number of needed trucks after deploy
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
