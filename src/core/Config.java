package core;

import org.ini4j.Ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Owner on 13/05/2017.
 */
public class Config {

    Ini ini;

    ArrayList<String> GMAPS_KEYS = new ArrayList<>();

    ArrayList<Integer> blacklist = new ArrayList<>();
    private final ArrayList<String> supporterRoles;

    private String token;
    private boolean geofences;
    private boolean logging;
    private boolean nests;
    private final boolean supporterOnly;

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


    public Config(Ini configIni, File gkeys){
        this.ini = configIni;

        Ini.Section config = ini.get("config");

        token = config.get("token");

        String blacklistStr = config.get("blacklist");

        for (String s : parseList(blacklistStr)) {
            blacklist.add(Integer.valueOf(s));
        }

        geofences = Boolean.parseBoolean(config.get("geofences"));

        pollingRate = Long.parseLong(config.get("dbPollingRate"));

        nests = Boolean.parseBoolean(config.get("nests"));

        supporterOnly = Boolean.parseBoolean(config.get("supporterOnly"));

        supporterRoles = parseList(config.get("supporterRoles"));

        commandChannelId = config.get("commandChannel");

        logging = Boolean.parseBoolean(config.get("logging"));

        roleLogId = config.get("roleLogChannel");

        userUpdatesId = config.get("userUpdatesChannel");

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
    }

    private ArrayList<String> parseList(String strList){
        ArrayList<String> list = new ArrayList<>();

        String[] idStrings = strList.substring(1,strList.length()-1).split(",");

        for (String idString : idStrings) {
            list.add(idString.trim());
        }

        return list;
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
                    new File("gkeys.ini")
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

    private boolean useGeofences() {
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
}
