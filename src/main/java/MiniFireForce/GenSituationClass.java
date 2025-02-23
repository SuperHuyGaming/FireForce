package MiniFireForce;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GenSituationClass {
    private final Map<Integer, Fire> activeFire;
    private final Map<Integer, FireStation> fireStations;
    private final ScheduledExecutorService scheduler;
    private EventLogger eventLogger;  
    private static final double TRUCK_SPEED = 50; // meters/second
    private volatile boolean paused = false;
    
    // List to store moving trucks for animation and intelligence.
    private final List<MovingTruck> movingTrucks = new ArrayList<>();

    public GenSituationClass() {
        this.activeFire = new HashMap<>();
        this.fireStations = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(4);

        startFireTimer();      
        startStationSpawner(); 
        startFireSpawner();    
        startAutoDeploy();     

        // Schedule periodic update of moving trucks (every 2 seconds).
        scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                updateMovingTrucks();
            }
        }, 2000, 2000, TimeUnit.MILLISECONDS);
    }

    public void setEventLogger(EventLogger logger) {
        this.eventLogger = logger;
    }

    // Pausing logic.
    public void setPaused(boolean paused) {
        this.paused = paused;
        if (eventLogger != null) {
            eventLogger.log(paused ? "Simulation paused." : "Simulation resumed.");
        }
    }

    public boolean isPaused() {
        return paused;
    }

    // Getter for moving trucks (used by the map for drawing).
    public List<MovingTruck> getMovingTrucks() {
        synchronized(movingTrucks) {
            return new ArrayList<>(movingTrucks);
        }
    }

    // Automatic Station Spawner: every 30 seconds (first in 10 seconds).
    private void startStationSpawner() {
        scheduler.scheduleAtFixedRate(() -> {
            if (paused) return;
            Random rand = new Random();
            float x = rand.nextInt(2000) - 1000;
            float y = rand.nextInt(2000) - 1000;
            int trucks = rand.nextInt(5) + 1;
            FireStation station = new FireStation(x, y, trucks);
            addFireStation(station);
        }, 10, 30, TimeUnit.SECONDS); 
    }

    // Automatic Fire Spawner: every 15 seconds (first in 5 seconds).
    private void startFireSpawner() {
        scheduler.scheduleAtFixedRate(() -> {
            if (paused) return;
            generateFire();
        }, 5, 15, TimeUnit.SECONDS); 
    }

    // Auto-deployment: every 10 seconds.
    private void startAutoDeploy() {
        scheduler.scheduleAtFixedRate(() -> {
            if (paused) return;
            if (!activeFire.isEmpty()) {
                // Iterate over a copy of active fires.
                for (Fire f : new ArrayList<>(activeFire.values())) {
                    deployFireTrucks(f);
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    // Station and Fire management.
    public void addFireStation(FireStation station) {
        fireStations.put(station.getID(), station);
        if (eventLogger != null && !paused) {
            eventLogger.log("FireStation added: ID " + station.getID() +
                    " at (" + station.getX() + ", " + station.getY() + ") with " +
                    station.getTrucks() + " trucks");
        }
    }

    public void generateFire() {
        Random random = new Random();
        int x = random.nextInt(2000) - 1000;
        int y = random.nextInt(2000) - 1000;
        int severity = random.nextInt(10) + 1;
        LocalDateTime time = LocalDateTime.now();
        Fire newFire = new Fire(x, y, severity, time);
        addActiveFire(newFire);
        if (eventLogger != null && !paused) {
            eventLogger.log("New fire generated: Fire ID " + newFire.getID() +
                    " at (" + x + ", " + y + ") with severity " + severity);
        }
    }

    public void addActiveFire(Fire fire) {
        activeFire.put(fire.getID(), fire);
        if (eventLogger != null && !paused) {
            eventLogger.log("Fire just started: Fire ID " + fire.getID() +
                    " at (" + fire.getX() + ", " + fire.getY() + ") with severity " + fire.getSeverity());
        }
    }

    public void removeFire(Fire fire) {
        activeFire.remove(fire.getID());
    }

    // Compute travel time from a station to a fire.
    private double calculateTravelTime(FireStation station, Fire fire, double speed) {
        double distance = station.calculateDistance(fire.getX(), fire.getY());
        return distance / speed;
    }

    // Deploy fire trucks to a given fire.
    public void deployFireTrucks(Fire fire) {
        if (!activeFire.containsKey(fire.getID())) return;
        if (eventLogger != null && !paused) {
            eventLogger.log("Deploying 1 truck for Fire ID " + fire.getID() +
                    " (severity: " + fire.getSeverity() + ")");
        }
        // Only one truck is needed.
        List<FireStation> nearestStations = findFireStation(fire);
        for (FireStation station : nearestStations) {
            if (!station.canDeploy()) continue;
            // Deploy exactly 1 truck from this station.
            station.deployTruck(1);
            if (eventLogger != null && !paused) {
                eventLogger.log("FireStation " + station.getID() +
                        " is sending 1 truck to Fire ID " + fire.getID());
            }
            double travelTime = calculateTravelTime(station, fire, TRUCK_SPEED);
            final double finalTravelTime = Math.max(travelTime, 3);  // enforce at least 3 seconds
            if (eventLogger != null && !paused) {
                eventLogger.log("Truck from Station " + station.getID() +
                        " traveling to Fire ID " + fire.getID() +
                        ". ETA: ~" + (int) finalTravelTime + "s");
            }
            long travelDurationMs = (long)(finalTravelTime * 1000);
            
            // Create a moving truck for the "to fire" leg.
            MovingTruck truck = new MovingTruck(station.getID(), fire.getID(),
                    station.getX(), station.getY(), fire.getX(), fire.getY(),
                    travelDurationMs, MovingTruck.State.TO_FIRE);
            synchronized(movingTrucks) {
                movingTrucks.add(truck);
            }
            
            // Schedule truck arrival: after travel time, set its state to EXTINGUISHING.
            scheduler.schedule(() -> {
                synchronized(movingTrucks) {
                    truck.setState(MovingTruck.State.EXTINGUISHING);
                }
                if (eventLogger != null) {
                    eventLogger.log("Truck from Station " + station.getID() +
                            " has arrived at Fire ID " + fire.getID() +
                            " and is extinguishing the fire...");
                }
                double extinguishTime = 5; // seconds (adjust as needed)
                scheduler.schedule(() -> {
                    removeFire(fire);
                    if (eventLogger != null && !paused) {
                        eventLogger.log("Fire ID " + fire.getID() +
                                " extinguished. Truck returning from Station " + station.getID());
                    }
                    // Update the same truck for the return journey.
                    synchronized(movingTrucks) {
                        truck.setState(MovingTruck.State.RETURNING);
                        truck.setDestination(station.getX(), station.getY());
                        truck.resetStartTime(); // Reset timer for return leg.
                        truck.setTravelDuration(travelDurationMs);
                    }
                    scheduler.schedule(() -> {
                        synchronized(movingTrucks) {
                            movingTrucks.remove(truck);
                        }
                        station.retrieveTruck(1);
                        if (eventLogger != null) {
                            eventLogger.log("Truck returned to Station " + station.getID() +
                                    ". Station now has " + station.getTrucks() + " trucks.");
                        }
                    }, travelDurationMs, TimeUnit.MILLISECONDS);
                }, (long)(extinguishTime * 1000), TimeUnit.MILLISECONDS);
            }, travelDurationMs, TimeUnit.MILLISECONDS);
            
            break; // Deploy one truck only.
        }
    }
    
    

    // Instead of using a TreeMap, sort stations by distance to the fire.
    public List<FireStation> findFireStation(Fire fire) {
        List<FireStation> stations = new ArrayList<>(fireStations.values());
        stations.sort((s1, s2) -> Double.compare(
                s1.calculateDistance(fire.getX(), fire.getY()),
                s2.calculateDistance(fire.getX(), fire.getY())
        ));
        return stations;
    }

    // ------------------------------------------------------------------
    // Intelligent Truck Updating Methods:
    // Helper: compute Euclidean distance.
    private double distanceBetween(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // This method is called periodically (every 2 seconds) to update moving trucks intelligently.
    private void updateMovingTrucks() {
        synchronized(movingTrucks) {
            for (MovingTruck truck : movingTrucks) {
                if (truck.getState() == MovingTruck.State.TO_FIRE) {
                    // Check if the current target fire still exists.
                    Fire currentTarget = activeFire.get(truck.getFireID());
                    double currentX = truck.getCurrentX();
                    double currentY = truck.getCurrentY();
                    if (currentTarget == null) {
                        // Target extinguished; send truck back.
                        FireStation station = fireStations.get(truck.getStationID());
                        if (station != null) {
                            truck.setState(MovingTruck.State.RETURNING);
                            truck.setDestination(station.getX(), station.getY());
                            double d = distanceBetween(currentX, currentY, station.getX(), station.getY());
                            long newDuration = (long)(Math.max(d / TRUCK_SPEED, 3) * 1000);
                            truck.setTravelDuration(newDuration);
                            truck.resetStartTime();
                            if (eventLogger != null) {
                                eventLogger.log("Truck from Station " + truck.getStationID() +
                                        " found its target extinguished. Returning to station.");
                            }
                        }
                        continue;
                    }
                    // Otherwise, check if another fire is closer.
                    double currentTargetDistance = distanceBetween(currentX, currentY, currentTarget.getX(), currentTarget.getY());
                    for (Fire candidate : activeFire.values()) {
                        if (candidate.getID() == truck.getFireID()) continue;
                        double candidateDistance = distanceBetween(currentX, currentY, candidate.getX(), candidate.getY());
                        if (candidateDistance < currentTargetDistance) {
                            truck.setFireID(candidate.getID());
                            truck.setDestination(candidate.getX(), candidate.getY());
                            long newDuration = (long)(Math.max(candidateDistance / TRUCK_SPEED, 3) * 1000);
                            truck.setTravelDuration(newDuration);
                            truck.resetStartTime();
                            if (eventLogger != null) {
                                eventLogger.log("Truck from Station " + truck.getStationID() +
                                        " reassigned from Fire ID " + currentTarget.getID() +
                                        " to closer Fire ID " + candidate.getID());
                            }
                            break; // Only reassign once.
                        }
                    }
                }
            }
        }
    }
    
    // Fire Spreading: fires may spread every 20 seconds.
    public void startFireTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            if (paused) return;
            if (activeFire.isEmpty()) return;
            Random random = new Random();
            for (Fire f : new ArrayList<>(activeFire.values())) {
                f.spreadFire();
                if (eventLogger != null) {
                    eventLogger.log("Fire ID " + f.getID() + " is spreading.");
                }
                if (random.nextDouble() < 0.3) {
                    spreadFire(f);
                }
            }
        }, 20, 20, TimeUnit.SECONDS);
    }
<<<<<<< HEAD
    
    private void spreadFire(Fire fire) {
        Random random = new Random();
        int newX = (int) fire.getX() + random.nextInt(101) - 50;
        int newY = (int) fire.getY() + random.nextInt(101) - 50;
        int newSeverity = Math.max(1, fire.getSeverity() - 1);
        LocalDateTime newTime = LocalDateTime.now();
        Fire newFire = new Fire(newX, newY, newSeverity, newTime);
        addActiveFire(newFire);
        if (eventLogger != null) {
            eventLogger.log("Fire spread: New fire created at (" + newX + ", " + newY +
                    ") from Fire ID " + fire.getID());
        }
    }
    
    // Getters for use in other components.
    public Map<Integer, FireStation> getFireStations() {
        return fireStations;
    }
    
    public Map<Integer, Fire> getActiveFires() {
        return activeFire;
    }
}
=======
}
>>>>>>> 60153862d96b6414b79ba38357bf6b0109acd716
