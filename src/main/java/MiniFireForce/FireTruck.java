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


}
