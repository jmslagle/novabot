package parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Paris on 3/04/2017.
 */
public class Argument {


    private ArgType type;
    private Object[] params;
    private ArrayList<String> malformed;

    public Argument (ArgType argType, Object[] args){

    }


    public Argument() {

    }

    public Argument(String group) {

    }

    public void setType(ArgType type) {
        this.type = type;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public ArgType getType() {
        return type;
    }

    public Object[] getParams() {
        return params;
    }

    public boolean fullyParsed() {
        for (Object param : params) {
            if(param == null) return false;
        }

        return true;
    }

    public static ArgType getDuplicateArg(Argument[] args) {
        ArrayList<ArgType> argTypes = new ArrayList<>();

        for (Argument arg : args) {
            if(argTypes.contains(arg.getType())) return arg.getType();

            argTypes.add(arg.getType());
        }

        return null;
    }

    public static boolean containsDuplicates(Argument[] args){
        return getDuplicateArg(args) != null;
    }

    public void setMalformed(ArrayList<String> malformed) {
        this.malformed = malformed;
    }

    public ArrayList<String> getMalformed() {
        return malformed;
    }

    public static String malformedToString(HashMap<ArgType, ArrayList<String>> malformedArgs) {
        final String[] str = {""};

        malformedArgs.forEach((key,value) -> {
            str[0] += "**" + key + "?**\n";
            for (String s : value) {
                str[0] += "  " + s +"\n";
            }

            str[0] += "\n";
        });

        return str[0];
    }
}


