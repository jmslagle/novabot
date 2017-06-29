package core;

/**
 * Created by Owner on 27/06/2017.
 */
public class Raid {
    public int bossId;
    public Location location;
    public int level;

    public Raid(){

    }

    public Raid(int level, int bossId, Location location){
        this.level = level;
        this.bossId = bossId;
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("RAID: %s,%s,%s",level,bossId,location);
    }
}
