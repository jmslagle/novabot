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
import java.util.*;

import static core.MessageListener.guild;
import static core.MessageListener.jda;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.WARNING;
import static raids.Raid.emotes;

/**
 * Created by Owner on 13/05/2017.
 */
public class Config {

    private final boolean standardRaidTable;
    private final String googleSuburbField;
    private final NotificationLimit nonSupporterLimit;
    private final Ini ini;

    private ArrayList<String> GMAPS_KEYS = new ArrayList<>();

    private final ArrayList<Integer> blacklist = new ArrayList<>();
    private ArrayList<String> supporterRoles = new ArrayList<>();

    private final String token;
    private final boolean geofences;
    private final boolean logging;
    private final boolean stats;
    private final boolean startupMessage;
    private final boolean supporterOnly;
    private final boolean countLocationsInLimits;

    private final String timeZone;

    private final String footerText;

    private final String adminRole;

    private final String commandChannelId;
    private String roleLogId;
    private String userUpdatesId;

    private final long pokePollingRate;
    private final long raidPollingRate;


    private final String rmUser;
    private final String rmPass;
    private final String rmIp;
    private final String rmPort;
    private final String rmDbName;

    private final String nbUser;
    private final String nbPass;
    private final String nbIp;
    private final String nbPort;
    private final String nbDbName;

    private final boolean useRmDb;

    private final boolean raidsEnabled;
    private final boolean pokemonEnabled;

    private final boolean raidOrganisationEnabled;

    private final HashMap<GeofenceIdentifier, String> geofencedChannelIds = new HashMap<>();
    private final String novabotRoleId;

    private final HashMap<String, NotificationLimit> roleLimits = new HashMap<>();
    private HashMap<GeofenceIdentifier, String> raidChats = new HashMap<>();

    public final HashMap<String, JsonObject> pokeFilters = new HashMap<>();
    private final HashMap<String, Format> formats = new HashMap<>();

    private final ArrayList<AlertChannel> pokeChannels = new ArrayList<>();
    private final ArrayList<AlertChannel> raidChannels = new ArrayList<>();


    private static final String[] formatKeys = new String[]{"pokemon", "raidEgg", "raidBoss"};
    private static final String[] formattingVars = new String[]{"title", "titleUrl", "body", "showMap", "mapZoom", "mapWidth", "mapHeight"};
    public final ArrayList<Integer> raidBosses = new ArrayList<>();
    public final HashMap<String, JsonObject> raidFilters = new HashMap<>();

    public final HashMap<String, String> presets = new HashMap<>();

    public Config(Ini configIni, File gkeys) {
        this.ini = configIni;

        Ini.Section config = ini.get("config");

        token = config.get("token");

        String blacklistStr = config.get("blacklist");

        for (String s : Util.parseList(blacklistStr)) {
            blacklist.add(Integer.valueOf(s));
        }

        String raidBossStr = config.get("raidBosses");

        for (String s : Util.parseList(raidBossStr)) {
            raidBosses.add(Integer.valueOf(s));
        }

        geofences = Boolean.parseBoolean(config.get("geofences"));

        useRmDb = Boolean.parseBoolean(config.get("useRmDb"));

        standardRaidTable = Boolean.parseBoolean(config.get("standardRaidTable"));

        googleSuburbField = config.get("googleSuburbField");

        raidsEnabled = Boolean.parseBoolean(config.get("raids"));

        raidOrganisationEnabled = Boolean.parseBoolean(config.get("raidOrganisation"));

        pokemonEnabled = Boolean.parseBoolean(config.get("pokemon"));

        pokePollingRate = Long.parseLong(config.get("pokePollingRate"));

        raidPollingRate = Long.parseLong(config.get("raidPollingRate"));

        supporterOnly = Boolean.parseBoolean(config.get("supporterOnly"));

        nonSupporterLimit = NotificationLimit.fromString(config.get("nonSupporterLimit"));

        countLocationsInLimits = Boolean.parseBoolean(config.get("countLocationsInLimits"));

        String supporterRolesLine = config.get("supporterRoles");

        if(supporterRolesLine.contains("[")){
            supporterRoles = Util.parseList(config.get("supporterRoles"));
        }else {
            supporterRoles.add(supporterRolesLine.trim());
        }

        commandChannelId = config.get("commandChannel");

        logging = Boolean.parseBoolean(config.get("logging"));

        if (logging) {
            roleLogId = config.get("roleLogChannel");

            userUpdatesId = config.get("userUpdatesChannel");
        }

        timeZone = config.get("timezone");

        footerText = config.get("footerText");

        stats = Boolean.parseBoolean(config.get("stats"));

        startupMessage = Boolean.parseBoolean(config.get("startupMessage"));

        adminRole = config.get("adminRole");

        novabotRoleId = config.get("novabotRole");

        Ini.Section rocketmapDb = ini.get("rocketmap db");
        rmUser = rocketmapDb.get("user");
        rmPass = rocketmapDb.get("password");
        rmIp = rocketmapDb.get("ip");
        rmPort = rocketmapDb.get("port");
        rmDbName = rocketmapDb.get("dbName");

        Ini.Section novabotDb = ini.get("novabot db");
        nbUser = novabotDb.get("user");
        nbPass = novabotDb.get("password");
        nbIp = novabotDb.get("ip");
        nbPort = novabotDb.get("port");
        nbDbName = novabotDb.get("dbName");

        GMAPS_KEYS = loadKeys(gkeys);

        loadFormatting("formatting.ini");

        if (raidsEnabled()) {
            loadRaidChannels();
        }

        if (raidOrganisationEnabled) {
            loadRaidChats();
        }

        loadSupporterRoles();

        loadPokemonChannels();

        loadPresets();
    }

