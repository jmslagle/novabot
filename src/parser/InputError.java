package parser;

import java.util.ArrayList;
import java.util.HashSet;

public enum InputError
{
    InvalidArg,
    UnusableLocation,
    BlacklistedPokemon,
    TooManyArgs,
    NotEnoughArgs,
    DuplicateArgs,
    MissingRequiredArg,
    MalformedArg,
    InvalidCommand;

    public String getErrorMessage(final UserCommand userCommand) {
        switch (this) {
            case InvalidArg: {
                String str = "That command can only accept ";
                final HashSet<ArgType> argTypes = Commands.get((String)userCommand.getArg(0).getParams()[0]).validArgTypes;
                str = str + ArgType.setToString(argTypes) + "";
                return str;
            }
//            case UnusableLocation: {
//                String str = "You specified one or more locations unusable by your access level.\n\n";
//
//                Argument argument = userCommand.getArg(ArgType.Locations);
//
//                HashMap<Reason,ArrayList<Location>> unusableMap = new HashMap<>();
//
//                for (Object o : argument.getParams()) {
//                    Location location = (Location) o;
//
//                    if(!location.usable){
//                        if(!unusableMap.containsKey(location.reason)){
//                            unusableMap.put(location.reason,new ArrayList<>());
//                        }
//                        unusableMap.get(location.reason).add(location);
//                    }
//                }
//
//                for (Reason reason : unusableMap.keySet()) {
//                    str += String.format("**%s:**%n",reason);
//
//                    for (Location location : unusableMap.get(reason)) {
//                        str += String.format("  %s%n",location.getSuburb());
//                    }
//
//                    str += "\n";
//                }
//
//                if(unusableMap.containsKey(Reason.SupporterAttemptedPublic)){
//                    str += "Instead of using Discord channels for notifications, supporters have direct access to ALL pokemon spawns," +
//                            " and instead can subscribe to all spawns or filter them based on suburb";
//
//                    if(config.useGeofences()) str += " or geofences";
//
//                    str += ".";
//                }
//                return str;
//            }
            case BlacklistedPokemon: {
                String str = "One or more pokemon you entered aren't being scanned for: \n\n";
                for (final String s : userCommand.getBlacklisted()) {
                    str = str + "  " + s + "\n";
                }
                return str;
            }
            case TooManyArgs: {
                int max = Commands.get((String)userCommand.getArg(0).getParams()[0]).getMaxArgs();
                return "You entered too many options. That command can have at most " + max + ((max == 1) ? " option" : " options");
            }
            case NotEnoughArgs: {
                int min = Commands.get((String)userCommand.getArg(0).getParams()[0]).getMinArgs();
                String str = "You didn't specify enough options. That command needs at least " + min + ((min == 1) ? " option" : " options");
                return str + "\n\n";
            }
            case DuplicateArgs: {
                final ArgType duplicateType = Argument.getDuplicateArg(userCommand.getArgs());
                String str = "You specified multiple " + duplicateType;
                if (duplicateType == ArgType.Float || duplicateType == ArgType.Int) {
                    return str;
                }
                str += " without putting them in a list.";
                str = str + " If you want to specify multiple " + duplicateType + ((duplicateType != ArgType.Pokemon) ? "s" : "") + ", please put them in a list like so:\n\n";
                assert duplicateType != null;
                switch (duplicateType) {
                    case Pokemon:
                        str += "`!addpokemon <dragonite,lapras,unown>`";
                        break;
                    case Locations:
                        str += "`!addpokemon lapras <belconnen,gungahlin>`";
                        break;

                }
                return str + "\n\n";
            }
            case MissingRequiredArg: {
                final HashSet<ArgType> requiredArgs = Commands.get((String)userCommand.getArg(0).getParams()[0]).getRequiredArgTypes();
                final String str = "For that command you must specify one or more " + ArgType.setToString(requiredArgs);
                return str + "\n\n";
            }
            case MalformedArg: {
                return "I couldn't recognise the following things:\n\n" + Argument.malformedToString(userCommand.getMalformedArgs());
            }
            case InvalidCommand: {
                return "I don't recognise that command. Use the `!help` command for a list of my commands";
            }
            default:
                return null;
        }
    }

    public static InputError mostSevere(final ArrayList<InputError> exceptions) {
        InputError error = InputError.InvalidArg;
        for (final InputError exception : exceptions) {
            if (exception.ordinal() > error.ordinal()) {
                error = exception;
            }
        }
        return error;
    }

}
