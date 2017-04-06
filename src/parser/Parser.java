package parser;

import core.Pokemon;
import core.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Paris on 3/04/2017.
 */
public class Parser {


    static final Pattern PATTERN = Pattern.compile("!?\\w+|<(.*?)>");

//    public static void main(String[] args) {
//
//        UserCommand command = parseInput("!addpokemon dog");
//
//        if(command.getExceptions().size() > 0){
//            System.out.println("Invalid command");
//            command.getExceptions().forEach(e -> System.out.println(e.getMessage()));
//        }
//    }

    public static Pokemon[] parseArgs(String[] args){
        Region[] regions;
        String[] pokeNames;
        float miniv,maxiv;

        for (String arg : args) {
//            if(arg.startsWith())
        }
        return null;
    }

    public static UserCommand parseInput(String input){
        UserCommand command = new UserCommand();

        Argument[] args = getArgs(input);

        String firstArg = (String) args[0].getParams()[0];

        if(!Commands.isCommandWithArgs(firstArg)){
            command.addException(InputError.InvalidCommand);
            return command;
        }

        Command cmd = Commands.get(firstArg);

        if(args.length -1 < cmd.minArgs){
            command.addException(InputError.NotEnoughArgs);
        }else if(args.length-1 > cmd.maxArgs){
            command.addException(InputError.TooManyArgs);
        }

        for (Argument arg : args) {
            if(!arg.fullyParsed()){
                command.addException(InputError.MalformedArg);
            }

            if(!cmd.validArgTypes.contains(arg.getType())){
                command.addException(InputError.InvalidArg);
            }
        }

        if(!cmd.meetsRequirements(args)){
            command.addException(InputError.MissingRequiredArg);
        }

        if(!cmd.allowDuplicateArgs && Argument.containsDuplicates(args)){
            command.addException(InputError.DuplicateArgs);
        }

        command.setArgs(args);

        return command;
    }

    private static Argument[] getArgs(String input) {
        ArrayList<Argument> args = new ArrayList<>();

        Matcher matcher = PATTERN.matcher(input);

        while (matcher.find()) {
            String group = matcher.group();
            if(group.charAt(0) == '<'){
                args.add(parseList(group));
            }else {
                args.add(getArg(group));
            }
        }

        Argument[] arguments = new Argument[args.size()];
        return args.toArray(arguments);
    }

    private static Argument getArg(String s) {
        Argument argument = new Argument();

        if(Commands.isCommandWithArgs(s.trim())) {
            argument.setType(ArgType.CommandStr);
            argument.setParams(new Object[] {s});
        }else if (Pokemon.VALID_NAMES.contains(s.trim())) {
            argument.setType(ArgType.Pokemon);
            argument.setParams(new Object[] {s});
        }else if (Region.fromString(s.trim()) != null){
            argument.setType(ArgType.Regions);
            argument.setParams(new Object[] {Region.fromString(s.trim())});
        }else if (getFloat(s.trim()) != null){
            argument.setType(ArgType.Iv);
            argument.setParams(new Object[] {getFloat(s.trim())});
        }else{
            argument.setType(ArgType.Unkown);
            argument.setParams(new Object[] {null});
            argument.setMalformed(new ArrayList<>(Arrays.asList(new String[] {s})));
        }

        return argument;
    }

    private static Float getFloat(String s) {

        try {
            Float f = Float.parseFloat(s.trim());

            return f;
        }catch (NumberFormatException e){
            return null;
        }
    }

    private static Argument parseList(String group) {
        Argument argument = new Argument();

        String toSplit = group.substring(1,group.length()-1);

        String[] strings = toSplit.split(",");

        Object[] args = new Object[strings.length];

        System.arraycopy(strings,0,args,0,strings.length);

        ArrayList<String> malformed = new ArrayList<>();

        for (int i = 0; i < strings.length; i++) {
            args[i] = Region.fromString(strings[i].trim());
            if(args[i] == null) malformed.add(strings[i]);
        }


        if(allNull(args)) {
            malformed.clear();

            for (int i = 0; i < strings.length; i++) {
                Pokemon pokemon = new Pokemon(strings[i]);

                if(pokemon.name == null) malformed.add(strings[i]);

                args[i] = pokemon.name;
            }

            if(!allNull(args)){
                  argument.setType(ArgType.Pokemon);
            }else{
                malformed.clear();
                if(args.length == 1 || args.length == 2){
                    for (int i = 0; i < strings.length; i++) {
                        args[i] = getFloat(strings[i].trim());
                    }

                    if(!allNull(args)){
                        argument.setType(ArgType.Iv);
                    }
                }
            }
        }else {
            argument.setType(ArgType.Regions);
        }

        if(argument.getType() != null){
            argument.setParams(args);
            argument.setMalformed(malformed);
        }
        return argument;
    }

    private static boolean allNull(Object[] args) {
        boolean allNull = true;

        for (Object arg : args) {
            if(arg != null) return false;
        }

        return allNull;
    }

}
