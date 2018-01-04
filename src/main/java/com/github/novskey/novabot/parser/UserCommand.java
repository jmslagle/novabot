package com.github.novskey.novabot.parser;

import com.github.novskey.novabot.core.Location;
import com.github.novskey.novabot.core.NovaBot;
import com.github.novskey.novabot.pokemon.Pokemon;
import com.github.novskey.novabot.raids.Raid;

import java.util.ArrayList;
import java.util.HashMap;

public class UserCommand
{
    public final NovaBot novaBot;
    private Argument[] args;
    private final ArrayList<InputError> exceptions;

    public UserCommand(NovaBot novaBot) {
        this.exceptions = new ArrayList<>();
        this.novaBot = novaBot;
    }

    public ArrayList<InputError> getExceptions() {
        return this.exceptions;
    }

    public void setArgs(final Argument[] args) {
        this.args = args;
    }

    public Pokemon[] buildPokemon() {
        Location[] locations = {Location.ALL};
        float miniv = 0.0f;
        float maxiv = 100.0f;
        String[] pokeNames = new String[0];
        for (final Argument arg : this.args) {
            switch (arg.getType()) {
                case Locations:
                    locations = this.toLocations(arg.getParams());
                    break;
                case Pokemon:
                    pokeNames = this.toStrings(arg.getParams());
                    break;
                case Float:
                    miniv = (float)arg.getParams()[0];
                    if (arg.getParams().length == 2) {
                        maxiv = (float)arg.getParams()[1];
                        break;
                    }
                    break;
                case Int:
                    miniv = ((Integer)arg.getParams()[0]).floatValue();
                    if (arg.getParams().length == 2) {
                        maxiv = ((Integer)arg.getParams()[1]).floatValue();
                        break;
                    }
                    break;
            }
        }
        final ArrayList<Pokemon> pokemons = new ArrayList<>();
        for (final String pokeName : pokeNames) {
            for (final Location location : locations) {
                System.out.println(pokeName);
                pokemons.add(new Pokemon(pokeName, location, miniv, maxiv));
            }
        }
        final Pokemon[] pokeArray = new Pokemon[pokemons.size()];
        return pokemons.toArray(pokeArray);
    }

    private Location[] toLocations(final Object[] params) {
        final Location[] locations = new Location[params.length];
        for (int i = 0; i < params.length; ++i) {
            locations[i] = (Location)params[i];
        }
        return locations;
    }

    private String[] toStrings(final Object[] params) {
        final String[] strings = new String[params.length];
        for (int i = 0; i < params.length; ++i) {
            strings[i] = params[i].toString();
        }
        return strings;
    }

    public Argument getArg(final int i) {
        return this.args[i];
    }

    public void addException(final InputError e) {
        this.exceptions.add(e);
    }

    public Argument[] getArgs() {
        return this.args;
    }

    public HashMap<ArgType, ArrayList<String>> getMalformedArgs() {
        final HashMap<ArgType, ArrayList<String>> malformed = new HashMap<>();
        for (final Argument arg : this.args) {
            if (arg.notFullyParsed()) {
                if (!malformed.containsKey(arg.getType())) {
                    final ArrayList<String> newList = new ArrayList<>();
                    malformed.put(arg.getType(), newList);
                }
                for (final String s : arg.getMalformed()) {
                    malformed.get(arg.getType()).add(s);
                }
            }
        }
        return malformed;
    }

    public boolean containsArg(final ArgType argType) {
        for (final Argument arg : this.args) {
            if (arg.getType() == argType) {
                return true;
            }
        }
        return false;
    }

    public Argument getArg(final ArgType argType) {
        for (final Argument arg : this.args) {
            if(arg == null) continue;
            if (arg.getType() == argType) {
                return arg;
            }
        }
        return null;
    }

    public Pokemon[] getUniquePokemon() {
        Argument pokemonArg = getArg(ArgType.Pokemon);
        if(pokemonArg == null) {
            return new Pokemon[] {};
        }

        String[] pokeNames;
        pokeNames = this.toStrings(pokemonArg.getParams());
        final Pokemon[] pokemons = new Pokemon[pokeNames.length];
        for (int i = 0; i < pokeNames.length; ++i) {
            pokemons[i] = new Pokemon(pokeNames[i]);
        }
        return pokemons;
    }

    public boolean containsBlacklisted() {
        return this.getBlacklisted().size() > 0;
    }

    public ArrayList<String> getBlacklisted() {
        final ArrayList<String> blacklisted = new ArrayList<>();
        for (final Object o : this.getArg(ArgType.Pokemon).getParams()) {
            if (novaBot.config.getBlacklist().contains(Pokemon.nameToID((String) o))) {
                blacklisted.add((String)o);
            }
        }
        return blacklisted;
    }

    public Location[] getLocations() {
        return this.toLocations(this.getArg(ArgType.Locations).getParams());
    }

    public String getIvMessage() {
        String message = "";
        if (containsArg(ArgType.Float)) {
            final Argument ivArg = getArg(ArgType.Float);
            if (ivArg.getParams().length == 1) {
                message = message + " " + ivArg.getParams()[0] + "% IV or above";
            }
            else {
                message = message + " between " + ivArg.getParams()[0] + " and " + ivArg.getParams()[1] + "% IV";
            }
        }else if(containsArg(ArgType.Int)){
            final Argument ivArg = getArg(ArgType.Int);
            if (ivArg.getParams().length == 1) {
                message = message + " " + ivArg.getParams()[0] + "% IV or above";
            }
            else {
                message = message + " between " + ivArg.getParams()[0] + " and " + ivArg.getParams()[1] + "% IV";
            }
        }
        return message;
    }


    public Raid[] buildRaids() {
        Location[] locations = { Location.ALL };
        String[] bossNames = new String[0];
        for (final Argument arg : this.args) {
            switch (arg.getType()) {
                case Locations:
                    locations = this.toLocations(arg.getParams());
                    break;
                case Pokemon:
                    bossNames = this.toStrings(arg.getParams());
                    break;
            }
        }

        final ArrayList<Raid> raids = new ArrayList<>();
        for (final String bossName : bossNames) {
            for (final Location location : locations) {
                Raid raid = new Raid(Pokemon.nameToID(bossName),location);
                System.out.println(raid);
                raids.add(raid);
            }
        }

        final Raid[] raidArray = new Raid[raids.size()];
        return raids.toArray(raidArray);
    }
}
