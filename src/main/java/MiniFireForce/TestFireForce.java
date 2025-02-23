package MiniFireForce;

import java.time.LocalDateTime;
import java.util.List;

public class TestFireForce {
    
    public static void main(String[] args) {
        // Create a FireStation instance.
        
        FireStation station1 = new FireStation(100, 100, 5);
        System.out.println("Created FireStation at (" + station1.getX() + ", " + station1.getY() + ") with " + station1.getTrucks() + " trucks.");

        // Create a Fire instance.
        Fire fire1 = new Fire(150, 150, 6, LocalDateTime.now());
        System.out.println("Created Fire at (" + fire1.getX() + ", " + fire1.getY() + ") with severity " + fire1.getSeverity() + ".");

        // Test calculating the distance between the fire station and the fire.
        double distance = station1.calculateDistance(fire1.getX(), fire1.getY());
        System.out.println("Distance from FireStation to Fire: " + distance);

        // Check if the station can deploy trucks.
        System.out.println("Can station deploy trucks? " + station1.canDeploy());

        // Test deploying trucks.
        int requiredTrucks = 3;
        int remainingRequired = station1.deployTruck(requiredTrucks);
        System.out.println("After deploying " + requiredTrucks + " trucks, remaining trucks needed: " + remainingRequired);
        System.out.println("Trucks left in station: " + station1.getTrucks());

        // Retrieve some trucks back to the station.
        station1.retrieveTruck(2);
        System.out.println("After retrieving 2 trucks, trucks in station: " + station1.getTrucks());

        // Test FireTruck functionality.
        // Assume velocity is given in meters per second.
        FireTruck truck1 = new FireTruck(60, 10);
        double travelTime = truck1.calculateTravelTime(station1, fire1, truck1.getVelocity());
        System.out.println("Travel time for truck from station to fire: " + travelTime + " seconds.");

        double extinguishTime = truck1.calculateExtinguishingTime(fire1, 2);
        System.out.println("Extinguishing time for fire with 2 trucks: " + extinguishTime + " seconds.");

        // Test Fire's spreadFire method.
        System.out.println("Fire severity before spread: " + fire1.getSeverity());
        fire1.spreadFire();
        System.out.println("Fire severity after spread: " + fire1.getSeverity());

        // Create a GenSituationClass instance and add the station and fire.
        GenSituationClass situation = new GenSituationClass();
        situation.addFireStation(station1);
        situation.addActiveFire(fire1);
        System.out.println("Added FireStation and Fire to the situation.");

        // Generate an additional fire.
        situation.generateFire();
        System.out.println("Generated a new fire. Active fires: " + situation.getActiveFires().size());

        // Compare fires to find the most severe one.
        Fire mostSevereFire = situation.compareFires();
        System.out.println("Most severe fire has severity: " + mostSevereFire.getSeverity());

        // Find the nearest fire station to the original fire.
        List<FireStation> nearestStations = situation.findFireStation(fire1);
        if (!nearestStations.isEmpty()) {
            FireStation nearest = nearestStations.get(0);
            System.out.println("Nearest station to fire is at (" + nearest.getX() + ", " + nearest.getY() + ")");
        }

        // Deploy fire trucks for the original fire.
        System.out.println("Deploying fire trucks for the fire with severity " + fire1.getSeverity());
        situation.deployFireTrucks(fire1);
        System.out.println("After deployment, active fires: " + situation.getActiveFires().size());
    }
}
