package parser;

import java.util.*;

public enum InputError
{
    InvalidArg,
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
            case BlacklistedPokemon: {
                String str = "One or more pokemon you entered aren't being scanned for: \n\n";
                for (final String s : userCommand.getBlacklisted()) {
                    str = str + "  " + s + "\n";
                }
                return str;
            }
            case TooManyArgs: {
                final int max = Commands.get((String)userCommand.getArg(0).getParams()[0]).getMaxArgs();
                final String str = "You entered too many options. That command can have at most " + max + ((max == 1) ? " option" : " options");
                return str;
            }
            case NotEnoughArgs: {
                final int min = Commands.get((String)userCommand.getArg(0).getParams()[0]).getMinArgs();
                final String str = "You didn't specify enough options. That command needs at least " + min + ((min == 1) ? " option" : " options");
                return str + "\n\n";
            }
            case DuplicateArgs: {
                final ArgType duplicateType = Argument.getDuplicateArg(userCommand.getArgs());
                String str = "You specified multiple " + duplicateType;
                if (duplicateType == ArgType.Iv) {
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
                String str = "I couldn't recognise the following things:\n\n";
                str += Argument.malformedToString(userCommand.getMalformedArgs());
                return str;
            }
            case InvalidCommand: {
                final String str = "I don't recognise that command. Use the `!help` command for a list of my commands";
                return str;
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
