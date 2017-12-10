package core;

import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.ArrayList;

import static core.MessageListener.config;

public class Location
{
    private static final Location ALL = new Location(LocationType.All);
    public ArrayList<GeofenceIdentifier> geofenceIdentifiers;
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


    public Location(ArrayList<GeofenceIdentifier> identifiers) {
        this.locationType = LocationType.Geofence;
        this.geofenceIdentifiers = identifiers;
    }

    public Location(LocationType type) {
        this.locationType = type;
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
            case All:
                return "all";
        }

        return null;
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

        if(str.equalsIgnoreCase("all")) return Location.ALL;

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
        return toString();
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

    public String toDbString() {

        switch(locationType){
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
            case All:
                return "All";
        }

        return null;
    }
}
