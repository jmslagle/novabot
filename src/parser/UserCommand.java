package parser;

import core.*;
import nests.NestSearch;
import nests.NestStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class UserCommand
{
    private Argument[] args;
    private final ArrayList<InputError> exceptions;

    public UserCommand() {
        this.exceptions = new ArrayList<InputError>();
    }

    public ArrayList<InputError> getExceptions() {
        return this.exceptions;
    }

    public void setArgs(final Argument[] args) {
        this.args = args;
    }

    public Pokemon[] buildPokemon() {
        Location[] locations = { new Location(Region.All) };
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
        final ArrayList<Pokemon> pokemons = new ArrayList<Pokemon>();
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
        final HashMap<ArgType, ArrayList<String>> malformed = new HashMap<ArgType, ArrayList<String>>();
        for (final Argument arg : this.args) {
            if (!arg.fullyParsed()) {
                if (!malformed.containsKey(arg.getType())) {
                    final ArrayList<String> newList = new ArrayList<String>();
                    malformed.put(arg.getType(), newList);
                }
                for (final String s : arg.getMalformed()) {
                    malformed.get(arg.getType()).add(s);
                }
            }
        }
        return malformed;
    }

    public NestSearch buildNestSearch() {
        NestStatus[] statuses = { NestStatus.Confirmed, NestStatus.Suspected };
        Pokemon[] pokemons = new Pokemon[0];
        for (final Argument arg : this.args) {
            switch (arg.getType()) {
                case Status:
                    statuses = new NestStatus[arg.getParams().length];
                    for (int i = 0; i < arg.getParams().length; ++i) {
                        statuses[i] = (NestStatus)arg.getParams()[i];
                    }
                    break;
                case Pokemon:
                    pokemons = new Pokemon[arg.getParams().length];
                    for (int i = 0; i < arg.getParams().length; ++i) {
                        pokemons[i] = new Pokemon((String)arg.getParams()[i]);
                    }
                    break;
            }
        }
        return new NestSearch(pokemons, statuses);
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
            if (arg.getType() == argType) {
                return arg;
            }
        }
        return null;
    }

    public Pokemon[] getUniquePokemon() {
        String[] pokeNames = new String[0];
        pokeNames = this.toStrings(this.getArg(ArgType.Pokemon).getParams());
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
        final ArrayList<String> blacklisted = new ArrayList<String>();
        for (final Object o : this.getArg(ArgType.Pokemon).getParams()) {
            if (MessageListener.config.getBlacklist().contains(Pokemon.nameToID((String)o))) {
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
        Location[] locations = { new Location(Region.All) };
        int minLevel = 0;
        String[] bossNames = new String[0];
        for (final Argument arg : this.args) {
            switch (arg.getType()) {
                case Locations:
                    locations = this.toLocations(arg.getParams());
                    break;
                case Pokemon:
                    bossNames = this.toStrings(arg.getParams());
                    break;
                case Int:
                    minLevel = ((Integer)arg.getParams()[0]);
                    break;
            }
        }

        final ArrayList<Raid> raids = new ArrayList<>();
        for (final String bossName : bossNames) {
            for (final Location location : locations) {
                Raid raid = new Raid(0,Pokemon.nameToID(bossName),location);
                System.out.println(raid);
                raids.add(raid);
            }
        }

        if(minLevel != 0){
            for (Location location : locations) {
                Raid raid = new Raid(minLevel,0,location);
                System.out.println(raid);
                raids.add(raid);
            }
        }

        final Raid[] raidArray = new Raid[raids.size()];
        return raids.toArray(raidArray);
    }
}
