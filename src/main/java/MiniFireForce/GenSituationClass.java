package MiniFireForce;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages fire emergencies by tracking fires, fire stations, and deploying fire trucks.
 */
public class GenSituationClass {
    private Map<Integer, Fire> activeFire;
    private Map<Integer, FireStation> fireStations;

    public GenSituationClass() {
        this.activeFire = new HashMap<>();
        this.fireStations = new HashMap<>();
    }

    public Map<Integer, Fire> getActiveFires() {
        return activeFire;
    }

    public void addActiveFire(Fire fire) {
        this.activeFire.put(fire.getID(), fire);
    }

    public Map<Integer, FireStation> getFireStations() {
        return fireStations;
    }

    public void addFireStation(FireStation fireStation) {
        this.fireStations.put(fireStation.getID(), fireStation);
    }

    /**
     * Generates a random fire within a (-1000, 1000) coordinate range and adds it to active fires.
     */
    public void generateFire() {
        Random random = new Random();
        int x = random.nextInt(-1000, 1000);
        int y = random.nextInt(-1000, 1000);
        int severity = random.nextInt(10) + 1;
        LocalDateTime time = LocalDateTime.now();

        Fire generateFire = new Fire(x, y, severity, time);
        activeFire.put(generateFire.getID(), generateFire);
    }

    /**
     * Finds the nearest fire station to a given fire.
     */
    public FireStation findFireStation(Fire fire) {
        FireStation nearestStation = null;
        double minDistance = Double.MAX_VALUE;  // Use double for distance calculation

        for (FireStation station : fireStations.values()) {
            double distance = station.calculateDistance(fire.getX(), fire.getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        return nearestStation; // Returns the closest station or null if none exist
    }

    /**
     * Finds the most severe fire, prioritizing older ones in case of ties.
     */
    public Fire compareFires() {
        Fire mostSevereFire = null;

        for (Fire fire : activeFire.values()) {
            if (mostSevereFire == null ||
                    fire.getSeverity() > mostSevereFire.getSeverity() ||
                    (fire.getSeverity() == mostSevereFire.getSeverity() && fire.getTime().isBefore(mostSevereFire.getTime()))) {
                mostSevereFire = fire;
            }
        }
        return mostSevereFire;
    }

    /**
     * Deploys fire trucks from the nearest station. Removes fire if deployment succeeds.
     */
    public void deployFireTrucks(Fire fire) {
        FireStation station = findFireStation(fire);
        if (station == null) {
            return;
        }

        int trucksNeeded = Math.min(fire.getSeverity() / 2 + 1, station.getTrucks());
        station.canDeploy(trucksNeeded);
        boolean deployed = station.deployTruck(trucksNeeded);

        if (deployed) {
            activeFire.remove(fire.getID());
        }

    }


}
