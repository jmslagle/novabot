package core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import maps.GeofenceIdentifier;
import maps.Geofencing;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import notifier.PokeNotificationSender;
import notifier.RaidNotificationSender;
import org.ini4j.Ini;
import pokemon.PokeSpawn;
import pokemon.Pokemon;
import raids.Raid;
import raids.RaidSpawn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

import static raids.Raid.emotes;

/**
 * Created by Owner on 13/05/2017.
 */
public class Config {

    private static final String[] formatKeys = new String[]{"pokemon", "raidEgg", "raidBoss"};
    private static final String[] formattingVars = new String[]{"title", "titleUrl", "body", "content", "showMap", "mapZoom", "mapWidth", "mapHeight"};
    public final HashMap<String, JsonObject> pokeFilters = new HashMap<>();
    public final HashMap<String, JsonObject> raidFilters = new HashMap<>();
    private final HashMap<String, NotificationLimit> roleLimits = new HashMap<>();
    private final HashMap<String, Format> formats = new HashMap<>();
    private final AlertChannels pokeChannels = new AlertChannels();
    private final AlertChannels raidChannels = new AlertChannels();
    public HashMap<String, String> presets = new HashMap<>();
    public ArrayList<Integer> raidBosses = new ArrayList<>(Arrays.asList(2, 5, 8, 11, 28, 31, 34, 38, 62, 65, 68, 71, 73, 76, 82, 91, 94, 105, 123, 129, 131, 137, 139, 143, 144, 145, 146, 150, 243, 244, 245, 248, 249, 302, 303, 359));
    private ArrayList<Integer> blacklist = new ArrayList<>();
    private boolean logging = false;
    private boolean stats = true;
    private boolean startupMessage = false;
    private boolean countLocationsInLimits = true;
    private boolean standardRaidTable = true;
    private boolean useRmDb = true;
    private boolean raidsEnabled = true;
    private boolean pokemonEnabled = true;
    private boolean raidOrganisationEnabled = true;
    private String token = null;
    private ZoneId timeZone = ZoneId.systemDefault();
    private String footerText = null;
    private String googleSuburbField = "city";
    private String adminRole = null;
    private String commandChannelId = null;
    private String novabotRoleId = null;
    private String roleLogId = null;
    private String userUpdatesId = null;
    private String rmUser;
    private String rmPass;
    private String rmIp;
    private String rmPort;
    private String rmDbName;
    private String nbUser;
    private String nbPass;
    private String nbIp;
    private String nbPort;
    private String nbDbName;
    private long pokePollingDelay = 2;
    private long raidPollingDelay = 15;
    private int pokemonThreads = 2;
    private int raidThreads = 2;
    private NotificationLimit nonSupporterLimit = new NotificationLimit(null, null, null);
    private ArrayList<String> GMAPS_KEYS = new ArrayList<>();
    private HashMap<GeofenceIdentifier, String> raidChats = new HashMap<>();
    private NovaBot novaBot;

