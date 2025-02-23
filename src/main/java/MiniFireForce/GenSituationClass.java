package MiniFireForce;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class GenSituationClass {
    private final Map<Integer, Fire> activeFires;
    private final Map<Integer, FireStation> stations;
    private final List<MovingTruck> movingTrucks; // trucks in motion
    private ScheduledExecutorService scheduler;
    private EventLogger eventLogger;
    private volatile boolean paused = false;

    // For controlling truck speed (in "units" per second)
    private static final double TRUCK_SPEED = 50.0;

    public GenSituationClass() {
        activeFires = new HashMap<>();
        stations = new HashMap<>();
        movingTrucks = new ArrayList<>();

        // Start tasks
        scheduler = Executors.newScheduledThreadPool(4);
        startStationSpawner();
        startFireSpawner();
        startAutoDeploy();
        startFireTimer();
    }

    // ================== Event Logger and Pause ===================

    public void setEventLogger(EventLogger logger) {
        this.eventLogger = logger;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (eventLogger != null) {
            eventLogger.log(paused ? "Simulation paused." : "Simulation resumed.");
        }
    }

    public boolean isPaused() {
        return paused;
    }

    // ================== Accessors ===================

    public Map<Integer, Fire> getActiveFires() {
        return activeFires;
    }

    public Map<Integer, FireStation> getFireStations() {
        return stations;
    }

    public List<MovingTruck> getMovingTrucks() {
        synchronized (movingTrucks) {
            return new ArrayList<>(movingTrucks);
        }
    }

    // ================== Spawning Logic ===================

    private void startStationSpawner() {
        // Add a new FireStation every 30 seconds
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                Random rand = new Random();
                float x = rand.nextInt(2000) - 1000;
                float y = rand.nextInt(2000) - 1000;
                int trucks = rand.nextInt(5) + 1; // 1..5 trucks
                FireStation st = new FireStation(x, y, trucks);
                stations.put(st.getID(), st);
                if (eventLogger != null) {
                    eventLogger.log("New station spawned: ID " + st.getID()
                            + " at (" + st.getX() + ", " + st.getY() + ") with " + trucks + " trucks");
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void startFireSpawner() {
        // Add a new Fire every 10 seconds
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                generateFire();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void generateFire() {
        Random rand = new Random();
        int x = rand.nextInt(2000) - 1000;
        int y = rand.nextInt(2000) - 1000;
        int severity = rand.nextInt(10) + 1;
        LocalDateTime time = LocalDateTime.now();
        Fire fire = new Fire(x, y, severity, time);
        activeFires.put(fire.getID(), fire);
        if (eventLogger != null) {
            eventLogger.log("New fire spawned: ID " + fire.getID()
                    + " at (" + x + ", " + y + "), severity=" + severity);
        }
    }

    // ================== Fire Timer (Spread) ===================

    public void startFireTimer() {
        // Fires spread every 20 seconds
        scheduler.scheduleAtFixedRate(() -> {
            if (paused || activeFires.isEmpty()) return;
            Random rand = new Random();
            for (Fire f : new ArrayList<>(activeFires.values())) {
                f.spreadFire();
                if (eventLogger != null) {
                    eventLogger.log("Fire ID " + f.getID() + " is spreading (severity=" + f.getSeverity() + ")");
                }
                if (rand.nextDouble() < 0.3) {
                    spreadFire(f);
                }
            }
        }, 20, 20, TimeUnit.SECONDS);
    }

    private void spreadFire(Fire parent) {
        // spawn a new fire near the old one
        Random rand = new Random();
        int newX = (int) parent.getX() + rand.nextInt(101) - 50;
        int newY = (int) parent.getY() + rand.nextInt(101) - 50;
        int newSev = Math.max(1, parent.getSeverity() - 1);
        Fire newFire = new Fire(newX, newY, newSev, LocalDateTime.now());
        activeFires.put(newFire.getID(), newFire);
        if (eventLogger != null) {
            eventLogger.log("Fire spread -> new Fire ID " + newFire.getID()
                    + " at (" + newX + ", " + newY + ")");
        }
    }

    // ================== Deployment (Auto-Extinguish) ===================

    private void startAutoDeploy() {
        // Every 15 seconds, deploy trucks for all active fires
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused && !activeFires.isEmpty()) {
                for (Fire f : new ArrayList<>(activeFires.values())) {
                    deployFireTrucks(f);
                }
            }
        }, 5, 15, TimeUnit.SECONDS);
    }

    public void deployFireTrucks(Fire fire) {
        // If already extinguished, skip
        if (!activeFires.containsKey(fire.getID())) return;

        // We'll send exactly 1 truck for simplicity
        FireStation station = findNearestStation(fire);
        if (station == null) return; // no stations?
        if (!station.canDeploy()) return;

        station.deployTruck(1);
        if (eventLogger != null) {
            eventLogger.log("Station " + station.getID() + " deploying truck to Fire " + fire.getID());
        }

        // Travel time
        double distance = station.calculateDistance(fire.getX(), fire.getY());
        double travelTimeSec = Math.max(distance / TRUCK_SPEED, 3); // min 3s
        long travelMs = (long)(travelTimeSec * 1000);

        // Create a "MovingTruck" object for animation
        MovingTruck mt = new MovingTruck(
                station.getID(), fire.getID(),
                station.getX(), station.getY(),
                fire.getX(), fire.getY(),
                travelMs,
                MovingTruck.State.TO_FIRE
        );
        synchronized (movingTrucks) {
            movingTrucks.add(mt);
        }

        // Schedule arrival
        scheduler.schedule(() -> {
            // Arrived at the fire
            synchronized (movingTrucks) {
                mt.setState(MovingTruck.State.EXTINGUISHING);
            }
            if (eventLogger != null) {
                eventLogger.log("Truck arrived at Fire " + fire.getID() + ", extinguishing...");
            }

            // Extinguish time (for simplicity, 5s)
            long extinguishMs = 5000;
            scheduler.schedule(() -> {
                // Fire is extinguished
                activeFires.remove(fire.getID());
                if (eventLogger != null) {
                    eventLogger.log("Fire " + fire.getID() + " extinguished, truck returning to station " + station.getID());
                }

                // Return trip
                synchronized (movingTrucks) {
                    movingTrucks.remove(mt);
                }
                // Create a new truck object for the return trip
                MovingTruck rt = new MovingTruck(
                        station.getID(), fire.getID(),
                        fire.getX(), fire.getY(),
                        station.getX(), station.getY(),
                        travelMs,
                        MovingTruck.State.RETURNING
                );
                synchronized (movingTrucks) {
                    movingTrucks.add(rt);
                }

                // Once it returns
                scheduler.schedule(() -> {
                    synchronized (movingTrucks) {
                        movingTrucks.remove(rt);
                    }
                    station.retrieveTruck(1);
                    if (eventLogger != null) {
                        eventLogger.log("Truck returned to Station " + station.getID()
                                + ". Station now has " + station.getTrucks() + " trucks.");
                    }
                }, travelMs, TimeUnit.MILLISECONDS);

            }, extinguishMs, TimeUnit.MILLISECONDS);

        }, travelMs, TimeUnit.MILLISECONDS);
    }

    // A helper method to find the single nearest station
    private FireStation findNearestStation(Fire fire) {
        FireStation nearest = null;
        double minDist = Double.MAX_VALUE;
        for (FireStation st : stations.values()) {
            double d = st.calculateDistance(fire.getX(), fire.getY());
            if (d < minDist) {
                minDist = d;
                nearest = st;
            }
        }
        return nearest;
    }
}
