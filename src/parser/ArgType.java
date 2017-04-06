package parser;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Paris on 3/04/2017.
 */
public enum ArgType {
    Iv,
    Regions,
    Pokemon,
    CommandStr, Unkown;

    public static void main(String[] args) {
        HashSet<ArgType> hashSet = new HashSet<>(Arrays.asList(new ArgType[] {CommandStr,Pokemon,Regions}));

        System.out.println(setToString(hashSet));
    }

    @Override
    public String toString() {

        switch(this){
            case Iv:
                return "iv";
            case Regions:
                return "channel";
            case Pokemon:
                return "pokemon";
            case Unkown:
                return "other";
        }

        return null;
    }

    public static String setToString(HashSet<ArgType> argTypes){
        String str = "";

        argTypes.remove(CommandStr);

        ArgType[] argsArray = new ArgType[argTypes.size()];

        argTypes.toArray(argsArray);

        if(argTypes.size() == 1){
            return argsArray[0].toString();
        }

        for (int i = 0; i < argsArray.length; i++) {
            if(i == argsArray.length-1)
                str += "and " + argsArray[i].toString();
            else
                str += i == argsArray.length - 2 ? argsArray[i].toString() + " " : argsArray[i].toString() +", ";
        }

        return str;
    }
}
