package com.github.novskey.Util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.novskey.novabot.pokemon.Pokemon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Paris on 23/12/2017.
 */
public class PokeBotMigrator {

    static JsonObject userFilters;

    public static void main(String[] args) {

        if(args.length == 0){
            System.out.println("Please specify a user filters file to migrate.");
            return;
        }

        JsonParser parser = new JsonParser();

        try {
            JsonElement element = parser.parse(new FileReader(args[0]));

            if (element.isJsonObject()) {
               userFilters = element.getAsJsonObject();
            }else{
                System.out.println("Invalid JSON");
                return;
            }

            for (Map.Entry<String, JsonElement> entry : userFilters.entrySet()) {
                String userId = entry.getKey();
                JsonObject filters = entry.getValue().getAsJsonObject();
                boolean paused = filters.get("paused").getAsBoolean();
                JsonArray locations = filters.get("areas").getAsJsonArray();
                JsonObject raids = filters.get("raids").getAsJsonObject();
                JsonObject pokemon = filters.get("pokemon").getAsJsonObject();

                ArrayList<Pokemon> pokeList = new ArrayList<>();

                for (Map.Entry<String, JsonElement> pokeEntry : pokemon.entrySet()) {
                    String pokeName = pokeEntry.getKey().toLowerCase();
                    JsonElement filterElement = pokeEntry.getValue();

                    float min_iv = 0;
                    float max_iv = 100;

                    if (filterElement.isJsonObject()){
                        JsonObject filterObject = filterElement.getAsJsonObject();
                        JsonElement minObj = filterObject.get("min_iv");
                        JsonElement maxObj = filterObject.get("max_iv");

                        if(minObj != null){
                            min_iv = minObj.getAsFloat();
                        }

                        if(maxObj != null){
                            max_iv = maxObj.getAsFloat();
                        }
                    }

                    if(Pokemon.nameToID(pokeName) != 0) {
                        pokeList.add(new Pokemon(Pokemon.nameToID(pokeName), min_iv, max_iv));
                    }
                }
                System.out.println(userId + " paused = " + paused);
                System.out.println(locations);
                System.out.println(pokeList);
                System.out.println("");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
