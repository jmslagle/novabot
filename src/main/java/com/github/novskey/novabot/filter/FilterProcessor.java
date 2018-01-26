package com.github.novskey.novabot.filter;

import com.github.novskey.novabot.pokemon.PokeSpawn;
import com.github.novskey.novabot.raids.RaidSpawn;
import com.google.gson.JsonObject;

public interface FilterProcessor {
    boolean matchesFilter(String filterName, RaidSpawn raidSpawn);

    boolean matchesFilter(String filterName, PokeSpawn pokeSpawn);

    boolean loadFilter(String filterName, Type type);

    enum Type {
        POKEMON,
        RAID
    }
}
