package core;

import java.util.*;

public class UserPref
{
    private final HashMap<String, Set<Pokemon>> pokemonPrefs;

    public UserPref(final String userID) {
        this.pokemonPrefs = new HashMap<String, Set<Pokemon>>();
    }

    public void addPokemon(final Pokemon pokemon, final Location[] locations) {
        for (final Location location : locations) {
            if (!this.pokemonPrefs.containsKey(location.toWords())) {
                final Set<Pokemon> set = new HashSet<Pokemon>();
                set.add(pokemon);
                this.pokemonPrefs.put(location.toWords(), set);
            }
            else {
                this.pokemonPrefs.get(location.toWords()).add(pokemon);
            }
        }
    }

    public String allPokemonToString() {
        String str = "";
        for (String locname : this.pokemonPrefs.keySet()) {
            final String location = locname;
            if (Region.fromString(location) != null) {
                locname = Region.fromString(location).toWords();
            }
            str = str + "**" + locname + "**:\n";
            for (final Pokemon pokemon : this.pokemonPrefs.get(location)) {
                str = str + "    " + pokemon.name;
                if (pokemon.miniv > 0.0f || pokemon.maxiv < 100.0f) {
                    if (pokemon.maxiv < 100.0f) {
                        if (pokemon.miniv == pokemon.maxiv) {
                            str = str + " " + pokemon.miniv;
                        }
                        else {
                            str = str + " " + pokemon.miniv + "-" + pokemon.maxiv;
                        }
                        str += "%";
                    }
                    else {
                        str = str + " " + pokemon.miniv + ((pokemon.miniv == 100.0f) ? "%" : "%+");
                    }
                }
                str += "\n";
            }
            str += "\n";
        }
        return str;
    }

    public boolean isEmpty() {
        final boolean[] empty = { true };
        this.pokemonPrefs.forEach((region, pokemons) -> {
            if (pokemons.size() > 0) {
                empty[0] = false;
            }
            return;
        });
        return empty[0];
    }
}
