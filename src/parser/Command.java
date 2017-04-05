package parser;

import javafx.util.Pair;
import parser.exceptions.CommandException;

import java.util.HashSet;

/**
 * Created by Paris on 3/04/2017.
 */
public class Command {

    int minArgs;
    int maxArgs;

    HashSet<ArgType> validArgTypes = new HashSet<>();

    HashSet<ArgType> requiredArgTypes = new HashSet<>();

    Argument[] arguments;
    private CommandException exception = null;

    public Command(){}


    public static Pair<Integer,Integer> requiredArgs(String s){

        if(s.startsWith("!add") || s.startsWith("!del")){
            return new Pair<>(1,3);
        }

        if(s.startsWith("!clear")){
            return new Pair<>(1,1);
        }

        return null;
    }

    public void setException(CommandException e){
        exception = e;
    }

    public CommandException getException() {
        return exception;
    }

    public void setValidArgTypes(HashSet<ArgType> validArgTypes) {
        this.validArgTypes = validArgTypes;
    }

    public void setRequiredArgTypes(HashSet<ArgType> requiredArgTypes) {
        this.requiredArgTypes = requiredArgTypes;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public HashSet<ArgType> getValidArgTypes() {
        return validArgTypes;
    }

    public void setArgRange(int min, int max) {
        minArgs = min;
        maxArgs = max;
    }


    public HashSet<ArgType> getRequiredArgTypes() {
        return requiredArgTypes;
    }

    public boolean meetsRequirements(Argument[] args) {

        boolean meetsRequirements = true;

        for (ArgType argType : requiredArgTypes) {
            boolean containsThisArg = false;
            for (Argument arg : args) {
                if(arg.getType() == argType) containsThisArg = true;
            }

            if(!containsThisArg) return false;
        }

        return true;
    }
}
