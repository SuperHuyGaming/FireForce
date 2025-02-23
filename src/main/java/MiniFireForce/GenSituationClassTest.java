package MiniFireForce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class GenSituationClassTest {
    private GenSituationClass situation;
    private Fire testFire;
    private FireStation testStation;

    @BeforeEach
    void setUp() {
        situation = new GenSituationClass();
        testFire = new Fire(0, 0, 5, LocalDateTime.now());
        testStation = new FireStation(1, 50,  10);
        situation.addActiveFire(testFire);
        situation.addFireStation(testStation);
    }

    @Test
    void testAddActiveFire() {
        // Normal case
        assertEquals(1, situation.getActiveFires().size());

        // Edge cases
        Fire fire1 = new Fire(Integer.MAX_VALUE, Integer.MAX_VALUE, 10, LocalDateTime.now());
        Fire fire2 = new Fire(Integer.MIN_VALUE, Integer.MIN_VALUE, 1, LocalDateTime.now());
        Fire fire3 = new Fire(0, 0, 0, LocalDateTime.now());
        Fire fire4 = new Fire(500, -500, 10, LocalDateTime.now());
        Fire fire5 = new Fire(-999, 999, 1, LocalDateTime.now());

        situation.addActiveFire(fire1);
        situation.addActiveFire(fire2);
        situation.addActiveFire(fire3);
        situation.addActiveFire(fire4);
        situation.addActiveFire(fire5);
        assertEquals(6, situation.getActiveFires().size());
    }

    @Test
    void testAddFireStation() {
        // Normal case
        assertEquals(1, situation.getFireStations().size());

        // Edge cases
        FireStation station1 = new FireStation(Integer.MAX_VALUE, Integer.MAX_VALUE, 10);
        FireStation station2 = new FireStation(Integer.MIN_VALUE, Integer.MIN_VALUE, 1);
        FireStation station3 = new FireStation( 0, 0, 0);
        FireStation station4 = new FireStation( 1000, -1000, 5);
        FireStation station5 = new FireStation(-999, 999, 1);

        situation.addFireStation(station1);
        situation.addFireStation(station2);
        situation.addFireStation(station3);
        situation.addFireStation(station4);
        situation.addFireStation(station5);
        assertEquals(6, situation.getFireStations().size());
    }

    @Test
    void testFindFireStation() {
        // Normal case
        List<FireStation> stations = situation.findFireStation(testFire);
        assertFalse(stations.isEmpty());

        // Edge cases
        Fire fire1 = new Fire(10000, 10000, 10, LocalDateTime.now()); // Far away
        Fire fire2 = new Fire(-10000, -10000, 10, LocalDateTime.now()); // Negative coordinates
        Fire fire3 = new Fire(0, 0, 10, LocalDateTime.now()); // Same as another fire
        Fire fire4 = new Fire(999, -999, 1, LocalDateTime.now()); // Edge placement
        Fire fire5 = new Fire(Integer.MAX_VALUE, Integer.MIN_VALUE, 5, LocalDateTime.now()); // Extreme case

        situation.addActiveFire(fire1);
        situation.addActiveFire(fire2);
        situation.addActiveFire(fire3);
        situation.addActiveFire(fire4);
        situation.addActiveFire(fire5);

        assertNotNull(situation.findFireStation(fire1));
        assertNotNull(situation.findFireStation(fire2));
        assertNotNull(situation.findFireStation(fire3));
        assertNotNull(situation.findFireStation(fire4));
        assertNotNull(situation.findFireStation(fire5));
    }

    @Test
    void testCompareFires() {
        // Normal case
        Fire severeFire = new Fire(200, 200, 10, LocalDateTime.now().minusMinutes(10));
        situation.addActiveFire(severeFire);
        assertEquals(severeFire, situation.compareFires());

        // Edge cases
        Fire fire1 = new Fire(0, 0, 10, LocalDateTime.now().minusDays(1)); // Older severe fire
        Fire fire2 = new Fire(100, 100, 10, LocalDateTime.now().minusHours(1)); // Recent severe fire
        Fire fire3 = new Fire(50, 50, 1, LocalDateTime.now().minusMinutes(5)); // Weak fire
        Fire fire4 = new Fire(-500, -500, 10, LocalDateTime.now().minusMinutes(2)); // Negative coordinates
        Fire fire5 = new Fire(999, 999, 5, LocalDateTime.now().minusSeconds(30)); // Just happened

        situation.addActiveFire(fire1);
        situation.addActiveFire(fire2);
        situation.addActiveFire(fire3);
        situation.addActiveFire(fire4);
        situation.addActiveFire(fire5);

        assertEquals(fire1, situation.compareFires());
    }

    @Test
    void testRemoveFire() {
        // Normal case
        situation.removeFire(testFire);
        assertFalse(situation.getActiveFires().containsKey(testFire.getID()));

        // Edge cases
        situation.removeFire(new Fire(999, 999, 5, LocalDateTime.now())); // Non-existent fire
        assertEquals(0, situation.getActiveFires().size());
    }
}
