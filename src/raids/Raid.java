package raids;

import core.Location;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Owner on 27/06/2017.
 */
public class Raid {
    public static final HashSet<Integer> POSSIBLE_BOSSES = new HashSet<>(
            Arrays.asList(
                    3,
                    6,
                    9,
                    59,
                    65,
                    68,
                    89,
                    94,
                    103,
                    110,
                    112,
                    125,
                    126,
                    131,
                    129,
                    134,
                    135,
                    136,
                    143,
                    153,
                    156,
                    159,
                    248)
    );

    public int bossId;
    public Location location;

    public Raid(){

    }

    public Raid(int bossId, Location location){
        this.bossId = bossId;
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("RAID: %s,%s",bossId,location);
    }
}