    public Config(Ini configIni, NovaBot novaBot) {
        novaBot.novabotLog.info("Configuring...");
        this.novaBot = novaBot;

        novaBot.novabotLog.info("Loading config.ini...");
        Ini.Section config = configIni.get("config");

        token = config.get("token", token);

        if (token == null) {
            novaBot.novabotLog.error("Couldn't find token in config.ini. novabot can't run without a bot token.");
            novaBot.shutDown();
            return;
        }

        String blacklistStr = config.get("blacklist", "[]");

        for (String s : Util.parseList(blacklistStr)) {
            blacklist.add(Integer.valueOf(s));
        }

        String raidBossStr = config.get("raidBosses", "[2, 5, 8, 11, 28, 31, 34, 38, 62, 65, 68, 71, 73, 76, 82, 91, 94, 105, 123, 129, 131, 137, 139, 143, 144, 145, 146, 150, 243, 244, 245, 248, 249, 302, 303, 359]");

        for (String s : Util.parseList(raidBossStr)) {
            raidBosses.add(Integer.valueOf(s));
        }

        useRmDb = config.get("useRmDb", Boolean.class, useRmDb);

        standardRaidTable = config.get("standardRaidTable", Boolean.class, standardRaidTable);

        googleSuburbField = config.get("googleSuburbField", googleSuburbField);

        raidsEnabled = config.get("raids", Boolean.class, raidsEnabled);

        raidOrganisationEnabled = config.get("raidOrganisation", Boolean.class, raidOrganisationEnabled);

        pokemonEnabled = config.get("pokemon", Boolean.class, pokemonEnabled);

        pokePollingDelay = config.get("pokePollingDelay", Long.class, pokePollingDelay);

        pokemonThreads = config.get("pokemonThreads", Integer.class, pokemonThreads);

        raidPollingDelay = config.get("raidPollingDelay", Long.class, raidPollingDelay);

        raidThreads = config.get("raidThreads", Integer.class, raidThreads);

        nonSupporterLimit = NotificationLimit.fromString(config.get("nonSupporterLimit", "[n,n,n]"));

        countLocationsInLimits = config.get("countLocationsInLimits", Boolean.class, countLocationsInLimits);


        logging = config.get("logging", Boolean.class, logging);

        if (logging) {
            roleLogId = config.get("roleLogChannel", roleLogId);

            userUpdatesId = config.get("userUpdatesChannel", userUpdatesId);
        }

        timeZone = ZoneId.of(config.get("timezone", String.valueOf(timeZone)));

        footerText = config.get("footerText", footerText);

        stats = config.get("stats", Boolean.class, stats);

        startupMessage = config.get("startupMessage", Boolean.class, startupMessage);

        adminRole = config.get("adminRole", adminRole);

        if (adminRole == null) {
            novaBot.novabotLog.warn("Couldn't find adminRole in config.ini. !reload command won't work unless an adminRole is specified.");
        }

        novabotRoleId = config.get("novabotRole", novabotRoleId);

        if (novabotRoleId == null) {
            novaBot.novabotLog.warn("Couldn't find novabotRoleId in config.ini. A novabotRoleId must be specified in order to use raid organisation.");
            if (!raidOrganisationEnabled) {
                novaBot.novabotLog.error("Raid organisation enabled with no novabotRoleId");
                novaBot.shutDown();
                return;
            }
        }

        commandChannelId = config.get("commandChannel", commandChannelId);

        if (commandChannelId == null) {
            novaBot.novabotLog.warn("Couldn't find commandChannel in config.ini. novabot will only be able to accept commands in DM.");
        }

        Ini.Section rocketmapDb = configIni.get("rocketmap db");
        rmUser = rocketmapDb.get("user", rmUser);
        rmPass = rocketmapDb.get("password", rmPass);
        rmIp = rocketmapDb.get("ip", rmIp);
        rmPort = rocketmapDb.get("port", rmPort);
        rmDbName = rocketmapDb.get("dbName", rmDbName);

        Ini.Section novabotDb = configIni.get("novabot db");
        nbUser = novabotDb.get("user", nbUser);
        nbPass = novabotDb.get("password", nbPass);
        nbIp = novabotDb.get("ip", nbIp);
        nbPort = novabotDb.get("port", nbPort);
        nbDbName = novabotDb.get("dbName", nbDbName);

        novaBot.novabotLog.info("Finished loading config.ini");

        novaBot.novabotLog.info("Loading gkeys.txt...");
        GMAPS_KEYS = loadKeys(new File("gkeys.txt"));
        novaBot.novabotLog.info("Finished loading gkeys.txt");

        novaBot.novabotLog.info("Loading formatting.ini...");
        loadFormatting("formatting.ini");
        novaBot.novabotLog.info("Finished loading formatting.ini");


        if (raidsEnabled()) {
            novaBot.novabotLog.info("Loading raidchannels.ini...");
            loadRaidChannels();
            novaBot.novabotLog.info("Finished loading raidchannels.ini");
        }

        novaBot.novabotLog.info("Loading supporterlevels.txt...");
        loadSupporterRoles();
        novaBot.novabotLog.info("Finished loading supporterlevels.txt");

        if (pokemonEnabled()) {
            novaBot.novabotLog.info("Loading pokechannels.ini...");
            loadPokemonChannels();
            novaBot.novabotLog.info("Finished loading pokechannels.ini");
        }

        novaBot.novabotLog.info("Loading presets.ini...");
        loadPresets();
        novaBot.novabotLog.info("Finished loading presets.ini");

        novaBot.novabotLog.info("Finished configuring");
    }

    public boolean countLocationsInLimits() {
        return countLocationsInLimits;
    }

