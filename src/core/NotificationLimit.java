package core;

/**
 * Created by miriam on 5/7/17.
 */
public class NotificationLimit {

    public int pokemonLimit;
    public int raidLimit;

    public NotificationLimit(int pokemonLimit, int raidLimit){
        this.pokemonLimit = pokemonLimit;
        this.raidLimit = raidLimit;
    }

    @Override
    public String toString() {
        return String.format("%s,%s",(pokemonLimit == -1 ? "n" : pokemonLimit),(raidLimit == -1 ? "n" : raidLimit));
    }

    public static NotificationLimit fromString(String line) {
        String[] limitSplit = line.split(",");

        String pokeLimitStr = limitSplit[0].substring(limitSplit[0].indexOf("[") + 1);
        int pokeLimit = pokeLimitStr.equals("n") ? -1 : Integer.parseInt(pokeLimitStr);

        String raidLimitStr = limitSplit[1].substring(0,limitSplit[1].indexOf("]"));
        int raidLimit = raidLimitStr.equals("n") ? -1 :Integer.parseInt(raidLimitStr);
        return new  NotificationLimit(pokeLimit,raidLimit);
    }
}
