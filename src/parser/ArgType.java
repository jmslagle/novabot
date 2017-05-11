package parser;

import java.util.*;

public enum ArgType
{
    Iv,
    Locations,
    Pokemon,
    CommandStr,
    Unknown,
    Status;

    public static void main(final String[] args) {
        final HashSet<ArgType> hashSet = new HashSet<ArgType>(Arrays.asList(ArgType.CommandStr, ArgType.Pokemon, ArgType.Locations));
        System.out.println(setToString(hashSet));
    }

    @Override
    public String toString() {
        switch (this) {
            case Iv:
                return "iv";
            case Locations:
                return "channel or location";
            case Pokemon:
                return "pokemon";
            case Unknown:
                return "other";
            default:
                return null;
        }
    }

    public static String setToString(final HashSet<ArgType> argTypes) {
        String str = "";
        argTypes.remove(ArgType.CommandStr);
        final ArgType[] argsArray = new ArgType[argTypes.size()];
        argTypes.toArray(argsArray);
        if (argTypes.size() == 1) {
            return argsArray[0].toString();
        }
        for (int i = 0; i < argsArray.length; ++i) {
            if (i == argsArray.length - 1) {
                str = str + "and " + argsArray[i].toString();
            }
            else {
                str += ((i == argsArray.length - 2) ? (argsArray[i].toString() + " ") : (argsArray[i].toString() + ", "));
            }
        }
        return str;
    }
}
