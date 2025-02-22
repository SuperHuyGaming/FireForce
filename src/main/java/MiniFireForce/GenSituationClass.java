package MiniFireForce;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenSituationClass {
    private final Map<Integer, Fire> activeFire;
    private final Map<Integer, FireStation> fireStations;

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

    public void generateFire() {
        Random random = new Random();
        int x = random.nextInt(-1000, 1000);
        int y = random.nextInt(-1000, 1000);
        int severity = random.nextInt(10) + 1;
        LocalDateTime time = LocalDateTime.now();

        Fire generateFire = new Fire(x, y, severity, time);
        activeFire.put(generateFire.getID(), generateFire);
    }


    public FireStation findFireStation(Fire fire) {
        FireStation nearestStation = null;
        double minDistance = Double.MAX_VALUE;  // Use double for distance calculation

        for (FireStation station : fireStations.values()) {
            if (!station.canDeploy()) {
                continue;
            }

            double distance = station.calculateDistance(fire.getX(), fire.getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        return nearestStation; // Returns the closest station or null if none exist
    }

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

    public void deployFireTrucks(Fire fire) {
        FireStation station = findFireStation(fire);
        if (station == null) {
            return;
        }

        int trucksNeeded = Math.min(fire.getSeverity() / 2 + 1, station.getTrucks());
        station.deployTruck(trucksNeeded);


        boolean deployed = trucksNeeded == 0;
        if (deployed) {
            activeFire.remove(fire.getID());
        }
    }

    // Put down a fire and return deployed trucks
    public void removeFire(Fire fire) {
        activeFire.remove(fire.getID());
    }
}
