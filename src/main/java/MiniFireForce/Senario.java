package MiniFireForce;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class Senario {
    private GenSituationClass situation;  // Manages fire stations and fires
    private ScheduledExecutorService scheduler;

    public Senario() {
        this.situation = new GenSituationClass();
        this.scheduler = Executors.newScheduledThreadPool(5);
    }

    /**
     * Start the fire scenario simulation.
     */
    public void startScenario() {
        System.out.println(" Wildfire Scenario Started! ");

        // Generate a fire and start monitoring it
        Fire fire = generateFire();
        startFireTimer(fire);
    }

    /**
     * Generates a fire and adds it to the situation.
     * @return the generated Fire object
     */
    private Fire generateFire() {
        Random random = new Random();
        float x = random.nextFloat() * 2000 - 1000;  // Range: -1000 to 1000
        float y = random.nextFloat() * 2000 - 1000;
        int severity = random.nextInt(10) + 1;
        LocalDateTime time = LocalDateTime.now();

        Fire fire = new Fire(x, y, severity, time);
        situation.addActiveFire(fire);

        System.out.println(" Fire started at (" + x + ", " + y + ") | Severity: " + severity);
        return fire;
    }

    /**
     * Starts a timer for fire progression and response actions.
     */
    private void startFireTimer(Fire fire) {
        Runnable fireTask = new Runnable() {
            int elapsedTime = 0;

            @Override
            public void run() {
                if (elapsedTime >= 20) {
                    System.out.println(" Fire at (" + fire.getX() + ", " + fire.getY() + ") has reached maximum spread time.");
                    deployFireTrucks(fire);  // Auto-deploy fire trucks if fire still exists
                    scheduler.shutdown();  // Stop the scheduler
                    return;
                }

                // Simulate fire spreading
                fire.spreadFire();
                System.out.println(" Fire at (" + fire.getX() + ", " + fire.getY() + ") spread to severity: " + fire.getSeverity());

                // If fire severity is high, deploy fire trucks early
                if (fire.getSeverity() >= 8) {
                    System.out.println(" Emergency! Deploying fire trucks immediately!");
                    deployFireTrucks(fire);
                }

                elapsedTime += 5;
            }
        };

        // Schedule task every 5 seconds, stops at 20 seconds
        scheduler.scheduleAtFixedRate(fireTask, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Deploys fire trucks to the fire if a station is available.
     */
    private void deployFireTrucks(Fire fire) {
        FireStation station = situation.findFireStation(fire);
        if (station == null) {
            System.out.println(" No fire station nearby! Fire is uncontrolled!");
            return;
        }

        int trucksNeeded = Math.min(fire.getSeverity() / 2 + 1, station.getTrucks());
        boolean deployed = station.deployTruck(trucksNeeded);

        if (deployed) {
            System.out.println(" " + trucksNeeded + " fire trucks deployed from Station " + station.getID());
            situation.getActiveFires().remove(fire.getID());  // Remove fire after response
            System.out.println(" Fire ID " + fire.getID() + " extinguished.");
        } else {
            System.out.println(" Not enough fire trucks available!");
        }
    }

    /**
     * Run the wildfire simulation.
     */
    public static void main(String[] args) {
        Senario wildfireScenario = new Senario();
        wildfireScenario.startScenario();
    }
}
