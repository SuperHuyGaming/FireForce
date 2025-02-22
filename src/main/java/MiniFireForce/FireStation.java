package MiniFireForce;

public class FireStation {
    private int xStation;
    private int yStation;
    private int trucks;

    // Take the location algorithm of Google Maps to estimate the distance

    public FireStation(int xStation, int yStation, int trucks) {
        this.xStation = xStation;
        this.yStation = yStation;
        this.trucks = trucks;
    }

    public double calculateDistance(int xFire, int yFire) {
        return Math.sqrt((xFire - xStation) * (xFire - xStation) + (yFire - yStation) * (yFire - yStation));
    }

    public void canDeploy(){
    }


}