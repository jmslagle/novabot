package com.github.novskey.novabot.parser;

import java.util.ArrayList;
import java.util.HashSet;

public enum InputError
{
    InvalidArg,
    BlacklistedPokemon,
    InvalidArgCombination,
    DuplicateArgs,
    MissingRequiredArg,
    MalformedArg,
    InvalidCommand, ;

    public String getErrorMessage(final UserCommand userCommand) {
        switch (this) {
            case InvalidArg: {
                String                 str      = "That command can only accept ";
                final HashSet<ArgType> argTypes = userCommand.novaBot.commands.get((String) userCommand.getArg(0).getParams()[0]).validArgTypes;
                str = str + ArgType.setToString(argTypes) + "";
                return str;
            }
            case BlacklistedPokemon: {
                StringBuilder str = new StringBuilder("One or more pokemon you entered have been blacklisted by the server owner: \n\n");
                for (final String s : userCommand.getBlacklisted()) {
                    str.append("  ").append(s).append("\n");
                }
                return str.toString();
            }
            case InvalidArgCombination: {
                StringBuilder str = new StringBuilder("You entered an invalid argument combination for that command. Try `!help ")
                        .append(((String)userCommand.getArg(ArgType.CommandStr).getParams()[0]).substring(1))
                        .append("` to see all valid combinations.");
                return str.toString();
            }
            case DuplicateArgs: {
                final ArgType duplicateType = Argument.getDuplicateArg(userCommand.getArgs());
                String str = "You specified multiple " + duplicateType;
                if (duplicateType == ArgType.Float || duplicateType == ArgType.Int || duplicateType == ArgType.IV) {
                    return str;
                }
                if (duplicateType == ArgType.CommandStr) {
                    return str + "s. Please enter your commands one at a time in separate messages.";
                }
                if (duplicateType == ArgType.Locations) {
                    str += "s";
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
                final HashSet<ArgType> requiredArgs = userCommand.novaBot.commands.get((String) userCommand.getArg(0).getParams()[0]).getRequiredArgTypes();
                final String           str          = "For that command you must specify one or more " + ArgType.setToString(requiredArgs);
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
