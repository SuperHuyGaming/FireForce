package MiniFireForce;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class GenSituationClass {
    private final Map<Integer, Fire> activeFires;
    private final Map<Integer, FireStation> stations;
    private final List<MovingTruck> movingTrucks; // For animation
    private ScheduledExecutorService scheduler;
    private EventLogger eventLogger;
    private volatile boolean paused = false;

    // We'll spawn stations, fires, etc. continuously
    private static final double TRUCK_SPEED = 50.0; // units per second

    public GenSituationClass() {
        activeFires = new HashMap<>();
        stations = new HashMap<>();
        movingTrucks = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(4);

        startStationSpawner();
        startFireSpawner();
        startAutoDeploy();
        startFireTimer();
    }

    // ========== Logging and Pause ==========

    public void setEventLogger(EventLogger logger) {
        this.eventLogger = logger;
    }

    public void setPaused(boolean p) {
        paused = p;
        if (eventLogger != null) {
            eventLogger.log(paused ? "Simulation paused." : "Simulation resumed.");
        }
    }

    public boolean isPaused() {
        return paused;
    }

    // ========== Accessors ==========

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

    // ========== Station, Fire Spawners ==========

    private void startStationSpawner() {
        // Add a new station every 30s
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                Random r = new Random();
                float x = r.nextInt(2000) - 1000;
                float y = r.nextInt(2000) - 1000;
                int trucks = r.nextInt(5) + 1; // 1..5
                FireStation st = new FireStation(x, y, trucks);
                stations.put(st.getID(), st);
                if (eventLogger != null) {
                    eventLogger.log("New station: ID " + st.getID() + " at (" + x + ", " + y + "), trucks=" + trucks);
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void startFireSpawner() {
        // Add a new Fire every 10s
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                generateFire();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void generateFire() {
        Random r = new Random();
        int x = r.nextInt(2000) - 1000;
        int y = r.nextInt(2000) - 1000;
        int sev = r.nextInt(10) + 1;
        LocalDateTime now = LocalDateTime.now();
        Fire f = new Fire(x, y, sev, now);
        activeFires.put(f.getID(), f);
        if (eventLogger != null) {
            eventLogger.log("New Fire: ID " + f.getID() + " at (" + x + ", " + y + "), sev=" + sev);
        }
    }

    // ========== Fire Timer (Spread) ==========

    public void startFireTimer() {
        // Every 20s, existing fires may spread or spawn a new one
        scheduler.scheduleAtFixedRate(() -> {
            if (paused || activeFires.isEmpty()) return;
            Random rand = new Random();
            for (Fire f : new ArrayList<>(activeFires.values())) {
                f.spreadFire();
                if (eventLogger != null) {
                    eventLogger.log("Fire ID " + f.getID() + " is spreading, severity=" + f.getSeverity());
                }
                if (rand.nextDouble() < 0.3) {
                    spawnNearbyFire(f);
                }
            }
        }, 20, 20, TimeUnit.SECONDS);
    }

    private void spawnNearbyFire(Fire oldFire) {
        Random rand = new Random();
        int nx = (int)oldFire.getX() + rand.nextInt(101) - 50;
        int ny = (int)oldFire.getY() + rand.nextInt(101) - 50;
        int newSev = Math.max(1, oldFire.getSeverity() - 1);
        Fire nf = new Fire(nx, ny, newSev, LocalDateTime.now());
        activeFires.put(nf.getID(), nf);
        if (eventLogger != null) {
            eventLogger.log("Fire spread -> new Fire ID " + nf.getID() + " at (" + nx + ", " + ny + ")");
        }
    }

    // ========== Deploy ==========

    private void startAutoDeploy() {
        // Every 15 seconds, automatically extinguish each active fire with 1 truck
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused && !activeFires.isEmpty()) {
                for (Fire f : new ArrayList<>(activeFires.values())) {
                    deploySingleTruck(f);
                }
            }
        }, 5, 15, TimeUnit.SECONDS);
    }

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

    public void deploySingleTruck(Fire fire) {
        // If the fire is already gone, skip
        if (!activeFires.containsKey(fire.getID())) return;

        // Find the nearest station
        FireStation st = findNearestStation(fire);
        if (st == null) return; // no stations
        if (!st.canDeploy()) return; // no trucks

        st.deployTruck(1); // send exactly 1 truck
        if (eventLogger != null) {
            eventLogger.log("Station " + st.getID() + " sends 1 truck to Fire " + fire.getID());
        }

        // Calculate travel time
        double distance = st.calculateDistance(fire.getX(), fire.getY());
        double travelTimeSec = Math.max(distance / TRUCK_SPEED, 3); // min 3s
        long travelMs = (long)(travelTimeSec * 1000);

        // Create a moving truck to animate
        MovingTruck mt = new MovingTruck(
                st.getID(), fire.getID(),
                st.getX(), st.getY(),
                fire.getX(), fire.getY(),
                travelMs,
                MovingTruck.State.TO_FIRE
        );
        synchronized (movingTrucks) {
            movingTrucks.add(mt);
        }

        // Schedule arrival
        scheduler.schedule(() -> {
            // Truck arrived at fire
            synchronized (movingTrucks) {
                mt.setState(MovingTruck.State.EXTINGUISHING);
            }
            if (eventLogger != null) {
                eventLogger.log("Truck arrived at Fire " + fire.getID() + " -> extinguishing...");
            }

            // Extinguish for 5s
            long extinguishMs = 5000;
            scheduler.schedule(() -> {
                // Remove the fire
                activeFires.remove(fire.getID());
                if (eventLogger != null) {
                    eventLogger.log("Fire " + fire.getID() + " extinguished -> truck returning to station " + st.getID());
                }

                // Move truck from the map
                synchronized (movingTrucks) {
                    movingTrucks.remove(mt);
                }

                // Create a new truck object for returning
                MovingTruck returningTruck = new MovingTruck(
                        st.getID(), fire.getID(),
                        fire.getX(), fire.getY(),
                        st.getX(), st.getY(),
                        travelMs,
                        MovingTruck.State.RETURNING
                );
                synchronized (movingTrucks) {
                    movingTrucks.add(returningTruck);
                }

                // Once it returns
                scheduler.schedule(() -> {
                    synchronized (movingTrucks) {
                        movingTrucks.remove(returningTruck);
                    }
                    st.retrieveTruck(1);
                    if (eventLogger != null) {
                        eventLogger.log("Truck returned to Station " + st.getID()
                                + " -> station now has " + st.getTrucks() + " trucks");
                    }
                }, travelMs, TimeUnit.MILLISECONDS);

            }, extinguishMs, TimeUnit.MILLISECONDS);

        }, travelMs, TimeUnit.MILLISECONDS);
    }
}
