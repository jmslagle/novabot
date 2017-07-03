package core;

import raids.Raid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserPref
{
    private HashMap<String, Set<Pokemon>> pokemonPrefs = new HashMap<>();
    private HashMap<String, Set<Raid>> raidPrefs = new HashMap<>();
    private boolean supporter;

    public UserPref(boolean supporter){
        this.supporter = supporter;
    }

    public UserPref() {
    }

    public void addPokemon(final Pokemon pokemon) {
        Location location = pokemon.getLocation();

        if (!this.pokemonPrefs.containsKey(location.toWords())) {
            final Set<Pokemon> set = new HashSet<Pokemon>();
            set.add(pokemon);
            this.pokemonPrefs.put(location.toWords(), set);
        }
        else {
            this.pokemonPrefs.get(location.toWords()).add(pokemon);
        }
    }

    public void addRaid(final Raid raid) {
        Location location = raid.location;

        if (!this.raidPrefs.containsKey(location.toWords())) {
            final Set<Raid> set = new HashSet<>();
            set.add(raid);
            this.raidPrefs.put(location.toWords(), set);
        }
        else {
            this.raidPrefs.get(location.toWords()).add(raid);
        }
    }

    public String allRaidsToString(){
        String str = "";
        for (String locname : this.raidPrefs.keySet()) {
            Location location = Location.fromString(locname, supporter);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str = str + "**" + locname + "**:\n";
            for (final Raid raid : this.raidPrefs.get(locStr)) {
                str += String.format("    %s%n", Pokemon.idToName(raid.bossId));
            }
            str += "\n";
        }
        return str;
    }

    public String allPokemonToString() {
        String str = "";
        for (String locname : this.pokemonPrefs.keySet()) {
            Location location = Location.fromString(locname, supporter);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str = str + "**" + locname + "**:\n";
            for (final Pokemon pokemon : this.pokemonPrefs.get(locStr)) {
                str += String.format("    %s%n", pokePrefString(pokemon));
            }
            str += "\n";
        }
        return str;
    }

    private String pokePrefString(Pokemon pokemon) {
        String str = pokemon.name;
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

    public String allSettingsToString() {
        HashMap<String,Set<String>> prefMap = new HashMap<>();

        raidPrefs.forEach((location , raids) ->{
            if (!prefMap.containsKey(location)) {
                final Set<String> set = new HashSet<>();

                for (Raid raid : raids) {
                    set.add(String.format("%s raids", Pokemon.idToName(raid.bossId)));
                }
                prefMap.put(location, set);
            }
            else {
                for (Raid raid : raids) {
                    prefMap.get(location).add(String.format("%s raids",Pokemon.idToName(raid.bossId)));
                }
            }
        });

        pokemonPrefs.forEach((location , pokemons) ->{
            if (!prefMap.containsKey(location)) {
                final Set<String> set = new HashSet<>();

                for (Pokemon pokemon : pokemons) {
                    set.add(pokePrefString(pokemon));
                }
                prefMap.put(location, set);
            }
            else {
                for (Pokemon pokemon : pokemons) {
                    prefMap.get(location).add(pokePrefString(pokemon));
                }
            }
        });

        String str = "";
        for (String locname : prefMap.keySet()) {
            Location location = Location.fromString(locname, supporter);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str = str + "**" + locname + "**:\n";
            for (final String string : prefMap.get(locStr)) {
                str += String.format("    %s%n", string);
            }
            str += "\n";
        }
        return str;
    }
}
