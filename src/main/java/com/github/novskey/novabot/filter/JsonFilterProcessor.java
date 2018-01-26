package com.github.novskey.novabot.filter;

import com.github.novskey.novabot.Util.UtilityFunctions;
import com.github.novskey.novabot.notifier.PokeNotificationSender;
import com.github.novskey.novabot.notifier.RaidNotificationSender;
import com.github.novskey.novabot.pokemon.PokeSpawn;
import com.github.novskey.novabot.pokemon.Pokemon;
import com.github.novskey.novabot.raids.RaidSpawn;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

@Slf4j
@NoArgsConstructor
@EqualsAndHashCode
public class JsonFilterProcessor implements FilterProcessor {

    private HashMap<String, JsonObject> pokeFilters = new HashMap<>();
    private HashMap<String, JsonObject> raidFilters = new HashMap<>();

    @Override
    public boolean matchesFilter(String filterName, RaidSpawn raidSpawn) {

        JsonObject filter = raidFilters.get(filterName);
        RaidNotificationSender.notificationLog.info("Filter: " + filter);
        String searchStr = raidSpawn.gymId;

        JsonElement raidFilter = searchFilter(filter,searchStr);
        RaidNotificationSender.notificationLog.info(searchStr + ": " + raidFilter);

        if (raidFilter == null) {
            RaidNotificationSender.notificationLog.info(String.format("couldn't find filter for '%s'",searchStr));
            searchStr = raidSpawn.getProperties().get("gym_name");
            raidFilter = searchFilter(filter, searchStr);
            RaidNotificationSender.notificationLog.info(searchStr + ": " + raidFilter);

            if (raidFilter == null) {
                RaidNotificationSender.notificationLog.info(String.format("couldn't find filter for '%s'", searchStr));

                searchStr = "Default";
                raidFilter = searchFilter(filter, searchStr);
                RaidNotificationSender.notificationLog.info(searchStr + ": " + raidFilter);

                if (raidFilter == null){
                    RaidNotificationSender.notificationLog.info("no default block in filter, moving on");
                    return false;
                }
            }
        }

        if (raidFilter.isJsonObject()) {
            searchStr = (raidSpawn.bossId >= 1) ? Pokemon.getFilterName(raidSpawn.bossId) : "Egg" + raidSpawn.raidLevel;

            JsonElement subFilter = searchFilter(raidFilter.getAsJsonObject(),searchStr);
            RaidNotificationSender.notificationLog.info(searchStr + ": " + subFilter);

            if (subFilter != null){
                if (subFilter.getAsBoolean()) {
                    RaidNotificationSender.notificationLog.info(String.format("Raid enabled in filter block '%s', posting to discord", searchStr));
                    return true;
                }else {
                    RaidNotificationSender.notificationLog.info(String.format("Raid not enabled in filter block '%s', ignoring spawn", searchStr));
                    return false;
                }
            } else {
                subFilter = searchFilter(raidFilter.getAsJsonObject(),"Level"+raidSpawn.raidLevel);
                RaidNotificationSender.notificationLog.info(searchStr + ": " + subFilter);

                if(subFilter != null && subFilter.getAsBoolean()){
                    RaidNotificationSender.notificationLog.info(String.format("Raid enabled in filter block '%s', posting to discord", "Level"+ raidSpawn.raidLevel));
                    return true;
                }else {
                    RaidNotificationSender.notificationLog.info(String.format("Raid not enabled in filter block '%s', ignoring spawn", "Level"+ raidSpawn.raidLevel));
                    return false;
                }
            }
        } else {
            if (raidFilter.getAsBoolean()) {
                RaidNotificationSender.notificationLog.info(String.format("Raid enabled in filter block '%s', posting to discord", searchStr));
                return true;
            }else{
                RaidNotificationSender.notificationLog.info(String.format("Raid not enabled in filter block '%s', ignoring spawn", searchStr));
                return false;
            }
        }
    }

    @Override
    public boolean matchesFilter(String filterName, PokeSpawn pokeSpawn) {
        JsonObject filter = pokeFilters.get(filterName);

        if (filter == null) {
            return false;
        }

        JsonElement pokeFilter = searchFilter(filter, UtilityFunctions.capitaliseFirst(Pokemon.getFilterName(pokeSpawn.getFilterId())));
        if (pokeFilter == null) {
            PokeNotificationSender.notificationLog.info(String.format("pokeFilter %s is null for %s", filterName, pokeSpawn.getProperties().get("pkmn")));
            pokeFilter = searchFilter(filter, "Default");

            if (pokeFilter == null) {
                return false;
            }
        }

        if (pokeFilter.isJsonArray()) {
            JsonArray array = pokeFilter.getAsJsonArray();
            for (JsonElement element : array) {
                if (processElement(element, pokeSpawn,filterName)) return true;
            }
        }
        return processElement(pokeFilter,pokeSpawn,filterName);
    }


