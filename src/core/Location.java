package core;

import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.ArrayList;

import static core.MessageListener.config;

public class Location
{
    private static final Location ALL = new Location(Region.All);
    public ArrayList<GeofenceIdentifier> geofenceIdentifiers;
    private Region region;
    public LocationType locationType;
    private String suburb;
    public final boolean usable = true;
//    public Reason reason;

    private Location(final String suburb) {
        this.locationType = LocationType.Suburb;
        this.suburb = suburb;
    }

    public Location(final GeofenceIdentifier geofenceIdentifier) {
        this.locationType = LocationType.Geofence;
        this.geofenceIdentifiers = new ArrayList<>();
        this.geofenceIdentifiers.add(geofenceIdentifier);
    }

    public Location(Region region) {
        this.locationType = LocationType.Region;
        this.region = region;
    }

    private Location() {

    }

    public Location(ArrayList<GeofenceIdentifier> identifiers) {
        this.locationType = LocationType.Geofence;
        this.geofenceIdentifiers = identifiers;
    }


    public String getSuburb() {
        return this.suburb;
    }

    @Override
    public String toString() {

        switch(locationType){
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
        }

        return this.region.toString();
    }

    public static String listToString(final Location[] locations) {
        String str = "";
        if (locations.length == 1) {
            return locations[0].toWords();
        }
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                str = str + "and " + locations[i].toWords();
            }
            else {
                str += ((i == locations.length - 2) ? (locations[i].toWords() + " ") : (locations[i].toWords() + ", "));
            }
        }
        return str;
    }

    public static Location fromString(final String str) {

        if(str.equalsIgnoreCase("all")) return new Location(Region.All);

        if(config.useGeofences()) {
            ArrayList<GeofenceIdentifier> identifiers = GeofenceIdentifier.fromString(str);

            if (identifiers.size() != 0) {
                return new Location(identifiers);
            }
        }

        if (MessageListener.suburbs.isSuburb(str)) {
            return new Location(str);
        }
        return null;
    }

    public String toWords() {
        switch(locationType){
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
            case Region:
                return this.region.toString().toLowerCase();
        }
        return "";
    }

    public static Location fromDbString(String str) {
        if(str.equals("all")) return Location.ALL;

        if(config.useGeofences()){
            ArrayList<GeofenceIdentifier> identifiers = GeofenceIdentifier.fromString(str);

            if(identifiers.size() !=  0){
                return new Location(identifiers);
            }
        }

        if (MessageListener.suburbs.isSuburb(str)) {
            return new Location(str);
        }
        if (str.equals("civic")) {
            return new Location("city");
        }
        MessageListener.novabotLog.log(SimpleLog.Level.WARNING,str + ", from db string is null");
        return null;
    }
//
//    private static Location UNUSABLE(Reason reason, String str, LocationType type) {
//        Location location = new Location();
//        location.usable = false;
//        location.locationType = type;
//        location.suburb = str;
//        location.reason = reason;
//        return location;
//    }

    public String toDbString() {

        switch(locationType){
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
        }

        return this.region.toString();
    }
}
