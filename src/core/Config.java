package core;

import org.ini4j.Ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Owner on 13/05/2017.
 */
public class Config {

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

    private String timeZone;

    private String footerText;

    private String adminRole;

    private String commandChannelId;
    private String roleLogId;
    private String userUpdatesId;

    private long pollingRate;

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

    private String bodyFormatting;
    private String titleFormatting;
    private String titleUrl;

    private String mapZoom = "15";
    private String mapWidth = "255";
    private String mapHeight = "225";

    private boolean showMap = true;


    public Config(Ini configIni, File gkeys, Ini formattingIni){
        this.ini = configIni;

        Ini.Section config = ini.get("config");

        token = config.get("token");

        String blacklistStr = config.get("blacklist");

        for (String s : Util.parseList(blacklistStr)) {
            blacklist.add(Integer.valueOf(s));
        }

        geofences = Boolean.parseBoolean(config.get("geofences"));

        pollingRate = Long.parseLong(config.get("dbPollingRate"));

        nests = Boolean.parseBoolean(config.get("nests"));

        supporterOnly = Boolean.parseBoolean(config.get("supporterOnly"));

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

        Ini.Section formatting = formattingIni.get("formatting");
        titleFormatting = formatting.get("title");
        titleUrl = formatting.get("titleUrl");
        bodyFormatting = formatting.get("body");

        showMap = Boolean.parseBoolean(formatting.get("showMap"));

        mapZoom = formatting.get("mapZoom");
        mapWidth = formatting.get("mapWidth");
        mapHeight = formatting.get("mapHeight");
    }

    public String getTitleUrl() {
        return titleUrl;
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
                    new Ini(new File("config.example.ini")),
                    new File("gkeys.txt"),
                    new Ini(new File("formatting.ini"))
            );

            System.out.println(config.getToken());
            System.out.println(config.useGeofences());
            System.out.println(config.getRmUser());
            System.out.println(config.getBlacklist());
            System.out.println(config.getKeys());
            System.out.println(config.getSupporterRoles());
            System.out.println(config.isSupporterOnly());
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

    public long getPollingRate() {
        return pollingRate;
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

    public String getTitleFormatting() {
        return titleFormatting;
    }

    public String getBodyFormatting() {
        return bodyFormatting;
    }

    public String getMapZoom() {
        return mapZoom;
    }

    public String getMapWidth() {
        return mapWidth;
    }

    public String getMapHeight() {
        return mapHeight;
    }

    public boolean showMap() {
        return showMap;
    }

    public String getAdminRole() {
        return adminRole;
    }
}
