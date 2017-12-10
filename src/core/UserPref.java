package core;

import pokemon.Pokemon;
import raids.Raid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserPref
{
    private final HashMap<String, Set<Pokemon>> pokemonPrefs = new HashMap<>();
    private final HashMap<String, Set<Raid>> raidPrefs = new HashMap<>();
    private final HashMap<String, Set<String>> presetPrefs = new HashMap<>();

    public UserPref() {
    }

    public void addPreset(String presetName, Location location){
        if(!this.presetPrefs.containsKey(location.toWords())){
            Set<String> set = new HashSet<>();
            set.add(presetName);
            this.presetPrefs.put(location.toWords(),set);
        }else{
            presetPrefs.get(location.toWords()).add(presetName);
        }
    }

    public void addPokemon(final Pokemon pokemon) {
        Location location = pokemon.getLocation();

        if (!this.pokemonPrefs.containsKey(location.toWords())) {
            final Set<Pokemon> set = new HashSet<>();
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

    public static void main(String[] args) {
        MessageListener.loadConfig();
        MessageListener.loadSuburbs();
        System.out.println(Location.fromDbString("inner-north"));
    }

    public String allPresetsToString(){
        StringBuilder str = new StringBuilder();
        for (String locname : presetPrefs.keySet()) {
            Location location = Location.fromString(locname);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str.append("**").append(locname).append("**:\n");
            for (String preset : presetPrefs.get(locStr)) {
                str.append(String.format("    %s%n", preset));
            }
            str.append("\n");
        }
        return str.toString();
    }

    public String allRaidsToString(){
        StringBuilder str = new StringBuilder();
        for (String locname : this.raidPrefs.keySet()) {
            Location location = Location.fromString(locname);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str.append("**").append(locname).append("**:\n");
            for (final Raid raid : this.raidPrefs.get(locStr)) {
                str.append(String.format("    %s%n", Pokemon.idToName(raid.bossId)));
            }
            str.append("\n");
        }
        return str.toString();
    }

    public String allPokemonToString() {
        StringBuilder str = new StringBuilder();
        for (String locname : this.pokemonPrefs.keySet()) {
            Location location = Location.fromString(locname);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str.append("**").append(locname).append("**:\n");
            for (final Pokemon pokemon : this.pokemonPrefs.get(locStr)) {
                str.append(String.format("    %s%n", pokePrefString(pokemon)));
            }
            str.append("\n");
        }
        return str.toString();
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

    private String presetString(String preset) {
        return preset + " preset";
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

        presetPrefs.forEach((location,presets) -> {
            if (!prefMap.containsKey(location)) {
                Set<String> set = new HashSet<>();

                for (String preset : presets) {
                    set.add(presetString(preset));
                }
                prefMap.put(location,set);
            }else{
                for (String preset : presets) {
                    prefMap.get(location).add(presetString(preset));
                }
            }
        });

        StringBuilder str = new StringBuilder();
        for (String locname : prefMap.keySet()) {
            Location location = Location.fromString(locname);

            String locStr = locname;
            if (location != null) {
                locStr = location.toWords();
            }
            str.append("**").append(locname).append("**:\n");
            for (final String string : prefMap.get(locStr)) {
                str.append(String.format("    %s%n", string));
            }
            str.append("\n");
        }
        return str.toString();
    }


    public boolean isRaidEmpty() {
        boolean[] empty = { true };
        raidPrefs.forEach((loc, obj) -> {
            if (obj.size() > 0) {
                empty[0] = false;
            }
        });
        return empty[0];
    }

    public boolean isPokeEmpty() {
        boolean[] empty = { true };
        pokemonPrefs.forEach((loc, obj) -> {
            if (obj.size() > 0) {
                empty[0] = false;
            }
        });
        return empty[0];
    }

    public boolean isPresetEmpty() {
        boolean[] empty = { true };
        presetPrefs.forEach((loc, obj) -> {
            if (obj.size() > 0) {
                empty[0] = false;
            }
        });
        return empty[0];
    }

}
