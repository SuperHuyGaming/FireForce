package MiniFireForce;
import java.time.LocalDateTime;
import  java.util.HashMap;
import java.util.Map;
import java.util.Random;

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




    public void generateFire(){
    Random random = new Random();
    int x = random.nextInt(-1000, 1000);
    int y = random.nextInt(-1000, 1000);
    int severity = random.nextInt(10) + 1;
    LocalDateTime time = LocalDateTime.now();

    Fire generateFire = new Fire(x, y, severity, time);
    activeFire.put(generateFire.getID(),generateFire);
}





}
