package MiniFireForce;
import java.sql.Array;
import  java.util.*;
public class GenSituationClass {
    private List<Fire> activeFire;
    private List<FireStation> fireStations;

    public GenSituationClass(){
        this.activeFire = new ArrayList<>();
        this.fireStations = new ArrayList<>();
    }

    public List<Fire> getActiveFire(){
        return activeFire;
    }

    public void setActiveFire(List<Fire> activeFire){
        this.activeFire = activeFire;
    }

    public List<FireAction> fireStations(){
        return fireStations;
    }

    public void fireStation(){
        this.fireStations = fireStations;
    }

    Random random = new Random();
    String[] map = {"suburbs","downtown","industrial area","residential", "abandoned area" };
    int severity = random.nextInt(10) + 1;
    String location = map[random.nextInt(map.length)];

    // Fire newFire =



}
