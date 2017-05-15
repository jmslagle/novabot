package parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static core.MessageListener.config;

class Commands
{
    private static final HashMap<String, Command> commands;

    public static boolean isCommandWithArgs(final String s) {
        return Commands.commands.get(s) != null;
    }

    public static Command get(final String firstArg) {
        return Commands.commands.get(firstArg);
    }

    static {
        commands = new HashMap<String, Command>();
        final Command addPokemon = new Command();
        addPokemon.setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Locations, ArgType.Float, ArgType.Int)));
        addPokemon.setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.Pokemon)));
        addPokemon.setArgRange(1, 3);

        final Command addChannel = new Command();
        addChannel.setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Locations)));
        addChannel.setRequiredArgTypes(addChannel.getValidArgTypes());
        addChannel.setArgRange(1, 1);

        final Command delPokemon = new Command();
        delPokemon.setValidArgTypes(addPokemon.getValidArgTypes());
        addPokemon.setRequiredArgTypes(addPokemon.getRequiredArgTypes());
        delPokemon.setArgRange(1, 3);

        final Command clearPokemon = new Command();
        clearPokemon.setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)));
        clearPokemon.setRequiredArgTypes(clearPokemon.getValidArgTypes());
        clearPokemon.setArgRange(1, 1);

        final Command clearLocation = new Command();
        clearLocation.setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Locations)));
        clearLocation.setRequiredArgTypes(clearLocation.getValidArgTypes());
        clearLocation.setArgRange(1, 1);

        if(config.nestsEnabled()) {
            final Command nest = new Command();
            nest.setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Status)));
            nest.setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)));
            nest.setArgRange(1, 2);
            Commands.commands.put("!nest", nest);
        }

        if(config.statsEnabled()) {
            final Command countPokemon = new Command();
            countPokemon.setValidArgTypes(new HashSet<>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Int, ArgType.TimeUnit)));
            countPokemon.setRequiredArgTypes(new HashSet<>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)));
            countPokemon.setArgRange(1, 3);
            Commands.commands.put("!countpokemon", countPokemon);
        }

        Commands.commands.put("!addpokemon", addPokemon);
        Commands.commands.put("!addchannel", addChannel);
        Commands.commands.put("!delpokemon", delPokemon);
        Commands.commands.put("!clearpokemon", clearPokemon);
        Commands.commands.put("!clearlocation", clearLocation);
    }
}
