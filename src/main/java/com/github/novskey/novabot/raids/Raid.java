package com.github.novskey.novabot.raids;

import com.github.novskey.novabot.core.Location;
import com.github.novskey.novabot.core.Types;
import com.github.novskey.novabot.pokemon.Pokemon;

import java.util.*;

/**
 * Created by Owner on 27/06/2017.
 */
public class Raid {

    public int bossId;
    public Location location;


    public Raid(){

    }

    public Raid(int bossId, Location location){
        this.bossId = bossId;
        this.location = location;
    }

    @Override
    public int hashCode() {
        return bossId *
                (location == null ? 1 : location.toDbString().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())) return false;
        Raid raid = (Raid) obj;
        return raid.bossId == this.bossId && raid.location.toDbString().equals(this.location.toDbString());
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

        weaknesses = Types.getEmoteNames(weaknesses);

        String weaknessArray[] = new String[weaknesses.size()];
        return weaknesses.toArray(weaknessArray);
    }

    public static String[] getBossStrengthsEmote(int move1, int move2){
        HashSet<String> strengths = new HashSet<>();

        strengths.addAll(Types.getStrengths(Pokemon.getMoveType(move1)));
        strengths.addAll(Types.getStrengths(Pokemon.getMoveType(move2)));

        strengths = Types.getEmoteNames(strengths);

        String strengthsArray[] = new String[strengths.size()];
        return strengths.toArray(strengthsArray);
    }

}
