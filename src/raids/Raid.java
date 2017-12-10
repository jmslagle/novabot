package raids;

import core.Location;
import core.Types;
import net.dv8tion.jda.core.entities.Emote;
import pokemon.Pokemon;

import java.util.*;

/**
 * Created by Owner on 27/06/2017.
 */
public class Raid {
    private static final HashMap<String,String> SPECIAL_NAMES = new HashMap<>();

    public static final String[] TYPES = new String[] {
            "bugtype",
            "dark",
            "dragontype",
            "electric",
            "fairy",
            "fighting",
            "firetype",
            "flying",
            "ghosttype",
            "grass",
            "ground",
            "ice",
            "normal",
            "poison",
            "psychic",
            "rock",
            "steel",
            "water"
    };

    public static final HashMap<String, Emote> emotes = new HashMap<>();

    public int bossId;
    public Location location;

    static {
        SPECIAL_NAMES.put("bug","bugtype");
        SPECIAL_NAMES.put("dragon","dragontype");
        SPECIAL_NAMES.put("fire","firetype");
        SPECIAL_NAMES.put("ghost","ghosttype");
    }

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

    public static String[] getBossWeaknessEmotes(int bossId){
        HashSet<String> weaknesses = new HashSet<>();
        for (String type : Pokemon.getTypes(bossId)) {
            weaknesses.addAll(Types.getWeaknesses(type));
        }

        for (String toReplace : SPECIAL_NAMES.keySet()) {
            int oldSize = weaknesses.size();
            weaknesses.remove(toReplace);
            if (weaknesses.size() < oldSize) {
                weaknesses.add(SPECIAL_NAMES.get(toReplace));
            }
        }

        String weaknessArray[] = new String[weaknesses.size()];
        return weaknesses.toArray(weaknessArray);
    }

    public static String[] getBossStrengthsEmote(int move1, int move2){
        HashSet<String> strengths = new HashSet<>();

        strengths.addAll(Types.getStrengths(Pokemon.getMoveType(move1)));
        strengths.addAll(Types.getStrengths(Pokemon.getMoveType(move2)));

        for (String toReplace : SPECIAL_NAMES.keySet()) {
            int oldSize = strengths.size();
            strengths.remove(toReplace);
            if (strengths.size() < oldSize) {
                strengths.add(SPECIAL_NAMES.get(toReplace));
            }
        }

        String strengthsArray[] = new String[strengths.size()];
        return strengths.toArray(strengthsArray);
    }


}
