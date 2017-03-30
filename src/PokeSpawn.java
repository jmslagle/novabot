/**
 * Created by Paris on 21/03/2017.
 */
public class PokeSpawn {

    int id;
    String suburb;
    Region region;
    float iv;
    String move_1;
    String move_2;

    public PokeSpawn(int id, String suburb, Region region, float iv, String move_1, String move_2) {
        this.id = id;
        this.suburb = suburb;
        this.region = region;
        this.iv = iv;
        this.move_1 = move_1;
        this.move_2 = move_2;
    }

    @Override
    public String toString() {
        return "[" + suburb + "]" + " #" + id + " " + Pokemon.idToName(id) + " " + iv + "%, " + move_1 +", " + move_2;
    }
}
