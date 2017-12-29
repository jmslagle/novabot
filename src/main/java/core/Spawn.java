package core;

import Util.UtilityFunctions;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.entities.Message;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Spawn {
    protected static final DateTimeFormatter printFormat24hr = DateTimeFormatter.ofPattern("HH:mm:ss");
    protected static final DateTimeFormatter printFormat12hr = DateTimeFormatter.ofPattern("hh:mm:ss a");
    public static NovaBot novaBot = null;
    private static int lastKey;

    static {
        lastKey = 0;
    }

    public final HashMap<String, String> properties = new HashMap<>();
    protected final HashMap<String, Message> builtMessages = new HashMap<>();
    public int move_1;
    public int move_2;
    protected double lat;
    protected double lon;
    protected String formatKey = "pokemon";
    protected ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();
    private String imageUrl;

    public ArrayList<GeofenceIdentifier> getGeofences() {
        return geofenceIdentifiers;
    }

    public String getImage(String formatFile) {
        if (this.imageUrl == null) {
            return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=%s&size=%sx%s&markers=color:red|%s,%s&key=%s", novaBot.config.getMapZoom(formatFile, formatKey), novaBot.config.getMapWidth(formatFile, formatKey), novaBot.config.getMapHeight(formatFile, formatKey), this.lat, this.lon, getNextKey());
        }
        return this.imageUrl;
    }

    public String getSuburb() {
        return properties.get("city");
    }

    public static void main(String[] args) {
        System.out.println(printFormat24hr.format(UtilityFunctions.getCurrentTime(UtilityFunctions.UTC)));
    }

    protected String getAppleMapsLink() {
        return String.format("http://maps.apple.com/maps?daddr=%s,%s&z=10&t=s&dirflg=w", this.lat, this.lon);
    }

    protected String getGmapsLink() {
        return String.format("https://www.google.com/maps?q=loc:%s,%s", this.lat, this.lon);
    }

    public static void setNovaBot(NovaBot novaBot) {
        Spawn.novaBot = novaBot;
    }

    private static String getNextKey() {
        if (lastKey == novaBot.config.getKeys().size() - 1) {
            lastKey = 0;
            return novaBot.config.getKeys().get(lastKey);
        }
        ++lastKey;
        return novaBot.config.getKeys().get(lastKey);
    }

}
