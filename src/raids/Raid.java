package raids;

import core.Location;
import net.dv8tion.jda.core.entities.Emote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static core.MessageListener.jda;

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

    public static HashMap<String, Emote> emotes = new HashMap<>();

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

    public static String[] getBossWeaknessEmotes(int bossId){
        switch(bossId){
            case 3: //venusaur
                return new String[] {"firetype", "psychic", "flying", "ice"};
            case 6: //charizard
                return new String[] {"rock", "water", "electric"};
            case 9: //blastoise
                return new String[] {"grass", "electric"};
            case 59: //arcanine
                return new String[] {"ground", "water", "rock"};
            case 65: //alakazam
                return new String[] {"dark", "bugtype", "ghosttype"};
            case 68: //machamp
                return new String[] {"fairy", "flying", "psychic"};
            case 89: //muk
                return new String[] {"psychic", "ground"};
            case 94: //gengar
                return new String[] {"psychic", "dark", "ghosttype"};
            case 110: //weezing
                return new String[] {"psychic"};
            case 112: //rhydon
                return new String[] {"water", "grass:ground", "fighting", "ice", "steel"};
            case 125: //electabuzz
                return new String[] {"ground"};
            case 126: //magmar
                return new String[] {"ground", "water", "rock"};
            case 131: //lapras
                return new String[] {"grass", "electric", "rock", "fighting"};
            case 129: //magikarp
                return new String[] {"electric", "grass"};
            case 134: //vaporeon
                return new String[] {"electric", "grass"};
            case 135: //flareon
                return new String[] {"ground", "water", "rock"};
            case 136: //jolteon
                return new String[] {"ground"};
            case 143: //snorlax
                return new String[] {"fighting"};
            case 153: //bayleef
                return new String[] {"ice", "firetype", "bugtype", "poison"};
            case 156: //quilava
                return new String[] {"ground", "water", "rock"};
            case 159: //croconaw
                return new String[] {"electric", "grass"};
            case 248: //tyranitar
                return new String[] {"fighting", "grass", "bugtype", "ground", "water", "steel", "fairy"};
        }
        return new String[]{};
    }

    public static String[] getBossStrengthsEmote(int bossId){
        switch(bossId){
            case 3: //venusaur
                return new String[] {"water", "fairy"};
            case 6: //charizard
                return  new String[] {"fighting", "bugtype", "grass", "ice"};
            case 9: //blastoise
                return  new String[] {"ground", "rock", "firetype"};
            case 59: //arcanine
                return  new String[] {"bugtype", "steel", "grass", "ice"};
            case 65: //alakazam
                return  new String[] {"fighting", "poison"};
            case 68: //machamp
                return  new String[] {"normal", "rock", "steel", "ice", "dark"};
            case 89: //muk
                return  new String[] {"grass", "fair"};
            case 94: //gengar
                return  new String[] {"grass", "psychic", "fairy"};
            case 110: //weezing
                return  new String[] {"grass", "fairy"};
            case 112: //rhydon
                return  new String[] {"poison", "rock", "firetype", "electric", "ice"};
            case 125: //electabuzz
                return  new String[] {"flying", "water"};
            case 126: //magmar
                return  new String[] {"bugtype", "steel", "grass", "ice"};
            case 131: //lapras
                return  new String[] {"flying", "ground", "grass", "dragontype", "rock", "firetype"};
            case 129: //magikarp
                return  new String[] {"ground", "rock", "firetype"};
            case 134: //vaporeon
                return  new String[] {"ground", "rock", "firetype"};
            case 135: //flareon
                return  new String[] {"bugtype", "steel", "grass", "ice"};
            case 136: //jolteon
                return  new String[] {"flying", "water"};
            case 143: //snorlax
                return  new String[] {};
            case 153: //bayleef
                return  new String[] {"ground", "rock", "water"};
            case 156: //quilava
                return  new String[] {"bugtype", "steel", "grass", "ice"};
            case 159: //croconaw
                return  new String[] {"ground", "rock", "firetype"};
            case 248: //tyranitar
                return  new String[] {"flying", "bugtype", "ghosttype", "firetype", "psychic", "ice"};
        }
        return new String[]{};
    }

    public static void loadEmotes() {
        emotes.put("steel",jda.getEmoteById(331594244957405195L));
        emotes.put("bugtype",jda.getEmoteById(331594245087690753L));
        emotes.put("water",jda.getEmoteById(331594244923850754L));
        emotes.put("ice",jda.getEmoteById(331594245305663489L));
        emotes.put("ground",jda.getEmoteById(331594244957667330L));
        emotes.put("grass",jda.getEmoteById(331594246102581248L));
        emotes.put("ghosttype",jda.getEmoteById(331594245167251456L));
        emotes.put("flying",jda.getEmoteById(331594245171445760L));
        emotes.put("firetype",jda.getEmoteById(331594245037359106L));
        emotes.put("fighting",jda.getEmoteById(331594245037359115L));
        emotes.put("fairy",jda.getEmoteById(331594245066719232L));
        emotes.put("electric",jda.getEmoteById(331594245053874176L));
        emotes.put("dragontype",jda.getEmoteById(331594245695864842L));
        emotes.put("normal",jda.getEmoteById(331594245788139520L));
        emotes.put("poison",jda.getEmoteById(331594245116919809L));
        emotes.put("psychic",jda.getEmoteById(331594245246943232L));
        emotes.put("rock",jda.getEmoteById(331594245037359107L));
    }
}
