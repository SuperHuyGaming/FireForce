package MiniFireForce;
import  java.util.*;

public class GenSituationClass {
    private List<Fire> activeFire;
    privare List<FireStation> fireStations;

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
    int x = random.nextInt(-1000,1000);
    int y = random.nextInt(-1000, 1000);
    int severity = random.nextInt(10) + 1;
    int time = random.nextInt(60);

    Fire generateFire = new Fire(x,y,severity,time);
    activeFire.add(generateFire);







}
