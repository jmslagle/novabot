package core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import maps.GeofenceIdentifier;
import maps.Geofencing;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.ini4j.Ini;
import pokemon.Pokemon;
import raids.Raid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static core.MessageListener.guild;
import static core.MessageListener.jda;
import static raids.Raid.emotes;

/**
 * Created by Owner on 13/05/2017.
 */
public class Config {

    private final boolean standardRaidTable;
    private final String googleSuburbField;
    private NotificationLimit nonSupporterLimit;
    Ini ini;

    ArrayList<String> GMAPS_KEYS = new ArrayList<>();

    ArrayList<Integer> blacklist = new ArrayList<>();
    private ArrayList<String> supporterRoles;

    private String token;
    private boolean geofences;
    private boolean logging;
    private boolean nests;
    private boolean stats;
    private boolean startupMessage;
    private boolean supporterOnly;
    private boolean countLocationsInLimits;

    private String timeZone;

    private String footerText;

    private String adminRole;

    private String commandChannelId;
    private String roleLogId;
    private String userUpdatesId;

    private long pokePollingRate;
    private long raidPollingRate;


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

    private HashMap<String,String> bodyFormatting = new HashMap<>();
    private String encounterBodyFormatting;
    private HashMap<String,String> titleFormatting = new HashMap<>();
    private HashMap<String,String> titleUrl = new HashMap<>();

    private HashMap<String,String> mapZoom = new HashMap<>();
    private HashMap<String,String> mapWidth = new HashMap<>();
    private HashMap<String,String> mapHeight = new HashMap<>();

    private HashMap<String,Boolean> showMap = new HashMap<>();

    private boolean useChannels;
    private boolean useRmDb;

    private boolean raidsEnabled;
    private boolean raidChannelsEnabled;
    private boolean pokemonEnabled;

    private boolean raidOrganisationEnabled;

    private HashMap<GeofenceIdentifier,String> geofencedChannelIds = new HashMap<>();
    private String novabotRoleId;

    HashMap<String,NotificationLimit> roleLimits = new HashMap<>();
    private HashMap<GeofenceIdentifier, String> raidChats = new HashMap<>();
    HashMap<GeofenceIdentifier,String> pokemonChannels = new HashMap<>();
    private int minRaidLevel;
    public JsonObject pokemonFilter;

    public Config(Ini configIni, File gkeys, Ini formattingIni){
        this.ini = configIni;

        Ini.Section config = ini.get("config");

        token = config.get("token");

        String blacklistStr = config.get("blacklist");

        for (String s : Util.parseList(blacklistStr)) {
            blacklist.add(Integer.valueOf(s));
        }

        geofences = Boolean.parseBoolean(config.get("geofences"));

        useChannels = Boolean.parseBoolean(config.get("channels"));

        useRmDb = Boolean.parseBoolean(config.get("useRmDb"));

        standardRaidTable = Boolean.parseBoolean(config.get("standardRaidTable"));

        googleSuburbField = config.get("googleSuburbField");

        raidsEnabled = Boolean.parseBoolean(config.get("raids"));

        minRaidLevel = Integer.parseInt(config.get("minRaidLevel"));

        raidOrganisationEnabled = Boolean.parseBoolean(config.get("raidOrganisation"));

        raidChannelsEnabled = Boolean.parseBoolean(config.get("raidChannels"));

        pokemonEnabled = Boolean.parseBoolean(config.get("pokemon"));

        pokePollingRate = Long.parseLong(config.get("pokePollingRate"));

        raidPollingRate = Long.parseLong(config.get("raidPollingRate"));

        nests = Boolean.parseBoolean(config.get("nests"));

        supporterOnly = Boolean.parseBoolean(config.get("supporterOnly"));

        nonSupporterLimit = NotificationLimit.fromString(config.get("nonSupporterLimit"));

        countLocationsInLimits = Boolean.parseBoolean(config.get("countLocationsInLimits"));

        supporterRoles = Util.parseList(config.get("supporterRoles"));

        commandChannelId = config.get("commandChannel");

        logging = Boolean.parseBoolean(config.get("logging"));

        roleLogId = config.get("roleLogChannel");

        userUpdatesId = config.get("userUpdatesChannel");

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

        String[] formatKeys = new String[] {"pokemon","raidEgg","raidBoss"};

        for (String formatKey : formatKeys) {
            Ini.Section format = formattingIni.get(formatKey);

            titleFormatting.put(formatKey,format.get("title"));
            titleUrl.put(formatKey,format.get("titleUrl"));
            bodyFormatting.put(formatKey,format.get("body"));

            if(formatKey.equals("pokemon")) {
                encounterBodyFormatting = format.get("encounteredBody");
            }

            showMap.put(formatKey,Boolean.parseBoolean(format.get("showMap")));

            mapZoom.put(formatKey,format.get("mapZoom"));
            mapWidth.put(formatKey,format.get("mapWidth"));
            mapHeight.put(formatKey,format.get("mapHeight"));
        }

        if(raidsEnabled()) {
            loadGeofenceChannels();
        }

        if(raidOrganisationEnabled){
            loadRaidChats();
        }

        if(!supporterOnly){
            loadSupporterRoles();
        }

//        loadPokeFilter();
//
//        loadPokemonChannels();
    }

    private void loadPokemonChannels() {
        if(!Geofencing.loaded) Geofencing.loadGeofences();

        File file = new File("pokechannels.txt");

        pokemonChannels = loadGeofencedChannels(file,pokemonChannels);
    }