    @Override
    public boolean loadFilter(String filterName, Type type) {
        JsonObject filter = null;
        JsonParser parser = new JsonParser();

        HashMap<String, JsonObject> filterMap;
        switch (type) {
            case POKEMON:
                filterMap = pokeFilters;
                break;
            case RAID:
                filterMap = raidFilters;
                break;
            default:
                throw new RuntimeException("Invalid Filter Type " + type.toString());
        }

        if (filterMap.containsKey(filterName)) {
            return false;
        }
        try {
            JsonElement element = parser.parse(new FileReader(filterName));

            if (element.isJsonObject()) {
                filter = element.getAsJsonObject();
            }

            if (filterMap.put(filterName, filter) == null) {
                log.info(String.format("Loaded filter %s", filterName));
            }
        } catch (FileNotFoundException e) {
            log.warn(String.format("Couldn't find filter file %s, aborting.",filterName));
            System.exit(0);
        }
        return true;
    }

    private boolean processElement(JsonElement pokeFilter, PokeSpawn pokeSpawn, String filterName) {
        if (pokeFilter.isJsonObject()) {
            JsonObject obj = pokeFilter.getAsJsonObject();

            JsonElement maxObj = obj.get("max_iv");
            JsonElement minObj = obj.get("min_iv");

            float max = maxObj == null ? Integer.MAX_VALUE : maxObj.getAsFloat();
            float min = minObj == null ? Integer.MIN_VALUE : minObj.getAsFloat();

            if ((pokeSpawn.iv == null ? -1 : pokeSpawn.iv) <= max && (pokeSpawn.iv == null ? -1 : pokeSpawn.iv) >= min) {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon between specified ivs (%s,%s)", infOrNum(min), infOrNum(max)));
            } else {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon (%s%%) not between specified ivs (%s,%s). filter %s", pokeSpawn.iv, infOrNum(min), infOrNum(max), filterName));
                return false;
            }

            maxObj = obj.get("max_cp");
            minObj = obj.get("min_cp");

            max = maxObj == null ? Integer.MAX_VALUE : maxObj.getAsFloat();
            min = minObj == null ? Integer.MIN_VALUE : minObj.getAsFloat();

            if ((pokeSpawn.cp == null ? -1 : pokeSpawn.cp) <= max && (pokeSpawn.cp == null ? -1 : pokeSpawn.cp) >= min) {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon between specified cp (%s,%s)", infOrNum(min), infOrNum(max)));
            } else {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon (%sCP) not between specified cp (%s,%s)", pokeSpawn.cp, infOrNum(min), infOrNum(max)));
                return false;
            }

            maxObj = obj.get("max_level");
            minObj = obj.get("min_level");

            max = maxObj == null ? Integer.MAX_VALUE : maxObj.getAsInt();
            min = minObj == null ? Integer.MIN_VALUE : minObj.getAsFloat();

            if ((pokeSpawn.level == null ? -1 : pokeSpawn.level) <= max && (pokeSpawn.level == null ? -1 : pokeSpawn.level) >= min) {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon between specified level (%s,%s)", infOrNum(min), infOrNum(max)));
            } else {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon (level %s) not between specified level (%s,%s)", pokeSpawn.level, infOrNum(min), infOrNum(max)));
                return false;
            }

            JsonArray sizes = obj.getAsJsonArray("size");

            if (sizes != null) {
                String  spawnSize = pokeSpawn.getProperties().get("size");
                boolean passed    = false;

                for (JsonElement size : sizes) {
                    if (size.getAsString().equals(spawnSize)) {
                        PokeNotificationSender.notificationLog.info(String.format("Pokemon size %s passed filter", spawnSize));
                        passed = true;
                        break;
                    }
                }

                if (!passed) {
                    PokeNotificationSender.notificationLog.info(String.format("Pokemon size %s did not pass filter", spawnSize));
                    return false;
                }
            }
            return true;
        } else {
            if (pokeFilter.getAsBoolean()) {
                PokeNotificationSender.notificationLog.info("Pokemon enabled in filter, posting to Discord");
                return true;
            } else {
                PokeNotificationSender.notificationLog.info("Pokemon not enabled in filter, not posting");
                return false;
            }
        }
    }

    private String infOrNum(float num) {
        if(num == Integer.MIN_VALUE){
            return "-inf";
        }else if (num == Integer.MAX_VALUE){
            return "inf";
        }else{
            return String.valueOf(num);
        }
    }

    private JsonElement searchFilter(JsonObject filter, String search) {
        if (filter == null || search == null) return null;
        return filter.get(search);
    }


}
