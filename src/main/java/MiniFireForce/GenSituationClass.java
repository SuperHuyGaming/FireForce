package MiniFireForce;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GenSituationClass {
    private final Map<Integer, Fire> activeFire;
    private final Map<Integer, FireStation> fireStations;
    private TreeMap<Double, FireStation> distances;
    private ScheduledExecutorService scheduler;

    /**
     * Spread fire to a nearby location
     */
    private void spreadFire(Fire fire) {
        Random random = new Random();
        int newX = (int) fire.getX() + random.nextInt(100) - 50;
        int newY = (int) fire.getX() + random.nextInt(100) - 50;
        int newSeverity = Math.max(1, fire.getSeverity() - 1); // New fire is slightly weaker
        LocalDateTime newTime = LocalDateTime.now();

        Fire newFire = new Fire(newX, newY, newSeverity, newTime);
        addActiveFire(newFire);
        System.out.println("🔥 New fire spread to (" + newX + ", " + newY + ")");
    }

    public GenSituationClass() {
        this.activeFire = new HashMap<>();
        this.fireStations = new HashMap<>();
        this.distances = new TreeMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        startFireTimer(); // Start automatic fire spreading
    }

    public Map<Integer, Fire> getActiveFires() {
        return activeFire;
    }

    public void addActiveFire(Fire fire) {
        this.activeFire.put(fire.getID(), fire);
        for (FireStation fs : fireStations.values()) {
            distances.put(fs.calculateDistance(fire.getX(), fire.getY()), fs);
        }
    }

    public Map<Integer, FireStation> getFireStations() {
        return fireStations;
    }

    public void addFireStation(FireStation fireStation) {
        this.fireStations.put(fireStation.getID(), fireStation);
    }

    public void generateFire() {
        Random random = new Random();
        int x = random.nextInt(2000) - 1000;
        int y = random.nextInt(2000) - 1000;
        int severity = random.nextInt(10) + 1;
        LocalDateTime time = LocalDateTime.now();

        Fire generateFire = new Fire(x, y, severity, time);
        activeFire.put(generateFire.getID(), generateFire);
    }

    public List<FireStation> findFireStation(Fire fire) {
        ArrayList<FireStation> nearestStations = new ArrayList<>();

        for (Double distance : distances.keySet()) {
            nearestStations.add(distances.get(distance));
        }
        return nearestStations; // Returns the list of stations sorted by distances from the fire
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
        // Calculate number of needed trucks
        int trucksNeeded = fire.getSeverity() / 2 + 1;
        List<FireStation> nearestStations = findFireStation(fire);

        // Continue searching for close stations that have trucks available
        int stationIndex = 0;

        while (trucksNeeded > 0) {
            // Record the currently nearest station
            FireStation nearest = nearestStations.get(stationIndex);

            if (nearest.canDeploy()) {
                trucksNeeded = nearest.deployTruck(trucksNeeded);
            } else {
                stationIndex++;
            }
        }
        removeFire(fire);
    }

    // Put down a fire and return deployed trucks
    public void removeFire(Fire fire) {
        activeFire.remove(fire.getID());
    }

    /**
     * Start the fire time, each 20 seconds 0-10% spread increase is implemented
     */
    public void startFireTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            if (activeFire.isEmpty()) {
                scheduler.shutdown();
                return;
            }

            Random random = new Random();
            for (Fire fire : new ArrayList<>(activeFire.values())) {
                fire.spreadFire();

                // **New Fire Spread Mechanism**
                if (random.nextDouble() < 0.3) {  // 30% chance to spread
                    spreadFire(fire);
                }
            }
        }, 20, 20, TimeUnit.SECONDS);
    }
}