    private void loadPresets() {
        File file = new File("presets.ini");

        try (Scanner in = new Scanner(file)) {

            String presetName = null;
            String filterName = null;
            Boolean pokemon = null;

            boolean first = true;

            while (in.hasNext()) {
                String line = in.nextLine().toLowerCase();

                if (line.length() == 0 || line.charAt(0) == ';') {
                    continue;
                }

                if (line.charAt(0) == '[') {

                    if (presetName != null) {
                        if (filterName != null) {
                            if (pokemon != null) {
                                if (pokemon) {
                                    if (!pokeFilters.containsKey(filterName)) {
                                        loadFilter(filterName,pokeFilters);
                                    }
                                } else {
                                    if (!raidFilters.containsKey(filterName)) {
                                        loadFilter(filterName,raidFilters);
                                    }
                                }

                                presets.put(presetName, filterName);
                        }else{
                            System.out.println("couldn't find type value");
                        }
                        } else {
                            System.out.println("couldn't find filter name");
                        }

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
                        String value = line.substring(equalsIndex + 1).trim();

                        switch (parameter){
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
                if (filterName != null) {
                    if (pokemon != null) {

                        if (pokemon) {
                            if (!pokeFilters.containsKey(filterName)) {
                                loadFilter(filterName,pokeFilters);
                            }
                        } else {
                            if (!raidFilters.containsKey(filterName)) {
                                loadFilter(filterName,raidFilters);
                            }
                        }

                        presets.put(presetName, filterName);
                    }else{
                        System.out.println("couldn't find type value");
                    }
                } else {
                    System.out.println("couldn't find filter name");
                }

            } else {
                System.out.println("couldn't find preset name");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadRaidChannels() {
        if (!Geofencing.loaded) Geofencing.loadGeofences();

        File file = new File("raidchannels.ini");

        try (Scanner in = new Scanner(file)) {

            String channelId = null;
            String filterName = null;
//            Integer minLevel = null;
            String formattingName = "formatting.ini";
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

//                            channel.minLevel = minLevel;

                            channel.geofences = geofenceIdentifiers;

                            channel.formattingName = formattingName;

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
                        String value = line.substring(equalsIndex + 1).trim();

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
//                            case "minLevel":
//                                minLevel = Integer.parseInt(value);
//                                break;
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
                        }
                    }
                }
            }

            AlertChannel channel;
            if (channelId != null) {
                channel = new RaidChannel(channelId);

                if (filterName != null) {
                    channel.filterName = filterName;

//                    channel.minLevel = minLevel;

                    channel.formattingName = formattingName;

                    channel.geofences = geofenceIdentifiers;

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

    private void loadFilter(String filterName, HashMap<String, JsonObject> filterMap) {
        JsonObject filter = null;
        JsonParser parser = new JsonParser();

        try {
            JsonElement element = parser.parse(new FileReader(filterName));

            if (element.isJsonObject()) {
                filter = element.getAsJsonObject();
            }

            filterMap.put(filterName, filter);
            System.out.println(String.format("Loaded filter %s: %s",filterName,filter));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadFormatting(String fileName) {

        Ini formatting = null;
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
                    format.addFormatting(formatKey,"encounteredTitle",section.get("encounteredTitle"));
                }
            }

            formats.put(fileName, format);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPokemonChannels() {
        if (!Geofencing.loaded) Geofencing.loadGeofences();

        File file = new File("pokechannels.ini");

        try (Scanner in = new Scanner(file)) {

            String channelId = null;
            String filterName = null;
            String formattingName = "formatting.ini";
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
                        String value = line.substring(equalsIndex + 1).trim();

                        if (parameter.equals("geofences")) {
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
                        } else if (parameter.equals("filter")) {
                            filterName = value;

                            if (!pokeFilters.containsKey(filterName)) {
                                loadFilter(filterName, pokeFilters);
                            }
                        } else if (parameter.equals("formatting")) {
                            formattingName = value;

                            if (!formats.containsKey(formattingName)) {
                                loadFormatting(formattingName);
                            }
                        }
                    }
                }
            }

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

            } else {
                System.out.println("couldn't find channel id");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JsonElement searchFilter(JsonObject filter, String search) {
        if (filter == null || search == null) return null;
        return filter.get(Util.capitaliseFirst(search));
    }

    private void loadRaidChats() {
        if (!Geofencing.loaded) Geofencing.loadGeofences();

        File file = new File("raidchats.txt");

        raidChats = loadGeofencedChannels(file, raidChats);
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
            MessageListener.novabotLog.log(WARNING,"Couldn't find supporterlevels.txt, ignoring");
        }
    }

    private HashMap<GeofenceIdentifier, String> loadGeofencedChannels(File file, HashMap<GeofenceIdentifier, String> map) {
        try {
            Scanner sc = new Scanner(file);

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
            e.printStackTrace();
        }

        return map;
    }

    public String getTitleUrl(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "titleUrl");
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

    public static void main(String[] args) {
        try {
            Config config = new Config(
                    new Ini(new File("config.ini")),
                    new File("gkeys.txt"));

            System.out.println(config.matchesFilter(config.raidFilters.get("raidfilter.json"),new RaidSpawn(3,true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean useGeofences() {
        return geofences;
    }

    public String getToken() {
        return token;
    }

    public String getRmUser() {
        return rmUser;
    }

    public ArrayList<Integer> getBlacklist() {
        return blacklist;
    }

    public ArrayList<String> getKeys() {
        return GMAPS_KEYS;
    }

    public String getNbPass() {
        return nbPass;
    }

    public String getRmPass() {
        return rmPass;
    }

    public String getNbUser() {
        return nbUser;
    }

    public String getCommandChannelId() {
        return commandChannelId;
    }

    public boolean loggingEnabled() {
        return logging;
    }

    public String getRoleLogId() {
        return roleLogId;
    }

    public String getUserUpdatesId() {
        return userUpdatesId;
    }

    public long getPokePollingRate() {
        return pokePollingRate;
    }

    public boolean isSupporterOnly() {
        return supporterOnly;
    }

    public ArrayList<String> getSupporterRoles() {
        return supporterRoles;
    }

    public String getRmIp() {
        return rmIp;
    }

    public String getRmPort() {
        return rmPort;
    }

    public String getRmDbName() {
        return rmDbName;
    }

    public String getNbIp() {
        return nbIp;
    }

    public String getNbPort() {
        return nbPort;
    }

    public String getNbDbName() {
        return nbDbName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getFooterText() {
        return footerText;
    }

    public boolean statsEnabled() {
        return stats;
    }

    public boolean showStartupMessage() {
        return startupMessage;
    }

    public String formatStr(HashMap<String, String> properties, String toFormat) {
        final String[] str = {toFormat};

        properties.forEach((key, value) -> {
            str[0] = str[0].replace(String.format("<%s>", key), value);
        });

        return str[0];
    }

    public String getTitleFormatting(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "title");
    }

    public String getBodyFormatting(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "body");
    }

    public String getEncounterBodyFormatting(String fileName) {
        return formats.get(fileName).getFormatting("pokemon", "encounteredBody");
    }

    public String getEncounterTitleFormatting(String fileName) {
        return formats.get(fileName).getFormatting("pokemon", "encounteredTitle");
    }

    public String getMapZoom(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "mapZoom");
    }

    public String getMapWidth(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "mapWidth");
    }

    public String getMapHeight(String fileName, String formatKey) {
        return formats.get(fileName).getFormatting(formatKey, "mapHeight");
    }

    public boolean showMap(String fileName, String formatKey) {
        return Boolean.parseBoolean(formats.get(fileName).getFormatting(formatKey, "showMap"));
    }

    public String getAdminRole() {
        return adminRole;
    }

    public boolean useRmDb() {
        return useRmDb;
    }

    public long getRaidPollingRate() {
        return raidPollingRate;
    }

    public boolean raidsEnabled() {
        return raidsEnabled;
    }

    public boolean pokemonEnabled() {
        return pokemonEnabled;
    }

    public boolean isRaidChannelsEnabled() {
        return raidChannels.size() > 0;
    }

    public String novabotRole() {
        return novabotRoleId;
    }

    public boolean isRaidOrganisationEnabled() {
        return raidOrganisationEnabled;
    }

    public NotificationLimit getNotificationLimit(Member member) {
        for (Role role : member.getRoles()) {
            NotificationLimit notificationLimit = roleLimits.get(role.getId());
            if (notificationLimit != null) {
                return notificationLimit;
            }
        }
        return null;
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

    public String raidChatsList() {
        StringBuilder str = new StringBuilder();

        for (String s : geofencedChannelIds.values()) {
            str.append(String.format("  %s%n", guild.getTextChannelById(s).getAsMention()));
        }

        return str.toString();
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

    public boolean countLocationsInLimits() {
        return countLocationsInLimits;
    }

    public NotificationLimit getNonSupporterLimit() {
        return nonSupporterLimit;
    }

    public boolean isRaidChannel(String id) {
        for (String s : raidChats.values()) {
            if (id.equals(s)) return true;
        }
        return false;
    }

    public boolean standardRaidTable() {
        return standardRaidTable;
    }

    public String getGoogleSuburbField() {
        return googleSuburbField;
    }

    public void loadEmotes() {
        for (String type : Raid.TYPES) {
            List<Emote> found = jda.getEmotesByName(type, true);
            if (found.size() == 0) try {
                guild.getController().createEmote(type, Icon.from(getClass().getResourceAsStream(type + ".png"))).queue(emote ->
                        emotes.put(type, emote));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else {
                emotes.put(type, found.get(0));
            }
        }
    }

    public ArrayList<AlertChannel> getAlertChannels(GeofenceIdentifier identifier) {
        ArrayList<AlertChannel> channels = null;

        for (AlertChannel alertChannel : pokeChannels) {
            if (alertChannel.geofences != null && alertChannel.geofences.contains(identifier)) {
                if (channels == null) channels = new ArrayList<>();
                channels.add(alertChannel);
            }
        }
        return channels;
    }

    public ArrayList<AlertChannel> getRaidChannels(GeofenceIdentifier identifier) {
        ArrayList<AlertChannel> channels = null;

        for (AlertChannel alertChannel : raidChannels) {
            if (alertChannel.geofences != null && alertChannel.geofences.contains(identifier)) {
                if (channels == null) channels = new ArrayList<>();
                channels.add(alertChannel);
            }
        }
        return channels;
    }

    public ArrayList<AlertChannel> getNonGeofencedPokeChannels() {
        ArrayList<AlertChannel> channels = null;

        for (AlertChannel alertChannel : pokeChannels) {
            if (alertChannel.geofences == null) {
                if (channels == null) channels = new ArrayList<>();
                channels.add(alertChannel);
            }

        }

        return channels;
    }

    public ArrayList<AlertChannel> getNonGeofencedRaidChannels() {
        ArrayList<AlertChannel> channels = null;

        for (AlertChannel alertChannel : raidChannels) {
            if (alertChannel.geofences == null) {
                if (channels == null) channels = new ArrayList<>();
                channels.add(alertChannel);
            }

        }

        return channels;
    }

    public ArrayList<String> findMatchingPresets(RaidSpawn raidSpawn) {
        ArrayList<String> matching = new ArrayList<>();

        for (Map.Entry<String, String> entry : presets.entrySet()) {
            if (matchesFilter(raidFilters.get(entry.getValue()),raidSpawn)){
                matching.add(entry.getKey());
            }
        }
        return matching;
    }

    public ArrayList<String> findMatchingPresets(PokeSpawn pokeSpawn) {
        ArrayList<String> matching = new ArrayList<>();

        for (Map.Entry<String, String> entry : presets.entrySet()) {
            JsonObject filter = pokeFilters.get(entry.getValue());
            if (filter != null && matchesFilter(filter,pokeSpawn,entry.getValue())){
                matching.add(entry.getKey());
            }
        }
        return matching;
    }

    public boolean matchesFilter(JsonObject filter, RaidSpawn raidSpawn) {
        String searchStr = (raidSpawn.bossId >= 1) ? Pokemon.getFilterName(raidSpawn.bossId) : "Egg"+raidSpawn.raidLevel;

        JsonElement raidFilter =searchFilter(filter, searchStr);
        if (raidFilter == null) {
//            System.out.println(String.format("raidFilter %s is null for %s for channel with id %s", channel.filterName, searchStr,channel.channelId));
            raidFilter = searchFilter(filter,"Level"+raidSpawn.raidLevel);

            if(raidFilter == null) return false;
        }

        if (raidFilter.isJsonObject()) {
            System.out.println("objects as raid filters not supported yet");
            return false;
        } else {
            if (raidFilter.getAsBoolean()) {
                return true;
            } else {
                RaidNotificationSender.notificationLog.log(INFO, "raid not enabled in filter, not posting");
                return false;
            }
        }
    }

    public boolean matchesFilter(JsonObject filter, PokeSpawn pokeSpawn, String filterName){
        JsonElement pokeFilter = searchFilter(filter, Pokemon.getFilterName(pokeSpawn.getFilterId()));
        if (pokeFilter == null) {
            PokeNotificationSender.notificationLog.log(INFO,String.format("pokeFilter %s is null for %s", filterName, pokeSpawn.properties.get("pkmn")));
//            System.out.println(String.format("pokeFilter %s is null for %s for channel with id %s", channel.filterName, pokeSpawn.properties.get("pkmn"),channel.channelId));

            pokeFilter = searchFilter(filter,"Default");

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
                PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon between specified ivs (%s,%s)", min, max));
            } else {
                PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon (%s%%) not between specified ivs (%s,%s). filter %s", pokeSpawn.iv, min, max, filterName));
                return false;
            }

            maxObj = obj.get("max_cp");
            minObj = obj.get("min_cp");

            max = maxObj == null ? Integer.MAX_VALUE : maxObj.getAsFloat();
            min = minObj == null ? 0 : minObj.getAsFloat();

            if (pokeSpawn.cp <= max && pokeSpawn.cp >= min){
                PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon between specified cp (%s,%s)", min, max));
            }else {
                PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon (%sCP) not between specified cp (%s,%s)", pokeSpawn.cp, min, max));
                return false;
            }

            maxObj = obj.get("max_level");
            minObj = obj.get("min_level");

            max = maxObj == null ? 30 : maxObj.getAsInt();
            min = minObj == null ? 0 : minObj.getAsFloat();

            if (pokeSpawn.level <= max && pokeSpawn.level >= min){
                PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon between specified level (%s,%s)", min, max));
            }else {
                PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon (level %s) not between specified level (%s,%s)", pokeSpawn.level, min, max));
                return false;
            }

            JsonArray sizes = obj.getAsJsonArray("size");

            if (sizes != null){
                String spawnSize = pokeSpawn.properties.get("size");
                boolean passed = false;

                for (JsonElement size : sizes) {
                    if (size.getAsString().equals(spawnSize)){
                        PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon size %s passed filter", spawnSize));
                        passed = true;
                        break;
                    }
                }

                if (!passed){
                    PokeNotificationSender.notificationLog.log(INFO, String.format("Pokemon size %s did not pass filter", spawnSize));
                    return false;
                }
            }
        return true;
        } else {
            if (pokeFilter.getAsBoolean()) {
                PokeNotificationSender.notificationLog.log(INFO, "Pokemon enabled in filter, posting to Discord");
                return true;
            } else {
                PokeNotificationSender.notificationLog.log(INFO, "Pokemon not enabled in filter, not posting");
                return false;
            }
        }
    }

    public String getPresetsList() {
        StringBuilder list = new StringBuilder("```");

        for (String presetName : presets.keySet()) {
            list.append(String.format("  %s%n", presetName));
        }

        list.append("```");
        return list.toString();
    }

    public boolean presetsEnabled() {
        return presets.size() > 0;
    }
}
