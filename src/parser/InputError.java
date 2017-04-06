package parser;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Owner on 6/04/2017.
 */
public enum InputError {
    InvalidArg,
    TooManyArgs,
    NotEnoughArgs,
    DuplicateArgs,
    MissingRequiredArg,
    MalformedArg,
    InvalidCommand;

    public String getErrorMessage(UserCommand userCommand){
        String str;

        switch (this){
            case InvalidArg:
                str = "That command can't accept " +  " argument(s)";
                return str;
            case TooManyArgs:
                str = "Too many arguments";
                return str;
            case NotEnoughArgs:
                int min = Commands.get((String) userCommand.getArg(0).getParams()[0]).getMinArgs();
                str = "You didn't specify enough arguments. That command needs at least " + min + (min == 1 ? " argument" : " arguments");

                return str + "\n\n";
            case DuplicateArgs:
                ArgType duplicateType = Argument.getDuplicateArg(userCommand.getArgs());
                str = "You specified duplicate " + duplicateType + " arguments.";

                if(duplicateType == ArgType.Iv) return str + "\n\n";

                str += " If you want to specify multiple " +duplicateType+ (duplicateType!=ArgType.Pokemon ? "s" :"") +
                        ", please put them in a list like so:\n\n";

                switch(duplicateType){
                    case Pokemon:
                        str += "`!addpokemon <dragonite,lapras,unown>`";
                        break;
                    case Regions:
                        str += "`!addpokemon lapras  <belconnen,gungahlin>`";
                        break;
                }

                return str + "\n\n";
            case MissingRequiredArg:
                HashSet<ArgType> requiredArgs = Commands.get((String) userCommand.getArg(0).getParams()[0]).getRequiredArgTypes();
                str = "You didn't specify " + (requiredArgs.size() == 1 ? "the" : "") + " required argument" + (requiredArgs.size() > 1 ? "s" : "") + ". That command requires a ";

                str += ArgType.setToString(requiredArgs) + " argument";

                return str + "\n\n";
            case MalformedArg:
                str = "I couldn't recognise the following arguments:\n\n";

                str += Argument.malformedToString(userCommand.getMalformedArgs());

                return str;
            case InvalidCommand:
                str = "I don't recognise that command. Use the `!help` command for a list of my commands";
                return str;
        }

        return null;
    }

    public static InputError mostSevere(ArrayList<InputError> exceptions) {
        InputError error = InvalidArg;

        for (InputError exception : exceptions) {
            if(exception.ordinal() > error.ordinal()) error = exception;
        }

        return error;
    }
}
