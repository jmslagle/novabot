package com.github.novskey.novabot.raids;

import com.github.novskey.novabot.Util.StringLocalizer;
import com.github.novskey.novabot.core.Location;
import com.github.novskey.novabot.core.Types;
import com.github.novskey.novabot.pokemon.Pokemon;

import java.util.*;

/**
 * Created by Owner on 27/06/2017.
 */
public class Raid {

    public int bossId;
    public int eggLevel = 0;
    public Location location;
    public String gymName = "";


    public Raid(){

    }

    public Raid(int bossId, Location location){
        this.bossId = bossId;
        this.location = location;
    }

    public Raid(int bossId,int eggLevel, String gymName,Location location){
        this(bossId,location);
        this.eggLevel = eggLevel;
        this.gymName = gymName;
    }

    public static String getRaidsString(Raid[] raids) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < raids.length; i++) {
            Raid raid = raids[i];
            if(i != 0){
                str.append(", ");
            }
            if (raid.bossId != 0) {
                str.append(String.format("%s %s", Pokemon.idToName(raid.bossId), StringLocalizer.getLocalString("Raids")));
            }else{
                str.append(String.format("%s %s %s", StringLocalizer.getLocalString("Level"), raid.eggLevel, StringLocalizer.getLocalString("Eggs")));
            }

            if (!raid.gymName.equals("")){
                str.append(String.format(" %s %s",StringLocalizer.getLocalString("At"),raid.gymName));
            }
        }
        return str.toString();
    }

    @Override
    public int hashCode() {
        return bossId *
                (location == null ? 1 : location.toDbString().hashCode()) *
               (eggLevel+1) * (gymName.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())) return false;
        Raid raid = (Raid) obj;
        return raid.bossId == this.bossId && raid.gymName.equalsIgnoreCase(this.gymName) && raid.eggLevel == this.eggLevel && raid.location.toDbString().equals(this.location.toDbString());
    }

    @Override
    public String toString() {
        return String.format("RAID: %s,%s,%s,%s",bossId,eggLevel, gymName,location);
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