    private void loadPokeFilter() {
        Gson g = new Gson();
        JsonParser parser = new JsonParser();

        try {
            JsonElement element = parser.parse(new FileReader("pokefilter.json"));

            if (element.isJsonObject()) {
                pokemonFilter = (JsonObject) element.getAsJsonObject().get("pokemon");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public JsonElement searchPokemonFilter(int id){
        return pokemonFilter.get(Util.capitaliseFirst(Pokemon.idToName(id)));
    }

    private void loadRaidChats() {
        if(!Geofencing.loaded) Geofencing.loadGeofences();

        File file = new File("raidchats.txt");

        raidChats = loadGeofencedChannels(file,raidChats);
    }

    private void loadSupporterRoles() {
        File file = new File("supporterlevels.txt");

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNext()) {
                String line = sc.nextLine().toLowerCase();

                String[] split = line.split("=");

                String roleId = split[0].trim();

                roleLimits.put(roleId,NotificationLimit.fromString(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getGeofenceChannelId(GeofenceIdentifier identifier){
        return geofencedChannelIds.get(identifier);
    }

    private HashMap<GeofenceIdentifier,String> loadGeofencedChannels(File file, HashMap<GeofenceIdentifier,String> map){
        try{
            Scanner sc = new Scanner(file);

            while(sc.hasNext()){
                String line = sc.nextLine().toLowerCase();

                String[] split = line.split("=");

                ArrayList<GeofenceIdentifier> geofenceIdentifiers = GeofenceIdentifier.fromString(split[0].trim());

                String channelId = split[1].trim();

                for (GeofenceIdentifier geofenceIdentifier : geofenceIdentifiers) {
                    map.put(geofenceIdentifier,channelId);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }

    private void loadGeofenceChannels() {

        if(!Geofencing.loaded) Geofencing.loadGeofences();

        File file = new File("raidalerts.txt");

        geofencedChannelIds = loadGeofencedChannels(file,geofencedChannelIds);

    }

    public String getTitleUrl(String formatKey) {
        return titleUrl.get(formatKey);
    }

    private ArrayList<String> loadKeys(File gkeys) {

        ArrayList<String> keys = new ArrayList<>();

        try {
            Scanner in = new Scanner(gkeys);

            while (in.hasNext()){
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
                    new File("gkeys.txt"),
                    new Ini(new File("formatting.ini")));

            for (int i = 1; i <= 250; i++) {
                System.out.println(i);
                    config.searchPokemonFilter(i);
            }
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

    public boolean nestsEnabled() {
        return nests;
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

    public String formatStr(HashMap<String,String> pokeProperties, String toFormat){
        final String[] str = {toFormat};

        pokeProperties.forEach((key, value) -> {
            str[0] = str[0].replace(String.format("<%s>", key),value);
        });

        return str[0];
    }

    public String getTitleFormatting(String formatKey) {
        return titleFormatting.get(formatKey);
    }

    public String getBodyFormatting(String formatKey) {
        return bodyFormatting.get(formatKey);
    }

    public String getEncounterBodyFormatting() {
        return encounterBodyFormatting;
    }

    public String getMapZoom(String formatKey) {
        return mapZoom.get(formatKey);
    }

    public String getMapWidth(String formatKey) {
        return mapWidth.get(formatKey);
    }

    public String getMapHeight(String formatKey) {
        return mapHeight.get(formatKey);
    }

    public boolean showMap(String formatKey) {
        return showMap.get(formatKey);
    }

    public String getAdminRole() {
        return adminRole;
    }

    public boolean useChannels() {
        return useChannels;
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
        return raidChannelsEnabled;
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
            if(notificationLimit != null){
                return notificationLimit;
            }
        }
        return null;
    }

    public ArrayList<GeofenceIdentifier> getRaidChatGeofences(String id) {
        ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();

        for (Map.Entry<GeofenceIdentifier, String> entry : raidChats.entrySet()) {
            if(entry.getValue().equals(id)){
                geofenceIdentifiers.add(entry.getKey());
            }
        }

        return geofenceIdentifiers;
    }

    public String raidChatsList() {
        String str = "";

        for (String s : geofencedChannelIds.values()) {
            str += String.format("  %s%n",guild.getTextChannelById(s).getAsMention());
        }

        return str;
    }

    public String[] getRaidChats(ArrayList<GeofenceIdentifier> geofences) {
        HashSet<String> chatIds = new HashSet<>();

        for (Map.Entry<GeofenceIdentifier, String> entry : raidChats.entrySet()) {
            boolean added = false;
            for (GeofenceIdentifier geofence : geofences) {
                if(added) break;
                if(entry.getKey().equals(geofence)){
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
            if(id.equals(s)) return true;
        }
        return false;
    }

    public boolean standardRaidTable() {
        return standardRaidTable;
    }

    public String getGoogleSuburbField(){
        return googleSuburbField;
    }

    public void loadEmotes() {
        for (String type : Raid.TYPES) {
            List<Emote> found = jda.getEmotesByName(type,true);
            if(found.size() == 0) try {
                guild.getController().createEmote(type, Icon.from(getClass().getResourceAsStream(type + ".png"))).queue(emote ->
                        emotes.put(type, emote));
            } catch (IOException e) {
                e.printStackTrace();
            }else{
                emotes.put(type,found.get(0));
            }
        }
    }

    public int getMinRaidLevel() {
        return minRaidLevel;
    }

    public String getPokeChannel(GeofenceIdentifier identifier) {
        return pokemonChannels.get(identifier);
    }
}
