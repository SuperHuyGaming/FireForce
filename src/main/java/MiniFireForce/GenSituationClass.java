package MiniFireForce;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * The {@code GenSituationClass} manages the entire fire simulation environment.
 * It handles the spawning of fire stations, fire events, and the automatic deployment
 * of fire trucks to extinguish fires. Fires can spread over time, and fire stations
 * prioritize responses based on proximity.
 *
 * This class uses a scheduled executor to automate updates to the simulation.
 */
public class GenSituationClass {
    private final Map<Integer, Fire> activeFires;
    private final Map<Integer, FireStation> stations;
    private final List<MovingTruck> movingTrucks; // For animation
    private ScheduledExecutorService scheduler;
    private EventLogger eventLogger;
    private volatile boolean paused = false;

    // Speed of fire trucks in units per second
    private static final double TRUCK_SPEED = 50.0;

    /**
     * Initializes the simulation environment.
     * - Spawns fire stations at regular intervals.
     * - Spawns fires at random locations.
     * - Automates fire truck deployment.
     * - Simulates fire spread over time.
     */
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

    /**
     * Sets the event logger for recording simulation events.
     *
     * @param logger an implementation of {@code EventLogger} to handle event logs
     */
    public void setEventLogger(EventLogger logger) {
        this.eventLogger = logger;
    }

    /**
     * Pauses or resumes the simulation.
     *
     * @param p {@code true} to pause, {@code false} to resume
     */
    public void setPaused(boolean p) {
        paused = p;
        if (eventLogger != null) {
            eventLogger.log(paused ? "Simulation paused." : "Simulation resumed.");
        }
    }

    /**
     * @return {@code true} if the simulation is paused, otherwise {@code false}
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * @return a map of active fires in the simulation
     */
    public Map<Integer, Fire> getActiveFires() {
        return activeFires;
    }

    /**
     * @return a map of fire stations in the simulation
     */
    public Map<Integer, FireStation> getFireStations() {
        return stations;
    }

    /**
     * @return a list of moving fire trucks for animation purposes
     */
    public List<MovingTruck> getMovingTrucks() {
        synchronized (movingTrucks) {
            return new ArrayList<>(movingTrucks);
        }
    }

    // ================= Fire Station Spawning =================

    /**
     * Periodically spawns a new fire station at a random location every 30 seconds.
     */
    private void startStationSpawner() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                Random r = new Random();
                float x = r.nextInt(2000) - 1000;
                float y = r.nextInt(2000) - 1000;
                int trucks = r.nextInt(5) + 1; // 1 to 5 trucks
                FireStation st = new FireStation(x, y, trucks);
                stations.put(st.getID(), st);
                if (eventLogger != null) {
                    eventLogger.log("New station: ID " + st.getID() + " at (" + x + ", " + y + "), trucks=" + trucks);
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    // ================= Fire Spawning =================

    /**
     * Periodically spawns a new fire at a random location every 10 seconds.
     */
    private void startFireSpawner() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                generateFire();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Generates a fire at a random location with a random severity.
     */
    public void generateFire() {
        Random r = new Random();
        int x = r.nextInt(2000) - 1000;
        int y = r.nextInt(2000) - 1000;
        int sev = r.nextInt(10) + 1; // Severity 1 to 10
        LocalDateTime now = LocalDateTime.now();
        Fire f = new Fire(x, y, sev, now);
        activeFires.put(f.getID(), f);
        if (eventLogger != null) {
            eventLogger.log("New Fire: ID " + f.getID() + " at (" + x + ", " + y + "), severity=" + sev);
        }
    }

    // ================= Fire Spread Simulation =================

    /**
     * Periodically spreads active fires and creates new nearby fires.
     */
    public void startFireTimer() {
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

    /**
     * Creates a new fire near an existing fire.
     *
     * @param oldFire the fire from which a new fire is spreading
     */
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

    // ================= Fire Truck Deployment =================

    /**
     * Periodically deploys fire trucks to active fires every 15 seconds.
     */
    private void startAutoDeploy() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused && !activeFires.isEmpty()) {
                for (Fire f : new ArrayList<>(activeFires.values())) {
                    deploySingleTruck(f);
                }
            }
        }, 5, 15, TimeUnit.SECONDS);
    }

    /**
     * Finds the nearest fire station to a given fire.
     *
     * @param fire the fire that needs a fire truck
     * @return the nearest fire station, or {@code null} if none exist
     */
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

    /**
     * Deploys a fire truck to a specified fire.
     *
     * @param fire the fire that needs a truck
     */
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
