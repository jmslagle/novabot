package parser;

import core.*;
import maps.GeofenceIdentifier;
import maps.Geofencing;
import nests.NestStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser
{
    private static final Pattern PATTERN;

    public static UserCommand parseInput(final String input, boolean supporter) {
        final UserCommand command = new UserCommand();
        final Argument[] args = getArgs(input,supporter);
        final String firstArg = (String)args[0].getParams()[0];
        command.setArgs(args);
        if (!Commands.isCommandWithArgs(firstArg)) {
            command.addException(InputError.InvalidCommand);
            return command;
        }
        final Command cmd = Commands.get(firstArg);
        for (final Argument arg : args) {
            if (!arg.fullyParsed()) {
                command.addException(InputError.MalformedArg);
                return command;
            }
        }
        if (!cmd.meetsRequirements(args)) {
            command.addException(InputError.MissingRequiredArg);
            return command;
        }
        if (!cmd.allowDuplicateArgs && Argument.containsDuplicates(args)) {
            command.addException(InputError.DuplicateArgs);
            return command;
        }
        if (args.length - 1 < cmd.minArgs) {
            command.addException(InputError.NotEnoughArgs);
            return command;
        }
        if (args.length - 1 > cmd.maxArgs) {
            command.addException(InputError.TooManyArgs);
            return command;
        }
        if (!args[0].getParams()[0].equals("!nest") && command.containsArg(ArgType.Pokemon) && command.containsBlacklisted()) {
            command.addException(InputError.BlacklistedPokemon);
            return command;
        }
        for (Argument arg : args) {
            if(arg.getType() == ArgType.Locations){
                for (Object o : arg.getParams()) {
                    Location l = (Location) o;

                    ArrayList<String> unusable = new ArrayList<>();

                    if(!l.usable){
                        unusable.add(l.getSuburb());
                    }
                    if(unusable.size() > 0) {
                        arg.setMalformed(unusable);
                        command.addException(InputError.UnusableLocation);
                        return command;
                    }
                }
            }
        }

        for (final Argument arg : args) {
            if (!cmd.validArgTypes.contains(arg.getType())) {
                command.addException(InputError.InvalidArg);
                return command;
            }
        }
        return command;
    }

    private static Argument[] getArgs(final String input, boolean supporter) {
        final ArrayList<Argument> args = new ArrayList<Argument>();
        final Matcher matcher = Parser.PATTERN.matcher(input);
        while (matcher.find()) {
            final String group = matcher.group();
            if (group.charAt(0) == '<') {
                args.add(parseList(group,supporter));
            }
            else {
                args.add(getArg(group, supporter));
            }
        }
        final Argument[] arguments = new Argument[args.size()];
        return args.toArray(arguments);
    }

    private static Argument getArg(final String s, boolean supporter) {
        final Argument argument = new Argument();
        if (Commands.isCommandWithArgs(s.trim())) {
            argument.setType(ArgType.CommandStr);
            argument.setParams(new Object[] { s });
        }
        else if (Pokemon.nameToID(s.trim()) != 0) {
            argument.setType(ArgType.Pokemon);
            argument.setParams(new Object[] { s.trim() });
        }
        else {
            final Location location;
            if ((location = Location.fromString(s.trim(),supporter)) != null) {
                argument.setType(ArgType.Locations);

                ArrayList<Location> locations = new ArrayList<>();

                if(location.locationType == LocationType.Geofence){
                    for (GeofenceIdentifier geofenceIdentifier : location.geofenceIdentifiers) {
                        locations.add(new Location(geofenceIdentifier));
                    }
                }

                if(locations.size() == 0){
                    locations.add(location);
                }
                argument.setParams(locations.toArray());
            }
            else if (NestStatus.fromString(s.trim()) != null) {
                argument.setType(ArgType.Status);
                argument.setParams(new Object[] { NestStatus.fromString(s.trim()) });
            }
            else if(TimeUnit.fromString(s.trim()) != null){
                argument.setType(ArgType.TimeUnit);
                argument.setParams(new Object[] { TimeUnit.fromString(s.trim())});
            }
            else if (getInt(s.trim()) != null) {
                argument.setType(ArgType.Int);
                argument.setParams(new Object[] { getInt(s.trim()) });
            }
            else if (getFloat(s.trim()) != null) {
                argument.setType(ArgType.Float);
                argument.setParams(new Object[] { getFloat(s.trim()) });
            }
            else {
                argument.setType(ArgType.Unknown);
                argument.setParams(new Object[] { null });
                argument.setMalformed(new ArrayList<String>(Arrays.asList(s)));
            }
        }
        return argument;
    }


    private static Integer getInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static Float getFloat(final String s) {
        try {
            return Float.parseFloat(s.trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static Argument parseList(final String group, boolean supporter) {
        final Argument argument = new Argument();
        final String toSplit = group.substring(1, group.length() - 1);
        final String[] strings = toSplit.split(",");
        final ArrayList<Object> args = new ArrayList<>();
        final ArrayList<String> malformed = new ArrayList<String>();
        for (int i = 0; i < strings.length; ++i) {
            Location loc = Location.fromString(strings[i].trim(), supporter);

            if (loc == null) {
                malformed.add(strings[i].trim());
                args.add(loc);
                continue;
            }

            if(loc.locationType == LocationType.Geofence){
                for (GeofenceIdentifier geofenceIdentifier : loc.geofenceIdentifiers) {
                    args.add(new Location(geofenceIdentifier));
                }
            }else{
                args.add(loc);
            }

        }
        if (allNull(args.toArray())) {
            args.clear();
            malformed.clear();
            for (int i = 0; i < strings.length; ++i) {
                final Pokemon pokemon = new Pokemon(strings[i].trim());
                if (pokemon.name == null) {
                    malformed.add(strings[i].trim());
                }
                args.add(pokemon.name);
            }
            if (!allNull(args.toArray())) {
                argument.setType(ArgType.Pokemon);
            }
            else {
                args.clear();
                malformed.clear();
                if (args.size() == 1 || args.size() == 2) {
                    for (int i = 0; i < strings.length; ++i) {
                        args.add(getFloat(strings[i].trim()));
                    }
                    if (!allNull(new ArrayList[]{args})) {
                        argument.setType(ArgType.Float);
                    }
                    else {
                        malformed.clear();
                        for (int i = 0; i < strings.length; ++i) {
                            NestStatus status = NestStatus.fromString(strings[i].trim());
                            args.add(status);
                            if (status == null) {
                                malformed.add(strings[i].trim());
                            }
                        }
                        if (!allNull(args.toArray())) {
                            argument.setType(ArgType.Status);
                        }
                    }
                }
            }
        }
        else {
            argument.setType(ArgType.Locations);
        }
        if (argument.getType() != null) {
            argument.setParams(args.toArray());
            argument.setMalformed(malformed);
        }
        else {
            argument.setType(ArgType.Unknown);
            argument.setParams(args.toArray());
            argument.setMalformed(new ArrayList<String>(Arrays.asList(strings)));
        }
        return argument;
    }

    private static boolean allNull(final Object[] args) {
        for (final Object arg : args) {
            if (arg != null) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        MessageListener.loadConfig();

        if(MessageListener.config.useChannels()){
            FeedChannels.loadChannels();
        }

        if(MessageListener.config.useGeofences()){
            Geofencing.loadGeofences();
        }

        MessageListener.loadSuburbs();
        System.out.println(Location.fromString("turner",true));
        UserCommand command = parseInput("!addpokemon <aerodactyl, chansey, miltank> <turner>",false);
        System.out.println(command.getExceptions());
    }

    static {
        PATTERN = Pattern.compile("(?=\\S*[.'-])([a-zA-Z0-9.'-]+)|!?\\w+|<(.*?)>");
    }
}
