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

    public static String getBossWeaknessEmotes(int bossId){
        switch(bossId){
            case 3: //venusaur
                return ":fire::psychic::flying::ice:";
            case 6: //charizard
                return ":rock::water::electric:";
            case 9: //blastoise
                return ":grass::electric:";
            case 59: //arcanine
                return ":ground::water::rock:";
            case 65: //alakazam
                return ":dark::bugtype::ghost:";
            case 68: //machamp
                return ":fairy::flying::psychic:";
            case 89: //muk
                return ":psychic::ground:";
            case 94: //gengar
                return ":psychic::dark::ghost:";
            case 110: //weezing
                return ":psychic:";
            case 112: //rhydon
                return ":water::grass:ground::fighting::ice::steel:";
            case 125: //electabuzz
                return ":ground:";
            case 126: //magmar
                return ":ground::water::rock:";
            case 131: //lapras
                return ":grass::electric::rock::fighting:";
            case 129: //magikarp
                return ":electric::grass:";
            case 134: //vaporeon
                return ":electric::grass:";
            case 135: //flareon
                return ":ground::water::rock:";
            case 136: //jolteon
                return ":ground:";
            case 143: //snorlax
                return ":fighting:";
            case 153: //bayleef
                return ":ice::fire::bugtype::poison:";
            case 156: //quilava
                return ":ground::water::rock:";
            case 159: //croconaw
                return ":electric::grass:";
            case 248: //tyranitar
                return ":fighting::grass::bugtype::ground::water::steel::fairy:";
        }
        return "";
    }

    public static String getBossStrengthsEmote(int bossId){
        switch(bossId){
            case 3: //venusaur
                return ":water::fairy:";
            case 6: //charizard
                return ":fighting::bugtype::grass::ice:";
            case 9: //blastoise
                return ":ground::rock::fire:";
            case 59: //arcanine
                return ":bugtype::steel::grass::ice:";
            case 65: //alakazam
                return ":fighting::poison:";
            case 68: //machamp
                return ":normal::rock::steel::ice::dark:";
            case 89: //muk
                return ":grass::fair:";
            case 94: //gengar
                return ":grass::psychic::fairy:";
            case 110: //weezing
                return ":grass::fairy:";
            case 112: //rhydon
                return ":poison::rock::fire::electric::ice:";
            case 125: //electabuzz
                return ":flying::water:";
            case 126: //magmar
                return ":bugtype::steel::grass::ice:";
            case 131: //lapras
                return ":flying::ground::grass::dragon::rock::fire:";
            case 129: //magikarp
                return ":ground::rock::fire:";
            case 134: //vaporeon
                return ":ground::rock::fire:";
            case 135: //flareon
                return ":bugtype::steel::grass::ice:";
            case 136: //jolteon
                return ":flying::water:";
            case 143: //snorlax
                return "nothing";
            case 153: //bayleef
                return ":ground::rock::water:";
            case 156: //quilava
                return ":bugtype::steel::grass::ice:";
            case 159: //croconaw
                return ":ground::rock::fire:";
            case 248: //tyranitar
                return ":flying::bugtype::ghost::fire::psychic::ice:";
        }
        return "";
    }
}
