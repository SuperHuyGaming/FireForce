package MiniFireForce;
import java.sql.Array;
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


}
