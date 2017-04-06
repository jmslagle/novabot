package parser;

import core.Pokemon;
import core.Region;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Paris on 4/04/2017.
 */
public class UserCommand {
    private Argument[] args;
    private ArrayList<InputError> exceptions = new ArrayList<>();

    private ArrayList<String> malformedArgs = new ArrayList<>();

    public ArrayList<InputError> getExceptions() {
        return exceptions;
    }

    public void setArgs(Argument[] args) {
        this.args = args;
    }


    public Pokemon[] buildPokemon(){

        Region[] regions = new Region[] {Region.All};
        float miniv = 0;
        float maxiv = 100;

        String[] pokeNames = new String[0];

        for (Argument arg : args) {
            switch(arg.getType()){
                case Regions:
                    regions = toRegions(arg.getParams());
                    break;
                case Pokemon:
                    pokeNames = toStrings(arg.getParams());
                    break;
                case Iv:
                    miniv = (Float) arg.getParams()[0];

                    if(arg.getParams().length == 2){
                        maxiv = (Float) arg.getParams()[1];
                    }
                    break;
            }
        }

        ArrayList<Pokemon> pokemons = new ArrayList<>();


        for (String pokeName : pokeNames) {
            for (Region region : regions) {
                pokemons.add(new Pokemon(pokeName, region, miniv, maxiv));
            }
        }

        Pokemon[] pokeArray = new Pokemon[pokemons.size()];

        return pokemons.toArray(pokeArray);

    }

    private Region[] toRegions(Object[] params) {
        Region[] regions = new Region[params.length];

        for (int i = 0; i < params.length; i++) {
            regions[i] = (Region) params[i];
        }

        return regions;
    }

    private String[] toStrings(Object[] params) {
        String[] strings = new String[params.length];

        for (int i = 0; i < params.length; i++) {
            strings[i] = params[i].toString();
        }

        return strings;
    }

    public Region[] getRegions(){
        return null;
    }

    public Argument getArg(int i) {
        return args[i];
    }

    public void addException(InputError e) {
        exceptions.add(e);
    }


    public Argument[] getArgs() {
        return args;
    }

    public HashMap<ArgType,ArrayList<String>> getMalformedArgs() {

        HashMap<ArgType,ArrayList<String>> malformed = new HashMap<>();

        for (Argument arg : args) {
            if(!arg.fullyParsed()){
                if(!malformed.containsKey(arg.getType())){
                    ArrayList<String> newList = new ArrayList<>();
                    malformed.put(arg.getType(),newList);
                }
            }else{
                continue;
            }

            for (String s : arg.getMalformed()) {
                malformed.get(arg.getType()).add(s);
            }
        }

        return malformed;

    }
}
