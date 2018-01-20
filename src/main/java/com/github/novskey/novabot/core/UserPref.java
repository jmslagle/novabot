package com.github.novskey.novabot.core;

import com.github.novskey.novabot.data.Preset;
import com.github.novskey.novabot.pokemon.Pokemon;
import com.github.novskey.novabot.raids.Raid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserPref {
    private final HashMap<String, Set<Pokemon>> pokemonPrefs = new HashMap<>();
    private final HashMap<String, Set<Raid>> raidPrefs = new HashMap<>();
    private final HashMap<String, Set<String>> presetPrefs = new HashMap<>();
    private final NovaBot novaBot;

    public UserPref(NovaBot novaBot) {
        this.novaBot = novaBot;
    }

    public void addPokemon(final Pokemon pokemon) {
        Location location = pokemon.getLocation();

        if (location == null) return;


        if (!this.pokemonPrefs.containsKey(location.toWords())) {
            final Set<Pokemon> set = new HashSet<>();
            set.add(pokemon);
            this.pokemonPrefs.put(location.toWords(), set);
        } else {
            this.pokemonPrefs.get(location.toWords()).add(pokemon);
        }
    }

    public void addPreset(String presetName, Location location) {

        if (location == null) return;

        if (!this.presetPrefs.containsKey(location.toWords())) {
            Set<String> set = new HashSet<>();
            set.add(presetName);
            this.presetPrefs.put(location.toWords(), set);
        } else {
            presetPrefs.get(location.toWords()).add(presetName);
        }
    }

    public void addRaid(final Raid raid) {
        Location location = raid.location;

        if (location == null) return;

        if (!this.raidPrefs.containsKey(location.toWords())) {
            final Set<Raid> set = new HashSet<>();
            set.add(raid);
            this.raidPrefs.put(location.toWords(), set);
        } else {
            this.raidPrefs.get(location.toWords()).add(raid);
        }
    }

    public String allPokemonToString() {
        StringBuilder str = new StringBuilder();
        for (String locname : this.pokemonPrefs.keySet()) {
            Location location = Location.fromString(locname, novaBot);

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

    public String allPresetsToString() {
        StringBuilder str = new StringBuilder();
        for (String locname : presetPrefs.keySet()) {
            Location location = Location.fromString(locname, novaBot);

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

    public String allRaidsToString() {
        StringBuilder str = new StringBuilder();
        for (String locname : this.raidPrefs.keySet()) {
            Location location = Location.fromString(locname, novaBot);

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

    public String allSettingsToString() {
        HashMap<String, Set<String>> prefMap = new HashMap<>();

        raidPrefs.forEach((location, raids) -> {
            if (!prefMap.containsKey(location)) {
                final Set<String> set = new HashSet<>();

                for (Raid raid : raids) {
                    set.add(String.format("%s raids", Pokemon.idToName(raid.bossId)));
                }
                prefMap.put(location, set);
            } else {
                for (Raid raid : raids) {
                    prefMap.get(location).add(String.format("%s raids", Pokemon.idToName(raid.bossId)));
                }
            }
        });

        pokemonPrefs.forEach((location, pokemons) -> {
            if (!prefMap.containsKey(location)) {
                final Set<String> set = new HashSet<>();

                for (Pokemon pokemon : pokemons) {
                    set.add(pokePrefString(pokemon));
                }
                prefMap.put(location, set);
            } else {
                for (Pokemon pokemon : pokemons) {
                    prefMap.get(location).add(pokePrefString(pokemon));
                }
            }
        });

        presetPrefs.forEach((location, presets) -> {
            if (!prefMap.containsKey(location)) {
                Set<String> set = new HashSet<>();

                for (String preset : presets) {
                    set.add(presetString(preset));
                }
                prefMap.put(location, set);
            } else {
                for (String preset : presets) {
                    prefMap.get(location).add(presetString(preset));
                }
            }
        });

        StringBuilder str = new StringBuilder();
        for (String locname : prefMap.keySet()) {
            Location location = Location.fromString(locname, novaBot);

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

    public boolean isPokeEmpty() {
        boolean[] empty = {true};
        pokemonPrefs.forEach((loc, obj) -> {
            if (obj.size() > 0) {
                empty[0] = false;
            }
        });
        return empty[0];
    }

    public boolean isPresetEmpty() {
        boolean[] empty = {true};
        presetPrefs.forEach((loc, obj) -> {
            if (obj.size() > 0) {
                empty[0] = false;
            }
        });
        return empty[0];
    }

    public boolean isRaidEmpty() {
        boolean[] empty = {true};
        raidPrefs.forEach((loc, obj) -> {
            if (obj.size() > 0) {
                empty[0] = false;
            }
        });
        return empty[0];
    }

    private String pokePrefString(Pokemon pokemon) {
        String str = pokemon.name;
        boolean iv = false, lvl = false;
        if (pokemon.miniv > 0.0f || pokemon.maxiv < 100.0f) {
            iv = true;
            if (pokemon.maxiv < 100.0f) {
                if (pokemon.miniv == pokemon.maxiv) {
                    str = str + " " + pokemon.miniv + "%";
                } else {
                    str = str + " " + pokemon.miniv + "-" + pokemon.maxiv + "%";
                }
            } else {
                str = str + " " + pokemon.miniv + ((pokemon.miniv == pokemon.maxiv) ? "%" : "%+");
            }
        }
        if (pokemon.minlvl > 0 || pokemon.maxlvl < 40) {
            lvl = true;
            if (iv){
                str += ",";
            }
            if (pokemon.maxlvl < 40) {
                if (pokemon.minlvl == pokemon.maxlvl) {
                    str = str + " level " + pokemon.minlvl + "";
                } else {
                    str = str + " level " + pokemon.minlvl + "-" + pokemon.maxlvl;
                }
            } else {
                str = str + " level " + pokemon.minlvl + ((pokemon.minlvl == pokemon.maxlvl) ? "" : "+");
            }
        }
        if (pokemon.mincp > 0 || pokemon.maxcp < Integer.MAX_VALUE) {
            if (iv || lvl){
                str += ",";
            }
            if (pokemon.maxcp < Integer.MAX_VALUE) {
                if (pokemon.mincp == pokemon.maxcp) {
                    str = str + " " + pokemon.mincp + "CP" + "";
                } else {
                    str = str + " " + pokemon.mincp + "-" + pokemon.maxcp + "CP";
                }
            } else {
                str = str + " " + pokemon.mincp + ((pokemon.mincp == pokemon.maxcp) ? "CP" : "CP+");
            }
        }
        return str;
    }

    private String presetString(String preset) {
        return preset + " preset";
    }

    public void addPreset(Preset preset) {
        addPreset(preset.presetName, preset.location);
    }
}
