package core;

import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.entities.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static core.MessageListener.config;

public class Spawn {
    public final HashMap<String,String> properties = new HashMap<>();

    private String imageUrl;

    protected double lat;
    protected double lon;

    public String move_1;
    public String move_2;
    protected String formatKey = "pokemon";

    protected ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();

    private static int lastKey;

    protected final HashMap<String, Message> builtMessages = new HashMap<>();

    protected static final SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss");

    static {
        lastKey = 0;
    }

    public String getSuburb() {
        return properties.get("city");
    }

    public ArrayList<GeofenceIdentifier> getGeofences() {
        return geofenceIdentifiers;
    }

    public String getImage(String formatFile) {
        if (this.imageUrl == null) {
            return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=%s&size=%sx%s&markers=color:red|%s,%s&key=%s", config.getMapZoom(formatFile, formatKey), config.getMapWidth(formatFile, formatKey), config.getMapHeight(formatFile, formatKey), this.lat, this.lon, getNextKey());
        }
        return this.imageUrl;
    }

    private static String getNextKey() {
        if (lastKey == config.getKeys().size() - 1) {
            lastKey = 0;
            return config.getKeys().get(lastKey);
        }
        ++lastKey;
        return config.getKeys().get(lastKey);
    }

    protected String getAppleMapsLink() {
        return String.format("http://maps.apple.com/maps?daddr=%s,%s&z=10&t=s&dirflg=w", this.lat, this.lon);
    }

    protected String getGmapsLink() {
        return String.format("https://www.google.com/maps?q=loc:%s,%s", this.lat, this.lon);
    }
}
