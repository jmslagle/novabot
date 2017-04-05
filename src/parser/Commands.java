package parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static parser.ArgType.*;


/**
 * Created by Paris on 4/04/2017.
 */
public class Commands {

    public static HashMap<String,Command> commands = new HashMap<>();

    static {
        Command addPokemon = new Command();
        addPokemon.setValidArgTypes(new HashSet<>(Arrays.asList(new ArgType[] {CommandStr,Pokemon,Regions,Iv})));
        addPokemon.setRequiredArgTypes(new HashSet<>(Arrays.asList(new ArgType[] {Pokemon})));
        addPokemon.setArgRange(1,3);

        Command delPokemon = new Command();
        delPokemon.setValidArgTypes(addPokemon.getValidArgTypes());
        addPokemon.setRequiredArgTypes(addPokemon.getRequiredArgTypes());
        delPokemon.setArgRange(1,3);

        Command clearPokemon = new Command();
        clearPokemon.setValidArgTypes(new HashSet<>(Arrays.asList(new ArgType[] {CommandStr,Pokemon})));
        clearPokemon.setRequiredArgTypes(clearPokemon.getValidArgTypes());
        clearPokemon.setArgRange(1,1);

        Command clearChannel = new Command();
        clearChannel.setValidArgTypes(new HashSet<>(Arrays.asList(new ArgType[] {CommandStr,Regions})));
        clearChannel.setRequiredArgTypes(clearChannel.getValidArgTypes());
        clearChannel.setArgRange(1,1);


        commands.put("!addpokemon",addPokemon);
        commands.put("!delpokemon",delPokemon);
//        commands.add("!help");
        commands.put("!clearpokemon",clearPokemon);
        commands.put("!clearchannel",clearChannel);
//        commands.add("!reset");
//        commands.add("!settings");
//        commands.add("!help");
//        commands.add("!channellist");
//        commands.add("!channels");
    }


    public static boolean isCommandWithArgs(String s) {
        return commands.get(s) != null;
    }

    public static Command get(String firstArg) {
        return commands.get(firstArg);
    }
}
