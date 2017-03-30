import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paris on 18/03/2017.
 */
public class UserPref {

    String userID;

    HashMap<Region,Set<Pokemon>> pokemonPrefs = new HashMap<>();

    public UserPref (String userID){
        this.userID = userID;
    }

    public void addPokemon(Pokemon pokemon, Region[] regions){
        for (Region region : regions) {
            if(!pokemonPrefs.containsKey(region)){
                Set<Pokemon> set = new HashSet<>();
                set.add(pokemon);

                pokemonPrefs.put(region, set);
            }else {
                pokemonPrefs.get(region).add(pokemon);
            }
        }
    }

    public void addPokemons(Pokemon[] pokemons, Region[] regions){
        for (Pokemon pokemon : pokemons) {
            addPokemon(pokemon,regions);
        }
    }

    public void removePokemon(Pokemon pokemon, Region[] regions){
        for (Region region : regions){
            pokemonPrefs.get(region).remove(pokemon);
        }
    }

    public void removePokemons(Pokemon[] pokemons, Region[] regions){
        for (Pokemon pokemon : pokemons) {
            removePokemon(pokemon, regions);
        }
    }

    public boolean hasRegion(Region region) {
        return pokemonPrefs.containsKey(region);
    }

    public boolean contains(Pokemon[] pokemons, Region[] regions) {
        int failedRegions = 0;

        for (Region r1 : regions) {
            for (Region r2 : pokemonPrefs.keySet()) {
                if(r1 == r2){
                    Set<Pokemon> set = pokemonPrefs.get(r2);
                    for (Pokemon pokemon : pokemons) {
                        if(!set.contains(pokemon)){
                            failedRegions++;
                            break;
                        }
                    }
                }
            }
        }

        return failedRegions != pokemonPrefs.keySet().size();

    }

    public String allPokemonToString(){
        String str = "";

        for (Region region : pokemonPrefs.keySet()) {
            str += "**" + region.toWords() + "**:\n";
            for (Pokemon pokemon : pokemonPrefs.get(region)) {
                str += "    " + pokemon.name;

                if(pokemon.miniv > 0 || pokemon.maxiv < 100){
                    if(pokemon.maxiv < 100){
                        if(pokemon.miniv == pokemon.maxiv){
                            str += " " + pokemon.miniv;
                        }else{
                            str += " " + pokemon.miniv + "-" + pokemon.miniv;
                        }

                        str += "%";
                    }else{
                        str += " " + pokemon.miniv + (pokemon.miniv == 100 ? "%" : "%+");
                    }
                }

                str+="\n";
            }
            str+="\n";
        }

        return str;
    }

    public boolean isEmpty() {
        final boolean[] empty = {true};

        pokemonPrefs.forEach((region,pokemons) -> {
            if(pokemons.size() > 0) {
                empty[0] = false;
            }
        });

        return empty[0];
    }
}
