package MiniFireForce;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

public class Fire {
    private float x;
    private float y;
    private int severity;
    private LocalDateTime time;
    private  int id = 0;
    private static int counter = 0; 

    public Fire(float x, float y, int severity, LocalDateTime time) {
        this.x = x;
        this.y = y;
        this.severity = severity;
        this.time = time;
        id=++counter;
    }

    public int getID() {
        return this.id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // Replacing javafx.util.Pair with AbstractMap.SimpleEntry
    public Map.Entry<Float, Float> getXY() {
        return new AbstractMap.SimpleEntry<>(x, y);
    }

    public int getSeverity() {
        return severity;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setX(float newX) {
        this.x = newX;
    }

    public void setY(float newY) {
        this.y = newY;
    }

    public void setTime(LocalDateTime newTime) {
        this.time = newTime;
    }

    public void updateSeverity(int newSev) {
        this.severity = newSev;
    }

    public void spreadFire() {
        if (severity < 10) {
            severity += (int) (Math.random() * 2);  // Increase severity randomly by 0 or 1
        }
    }
}
