package parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static core.MessageListener.config;
import static core.MessageListener.loadConfig;

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
        final Command addPokemon = new Command()
                .setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Locations, ArgType.Float, ArgType.Int)))
                .setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.Pokemon)))
                .setArgRange(1, 3);

        final Command delPokemon = new Command()
                .setValidArgTypes(addPokemon.getValidArgTypes())
                .setRequiredArgTypes(addPokemon.getRequiredArgTypes())
                .setArgRange(1, 3);

        final Command addRaid = new Command()
                .setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Locations)))
                .setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.Pokemon)))
                .setArgRange(1, 3);

        final Command delRaid = new Command()
                .setValidArgTypes(addRaid.getValidArgTypes())
                .setRequiredArgTypes(addRaid.getRequiredArgTypes())
                .setArgRange(1, 3);

        final Command clearPokemon = new Command()
                .setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)))
                .setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)))
                .setArgRange(1, 1);

        final Command clearLocation = new Command()
                .setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Locations)))
                .setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Locations)))
                .setArgRange(1, 1);

        final Command addChannel = new Command()
                .setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Locations)))
                .setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Locations)))
                .setArgRange(1, 1);

        if(config.nestsEnabled()) {
            final Command nest = new Command()
                    .setValidArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Status)))
                    .setRequiredArgTypes(new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)))
                    .setArgRange(1, 2);
            Commands.commands.put("!nest", nest);
        }

        if(config.statsEnabled()) {
            final Command stats = new Command()
                    .setValidArgTypes(new HashSet<>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Int, ArgType.TimeUnit)))
                    .setRequiredArgTypes(new HashSet<>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon)))
                    .setArgRange(1, 3);
            Commands.commands.put("!stats", stats);
        }


        Commands.commands.put("!addpokemon", addPokemon);
        Commands.commands.put("!delpokemon", delPokemon);
        Commands.commands.put("!addraid", addRaid);
        Commands.commands.put("!delraid", delRaid);
        Commands.commands.put("!addchannel", addChannel);
        Commands.commands.put("!clearpokemon", clearPokemon);
        Commands.commands.put("!clearlocation", clearLocation);
    }

}
