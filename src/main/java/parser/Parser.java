package parser;

import core.Location;
import core.LocationType;
import core.NovaBot;
import core.TimeUnit;
import maps.GeofenceIdentifier;
import pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
    private static final Pattern PATTERN;
    private final NovaBot novaBot;

    public Parser(NovaBot novaBot) {
        this.novaBot = novaBot;
    }

    public UserCommand parseInput(String input, boolean supporter) {
        input = input.toLowerCase();
        final UserCommand command = new UserCommand(novaBot);
        final Argument[] args = getArgs(input, supporter);
        final String firstArg = (String) args[0].getParams()[0];
        command.setArgs(args);
        if (!novaBot.commands.isCommandWithArgs(firstArg)) {
            command.addException(InputError.InvalidCommand);
            return command;
        }
        final Command cmd = novaBot.commands.get(firstArg);
        for (final Argument arg : args) {
            if (arg.notFullyParsed()) {
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
            if (arg.getType() == ArgType.Locations) {
                for (Object o : arg.getParams()) {
                    Location l = (Location) o;

                    ArrayList<String> unusable = new ArrayList<>();

                    if (!l.usable) {
                        unusable.add(l.getSuburb());
                    }
                    if (unusable.size() > 0) {
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

    private Argument getArg(final String s, boolean supporter) {
        final Argument argument = new Argument();
        if (novaBot.commands.isCommandWithArgs(s.trim())) {
            argument.setType(ArgType.CommandStr);
            argument.setParams(new Object[]{s});
        } else if (Pokemon.nameToID(s.trim()) != 0) {
            argument.setType(ArgType.Pokemon);
            argument.setParams(new Object[]{s.trim()});
        } else {
            final Location location;
            if ((location = Location.fromString(s.trim(), novaBot)) != null) {
                argument.setType(ArgType.Locations);

                ArrayList<Location> locations = new ArrayList<>();

                if (location.locationType == LocationType.Geofence) {
                    for (GeofenceIdentifier geofenceIdentifier : location.geofenceIdentifiers) {
                        locations.add(new Location(geofenceIdentifier));
                    }
                }

                if (locations.size() == 0) {
                    locations.add(location);
                }
                argument.setParams(locations.toArray());
            } else if (TimeUnit.fromString(s.trim()) != null) {
                argument.setType(ArgType.TimeUnit);
                argument.setParams(new Object[]{TimeUnit.fromString(s.trim())});
            }else if (novaBot.config.presets.get(s.trim()) != null) {
                    argument.setType(ArgType.Preset);
                    argument.setParams(new Object[]{s.trim()});
            } else if (getInt(s.trim().replace("iv","")) != null) {

                argument.setType(ArgType.Int);
                argument.setParams(new Object[]{getInt(s.trim().replace("iv",""))});
            } else if (getFloat(s.trim().replace("iv","")) != null) {
                argument.setType(ArgType.Float);
                argument.setParams(new Object[]{getFloat(s.trim().replace("iv",""))});
            } else {
                argument.setType(ArgType.Unknown);
                argument.setParams(new Object[]{null});
                argument.setMalformed(new ArrayList<>(Collections.singletonList(s)));
            }
        }
        return argument;
    }

    private Argument[] getArgs(final String input, boolean supporter) {
        final ArrayList<Argument> args    = new ArrayList<>();
        final Matcher             matcher = Parser.PATTERN.matcher(input);
        while (matcher.find()) {
            final String group = matcher.group();
            if (group.charAt(0) == '<') {
                args.add(parseList(group, supporter));
            } else {
                args.add(getArg(group, supporter));
            }
        }
        final Argument[] arguments = new Argument[args.size()];
        return args.toArray(arguments);
    }


    private static Integer getInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Float getFloat(final String s) {
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Argument parseList(final String group, boolean supporter) {
        final Argument argument = new Argument();
        final String toSplit = group.substring(1, group.length() - 1);
        final String[] strings = toSplit.split(",");
        final ArrayList<Object> args = new ArrayList<>();
        final ArrayList<String> malformed = new ArrayList<>();
        for (String string2 : strings) {
            Location loc = Location.fromString(string2.trim(), novaBot);

            if (loc == null) {
                malformed.add(string2.trim());
                args.add(null);
                continue;
            }

            if (loc.locationType == LocationType.Geofence) {
                for (GeofenceIdentifier geofenceIdentifier : loc.geofenceIdentifiers) {
                    args.add(new Location(geofenceIdentifier));
                }
            } else {
                args.add(loc);
            }

        }
        if (allNull(args.toArray())) {
            args.clear();
            malformed.clear();
            for (String string1 : strings) {
                final Pokemon pokemon = new Pokemon(string1.trim());
                if (pokemon.name == null) {
                    malformed.add(string1.trim());
                }
                args.add(pokemon.name);
            }
            if (!allNull(args.toArray())) {
                argument.setType(ArgType.Pokemon);
            } else {
                args.clear();
                malformed.clear();
                if (strings.length == 1 || strings.length == 2) {
                    for (String string : strings) {
                        args.add(getInt(string.trim()));
                    }
                    if (!allNull(new ArrayList[]{args})) {
                        argument.setType(ArgType.Int);
                    } else {
                        for (String string : strings) {
                            args.add(getFloat(string.trim()));
                        }
                        if (!allNull(new ArrayList[]{args})) {
                            argument.setType(ArgType.Float);
                        }
                    }
                } else {
                    args.clear();
                    malformed.clear();

                    for (String string : strings) {
                        String presetFilter = novaBot.config.presets.get(string.trim());
                        args.add(string.trim());
                        if (presetFilter == null) {
                            malformed.add(string.trim());
                        }
                    }

                    if (!allNull(args.toArray())) {
                        argument.setType(ArgType.Preset);
                    }
                }
            }
        } else {
            argument.setType(ArgType.Locations);
        }
        if (argument.getType() != null) {
            argument.setParams(args.toArray());
            argument.setMalformed(malformed);
        } else {
            argument.setType(ArgType.Unknown);
            argument.setParams(args.toArray());
            argument.setMalformed(new ArrayList<>(Arrays.asList(strings)));
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
        NovaBot novaBot = new NovaBot();
        novaBot.setup();
        novaBot.dbManager.novabotdbConnect();

        UserCommand command = novaBot.parser.parseInput("!clearpreset 100iv",true);
        Object[] presets = command.getArg(ArgType.Preset).getParams();
        novaBot.dbManager.clearPreset("paris", Arrays.copyOf(presets,presets.length,String[].class));
        System.out.println(command);
    }

    static {
        PATTERN = Pattern.compile("(?=\\S*[.'-])([a-zA-Z0-9.'-]+)|!?\\w+|<(.*?)>");
    }
}