    public ArrayList<String> findMatchingPresets(RaidSpawn raidSpawn) {
        ArrayList<String> matching = new ArrayList<>();

        for (Map.Entry<String, String> entry : presets.entrySet()) {
            if (matchesFilter(raidFilters.get(entry.getValue()), raidSpawn)) {
                matching.add(entry.getKey());
            }
        }
        return matching;
    }

    public ArrayList<String> findMatchingPresets(PokeSpawn pokeSpawn) {
        ArrayList<String> matching = new ArrayList<>();

        for (Map.Entry<String, String> entry : presets.entrySet()) {
            JsonObject filter = pokeFilters.get(entry.getValue());
            if (filter != null && matchesFilter(filter, pokeSpawn, entry.getValue())) {
                matching.add(entry.getKey());
            }
        }
        return matching;
    }

    public String formatStr(HashMap<String, String> properties, String toFormat) {
        final String[] str = {toFormat};

        properties.forEach((key, value) -> str[0] = str[0].replace(String.format("<%s>", key), value));

        return str[0];
    }

    public String getAdminRole() {
        return adminRole;
    }

    public ArrayList<Integer> getBlacklist() {
        return blacklist;
    }

    public String getBodyFormatting(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "body");
    }

    public String getCommandChannelId() {
        return commandChannelId;
    }

    public String getContentFormatting(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "content");
    }

    public String getEncounterBodyFormatting(String fileName) {
        return formats.get(fileName).getFormatting("pokemon", "encounteredBody");
    }

    public String getEncounterTitleFormatting(String fileName) {
        return formats.get(fileName).getFormatting("pokemon", "encounteredTitle");
    }

    public String getFooterText() {
        return footerText;
    }

    public String getGoogleSuburbField() {
        return googleSuburbField;
    }

    public ArrayList<String> getKeys() {
        return GMAPS_KEYS;
    }

    public String getMapHeight(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "mapHeight");
    }

    public String getMapWidth(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "mapWidth");
    }

    public String getMapZoom(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "mapZoom");
    }

    public String getNbDbName() {
        return nbDbName;
    }

    public String getNbIp() {
        return nbIp;
    }

    public String getNbPass() {
        return nbPass;
    }

    public String getNbPort() {
        return nbPort;
    }

    public String getNbUser() {
        return nbUser;
    }

    public ArrayList<AlertChannel> getNonGeofencedPokeChannels() {
        return pokeChannels.getNonGeofencedChannels();
    }

    public ArrayList<AlertChannel> getNonGeofencedRaidChannels() {
        return raidChannels.getNonGeofencedChannels();
    }

    public NotificationLimit getNonSupporterLimit() {
        return nonSupporterLimit;
    }

    public NotificationLimit getNotificationLimit(Member member) {
        for (Role role : member.getRoles()) {
            NotificationLimit notificationLimit = roleLimits.get(role.getId());
            if (notificationLimit != null) {
                return notificationLimit;
            }
        }
        return nonSupporterLimit;
    }

    public ArrayList<AlertChannel> getPokeChannels(GeofenceIdentifier identifier) {
        return pokeChannels.getChannelsByGeofence(identifier);
    }

    public long getPokePollingDelay() {
        return pokePollingDelay;
    }

    public int getPokemonThreads() {
        return pokemonThreads;
    }

    public String getPresetsList() {
        StringBuilder list = new StringBuilder("```");

        for (String presetName : presets.keySet()) {
            list.append(String.format("  %s%n", presetName));
        }

        list.append("```");
        return list.toString();
    }

    public ArrayList<AlertChannel> getRaidChannels(GeofenceIdentifier identifier) {
        return raidChannels.getChannelsByGeofence(identifier);
    }

    public ArrayList<GeofenceIdentifier> getRaidChatGeofences(String id) {
        ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();

        for (Map.Entry<GeofenceIdentifier, String> entry : raidChats.entrySet()) {
            if (entry.getValue().equals(id)) {
                geofenceIdentifiers.add(entry.getKey());
            }
        }

        return geofenceIdentifiers;
    }

    public String[] getRaidChats(ArrayList<GeofenceIdentifier> geofences) {
        HashSet<String> chatIds = new HashSet<>();

        for (Map.Entry<GeofenceIdentifier, String> entry : raidChats.entrySet()) {
            boolean added = false;
            for (GeofenceIdentifier geofence : geofences) {
                if (added) break;
                if (entry.getKey().equals(geofence)) {
                    chatIds.add(entry.getValue());
                    added = true;
                }
            }
        }

        String[] chatIdStrings = new String[chatIds.size()];
        return chatIds.toArray(chatIdStrings);
    }

    public long getRaidPollingDelay() {
        return raidPollingDelay;
    }

    public int getRaidThreads() {
        return raidThreads;
    }

    public String getRmDbName() {
        return rmDbName;
    }

    public String getRmIp() {
        return rmIp;
    }

    public String getRmPass() {
        return rmPass;
    }

    public String getRmPort() {
        return rmPort;
    }

    public String getRmUser() {
        return rmUser;
    }

    public String getRoleLogId() {
        return roleLogId;
    }

    public List<String> getSupporterRoles() {
        String[] rolesArr = new String[roleLimits.size()];
        roleLimits.keySet().toArray(rolesArr);
        return Arrays.asList(rolesArr);
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public String getTitleFormatting(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "title");
    }

    public String getTitleUrl(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "titleUrl");
    }

    public String getToken() {
        return token;
    }

    public String getUserUpdatesId() {
        return userUpdatesId;
    }

    public boolean isRaidChannel(String id) {
        for (String s : raidChats.values()) {
            if (id.equals(s)) return true;
        }
        return false;
    }

    public boolean isRaidChannelsEnabled() {
        return raidChannels.size() > 0;
    }

    public boolean isRaidOrganisationEnabled() {
        return raidOrganisationEnabled;
    }

    public void loadEmotes() {
        for (String type : Raid.TYPES) {
            List<Emote> found = novaBot.jda.getEmotesByName(type, true);
            if (found.size() == 0) try {
                novaBot.guild.getController().createEmote(type, Icon.from(new File("static/icons/" + type + ".png"))).queue(emote ->
                                                                                                                                    emotes.put(type, emote));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else {
                emotes.put(type, found.get(0));
            }
        }
    }

    public boolean loggingEnabled() {
        return logging;
    }

    public static void main(String[] args) {
        NovaBot novaBot = new NovaBot();
        novaBot.setup();
        System.out.println(novaBot.config.token);
    }

    public boolean matchesFilter(JsonObject filter, RaidSpawn raidSpawn) {
        String searchStr = (raidSpawn.bossId >= 1) ? Pokemon.getFilterName(raidSpawn.bossId) : "Egg" + raidSpawn.raidLevel;

        JsonElement raidFilter = searchFilter(filter, searchStr);
        if (raidFilter == null) {
//            System.out.println(String.format("raidFilter %s is null for %s for channel with id %s", channel.filterName, searchStr,channel.channelId));
            raidFilter = searchFilter(filter, "Level" + raidSpawn.raidLevel);

            if (raidFilter == null) return false;
        }

        if (raidFilter.isJsonObject()) {
            System.out.println("objects as raid filters not supported yet");
            return false;
        } else {
            if (raidFilter.getAsBoolean()) {
                return true;
            } else {
                RaidNotificationSender.notificationLog.info("raid not enabled in filter, not posting");
                return false;
            }
        }
    }

    public boolean matchesFilter(JsonObject filter, PokeSpawn pokeSpawn, String filterName) {
        JsonElement pokeFilter = searchFilter(filter, Pokemon.getFilterName(pokeSpawn.getFilterId()));
        if (pokeFilter == null) {
            PokeNotificationSender.notificationLog.info(String.format("pokeFilter %s is null for %s", filterName, pokeSpawn.properties.get("pkmn")));
//            System.out.println(String.format("pokeFilter %s is null for %s for channel with id %s", channel.filterName, pokeSpawn.properties.get("pkmn"),channel.channelId));

            pokeFilter = searchFilter(filter, "Default");

            if (pokeFilter == null) {
                return false;
            }
        }
        if (pokeFilter.isJsonObject()) {
            JsonObject obj = pokeFilter.getAsJsonObject();

            JsonElement maxObj = obj.get("max_iv");
            JsonElement minObj = obj.get("min_iv");

            float max = maxObj == null ? 100 : maxObj.getAsFloat();
            float min = minObj == null ? 0 : minObj.getAsFloat();

            if (pokeSpawn.iv <= max && pokeSpawn.iv >= min) {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon between specified ivs (%s,%s)", min, max));
            } else {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon (%s%%) not between specified ivs (%s,%s). filter %s", pokeSpawn.iv, min, max, filterName));
                return false;
            }

            maxObj = obj.get("max_cp");
            minObj = obj.get("min_cp");

            max = maxObj == null ? Integer.MAX_VALUE : maxObj.getAsFloat();
            min = minObj == null ? 0 : minObj.getAsFloat();

            if (pokeSpawn.cp <= max && pokeSpawn.cp >= min) {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon between specified cp (%s,%s)", min, max));
            } else {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon (%sCP) not between specified cp (%s,%s)", pokeSpawn.cp, min, max));
                return false;
            }

            maxObj = obj.get("max_level");
            minObj = obj.get("min_level");

            max = maxObj == null ? 30 : maxObj.getAsInt();
            min = minObj == null ? 0 : minObj.getAsFloat();

            if (pokeSpawn.level <= max && pokeSpawn.level >= min) {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon between specified level (%s,%s)", min, max));
            } else {
                PokeNotificationSender.notificationLog.info(String.format("Pokemon (level %s) not between specified level (%s,%s)", pokeSpawn.level, min, max));
                return false;
            }

            JsonArray sizes = obj.getAsJsonArray("size");

            if (sizes != null) {
                String  spawnSize = pokeSpawn.properties.get("size");
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

    public String novabotRole() {
        return novabotRoleId;
    }

    public boolean pokemonEnabled() {
        return pokemonEnabled;
    }


    public boolean presetsEnabled() {
        return presets.size() > 0;
    }

    public boolean raidsEnabled() {
        return raidsEnabled;
    }

    public boolean showMap(String fileName, String formatKey) {
        return Boolean.parseBoolean(formats.get(fileName).getFormatting(formatKey, "showMap"));
    }

    public boolean showStartupMessage() {
        return startupMessage;
    }

    public boolean standardRaidTable() {
        return standardRaidTable;
    }

    public boolean statsEnabled() {
        return stats;
    }

    public boolean suburbsEnabled() {
        return novaBot.suburbs.notEmpty();
    }

    public boolean useGeofences() {
        return Geofencing.notEmpty();
    }

    public boolean useRmDb() {
        return useRmDb;
    }

    private void loadFilter(String filterName, HashMap<String, JsonObject> filterMap) {
        JsonObject filter = null;
        JsonParser parser = new JsonParser();

        try {
            JsonElement element = parser.parse(new FileReader(filterName));

            if (element.isJsonObject()) {
                filter = element.getAsJsonObject();
            }

            if (filterMap.put(filterName, filter) == null) {
                novaBot.novabotLog.info(String.format("Loaded filter %s", filterName));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadFormatting(String fileName) {

        Ini formatting;
        try {
            formatting = new Ini(new File(fileName));

            Format format = new Format();

            for (String formatKey : formatKeys) {
                Ini.Section section = formatting.get(formatKey);

                for (String var : formattingVars) {
                    format.addFormatting(formatKey, var, section.get(var));
                }

                if (formatKey.equals("pokemon")) {
                    format.addFormatting(formatKey, "encounteredBody", section.get("encounteredBody"));
                    format.addFormatting(formatKey, "encounteredTitle", section.get("encounteredTitle"));
                }
            }

            formats.put(fileName, format);
        } catch (FileNotFoundException e) {
            novaBot.novabotLog.warn(String.format("Couldn't find formatting file %s", fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<GeofenceIdentifier, String> loadGeofencedChannels(File file, HashMap<GeofenceIdentifier, String> map) {
        Scanner sc = null;
        try {
            sc = new Scanner(file);
            while (sc.hasNext()) {
                String line = sc.nextLine().toLowerCase();

                String[] split = line.split("=");

                ArrayList<GeofenceIdentifier> geofenceIdentifiers = GeofenceIdentifier.fromString(split[0].trim());

                String channelId = split[1].trim();

                for (GeofenceIdentifier geofenceIdentifier : geofenceIdentifiers) {
                    map.put(geofenceIdentifier, channelId);
                }

            }
        } catch (FileNotFoundException e) {
            novaBot.novabotLog.warn(String.format("Couldn't find %s", file.toString()));
        }


        return map;
    }

    private ArrayList<String> loadKeys(File gkeys) {

        ArrayList<String> keys = new ArrayList<>();

        try {
            Scanner in = new Scanner(gkeys);

            while (in.hasNext()) {
                String key = in.nextLine();
                keys.add(key);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return keys;
    }

    private void loadPokemonChannels() {
        if (novaBot.geofencing == null || !novaBot.geofencing.loaded) novaBot.loadGeofences();

        File file = new File("pokechannels.ini");

        try (Scanner in = new Scanner(file)) {

            String                      channelId           = null;
            String                      filterName          = null;
            String                      formattingName      = "formatting.ini";
            HashSet<GeofenceIdentifier> geofenceIdentifiers = null;

            boolean first = true;

            while (in.hasNext()) {
                String line = in.nextLine().toLowerCase();

                if (line.length() == 0 || line.charAt(0) == ';') {
                    continue;
                }

                if (line.charAt(0) == '[') {
                    AlertChannel channel;

                    if (channelId != null) {
                        channel = new AlertChannel(channelId);

                        if (filterName != null) {
                            channel.filterName = filterName;

                            channel.geofences = geofenceIdentifiers;

                            channel.formattingName = formattingName;

                            pokeChannels.add(channel);
                        } else {
                            System.out.println("couldn't find filter name");
                        }

                    } else if (!first) {
                        System.out.println("couldn't find channel id");
                    }

                    int end = line.indexOf("]");
                    channelId = line.substring(1, end).trim();

                    first = false;
                } else {
                    int equalsIndex = line.indexOf("=");

                    if (!(equalsIndex == -1)) {
                        String parameter = line.substring(0, equalsIndex).trim();
                        String value     = line.substring(equalsIndex + 1).trim();

                        switch (parameter) {
                            case "geofences":
                                if (value.equals("all")) {
                                    geofenceIdentifiers = null;
                                    continue;
                                }
                                geofenceIdentifiers = new HashSet<>();

                                ArrayList<String> geofences;

                                if (value.charAt(0) == '[') {
                                    geofences = Util.parseList(value);
                                } else {
                                    geofences = new ArrayList<>();
                                    geofences.add(value);
                                }

                                for (String s : geofences) {
                                    geofenceIdentifiers.addAll(GeofenceIdentifier.fromString(s));
                                }
                                break;
                            case "filter":
                                filterName = value;

                                if (!pokeFilters.containsKey(filterName)) {
                                    loadFilter(filterName, pokeFilters);
                                }
                                break;
                            case "formatting":
                                formattingName = value;

                                if (!formats.containsKey(formattingName)) {
                                    loadFormatting(formattingName);
                                }
                                break;
                        }
                    }
                }
            }

            AlertChannel channel;
            if (channelId != null) {
                channel = new AlertChannel(channelId);

                if (filterName != null) {
                    channel.filterName = filterName;

                    channel.formattingName = formattingName;

                    channel.geofences = geofenceIdentifiers;

                    pokeChannels.add(channel);
                } else {
                    System.out.println("couldn't find filter name");
                }

            } else {
                System.out.println("couldn't find channel id");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadPresets() {
        File file = new File("presets.ini");

        try (Scanner in = new Scanner(file)) {

            String  presetName = null;
            String  filterName = null;
            Boolean pokemon    = null;

            boolean first = true;

            while (in.hasNext()) {
                String line = in.nextLine().toLowerCase();

                if (line.length() == 0 || line.charAt(0) == ';') {
                    continue;
                }

                if (line.charAt(0) == '[') {

                    if (presetName != null) {
                        parseBlock(presetName, filterName, pokemon);
                    } else if (!first) {
                        System.out.println("couldn't find preset name");
                    }

                    int end = line.indexOf("]");
                    presetName = line.substring(1, end).trim();

                    first = false;
                } else {
                    int equalsIndex = line.indexOf("=");

                    if (!(equalsIndex == -1)) {

                        String parameter = line.substring(0, equalsIndex).trim();
                        String value     = line.substring(equalsIndex + 1).trim();

                        switch (parameter) {
                            case "type":
                                pokemon = value.equals("pokemon");
                                break;
                            case "filter":
                                filterName = value;
                                break;
                        }
                    }
                }
            }

            if (presetName != null) {
                parseBlock(presetName, filterName, pokemon);
            } else {
                System.out.println("couldn't find preset name");
            }

        } catch (FileNotFoundException e) {
            novaBot.novabotLog.warn("Couldn't find presets.ini, ignoring");
        }
    }

    private void loadRaidChannels() {
        if (novaBot.geofencing == null || !novaBot.geofencing.loaded) novaBot.loadGeofences();

        File file = new File("raidchannels.ini");

        try (Scanner in = new Scanner(file)) {

            String                      channelId           = null;
            String                      filterName          = null;
            String                      formattingName      = "formatting.ini";
            String                      chatId              = null;
            HashSet<GeofenceIdentifier> geofenceIdentifiers = null;

            boolean first = true;

            while (in.hasNext()) {
                String line = in.nextLine().toLowerCase();

                if (line.length() == 0 || line.charAt(0) == ';') {
                    continue;
                }

                if (line.charAt(0) == '[') {
                    RaidChannel channel;

                    if (channelId != null) {
                        channel = new RaidChannel(channelId);

                        if (filterName != null) {
                            channel.filterName = filterName;

                            channel.geofences = geofenceIdentifiers;

                            channel.formattingName = formattingName;

                            channel.chatId = chatId;

                            if (chatId != null && geofenceIdentifiers != null) {
                                for (GeofenceIdentifier geofenceIdentifier : geofenceIdentifiers) {
                                    raidChats.put(geofenceIdentifier, chatId);
                                }
                            }

                            raidChannels.add(channel);
                        } else {
                            System.out.println("couldn't find filter name");
                        }

                    } else if (!first) {
                        System.out.println("couldn't find channel id");
                    }

                    int end = line.indexOf("]");
                    channelId = line.substring(1, end).trim();

                    first = false;
                } else {
                    int equalsIndex = line.indexOf("=");

                    if (!(equalsIndex == -1)) {
                        String parameter = line.substring(0, equalsIndex).trim();
                        String value     = line.substring(equalsIndex + 1).trim();

                        switch (parameter) {
                            case "geofences":
                                if (value.equals("all")) {
                                    geofenceIdentifiers = null;
                                    continue;
                                }
                                geofenceIdentifiers = new HashSet<>();

                                ArrayList<String> geofences;

                                if (value.charAt(0) == '[') {
                                    geofences = Util.parseList(value);
                                } else {
                                    geofences = new ArrayList<>();
                                    geofences.add(value);
                                }

                                for (String s : geofences) {
                                    geofenceIdentifiers.addAll(GeofenceIdentifier.fromString(s));
                                }
                                break;
                            case "filter":
                                filterName = value;

                                if (!raidFilters.containsKey(filterName)) {
                                    loadFilter(filterName, raidFilters);
                                }
                                break;
                            case "formatting":
                                formattingName = value;

                                if (!formats.containsKey(formattingName)) {
                                    loadFormatting(formattingName);
                                }
                                break;
                            case "chat":
                                chatId = value;
                                break;
                        }
                    }
                }
            }

            RaidChannel channel;
            if (channelId != null) {
                channel = new RaidChannel(channelId);

                if (filterName != null) {
                    channel.filterName = filterName;

                    channel.formattingName = formattingName;

                    channel.geofences = geofenceIdentifiers;

                    channel.chatId = chatId;

                    if (chatId != null && geofenceIdentifiers != null) {
                        for (GeofenceIdentifier geofenceIdentifier : geofenceIdentifiers) {
                            raidChats.put(geofenceIdentifier, chatId);
                        }
                    }

                    raidChannels.add(channel);
                } else {
                    System.out.println("couldn't find filter name");
                }

            } else {
                System.out.println("couldn't find channel id");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadSupporterRoles() {
        File file = new File("supporterlevels.txt");

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNext()) {
                String line = sc.nextLine().toLowerCase();

                String[] split = line.split("=");

                String roleId = split[0].trim();

                roleLimits.put(roleId, NotificationLimit.fromString(line));
            }
        } catch (FileNotFoundException e) {
            novaBot.novabotLog.warn("Couldn't find supporterlevels.txt, ignoring");
        }
    }

    private void parseBlock(String presetName, String filterName, Boolean pokemon) {
        if (filterName != null) {
            if (pokemon != null) {
                if (pokemon) {
                    if (!pokeFilters.containsKey(filterName)) {
                        loadFilter(filterName, pokeFilters);
                    }
                } else {
                    if (!raidFilters.containsKey(filterName)) {
                        loadFilter(filterName, raidFilters);
                    }
                }

                presets.put(presetName, filterName);
            } else {
                System.out.println("couldn't find type value");
            }
        } else {
            System.out.println("couldn't find filter name");
        }
    }

    private JsonElement searchFilter(JsonObject filter, String search) {
        if (filter == null || search == null) return null;
        return filter.get(Util.capitaliseFirst(search));
    }
}
