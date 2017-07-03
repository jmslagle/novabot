package core;

import maps.GeofenceIdentifier;
import maps.Geofencing;

import java.util.ArrayList;

import static core.MessageListener.config;

public class Location
{
    private static final Location ALL = new Location(Region.All);
    private static final Location UNUSABLE = new Location();
    public ArrayList<GeofenceIdentifier> geofenceIdentifiers;
    private Region region;
    public LocationType locationType;
    private FeedChannel channel;
    private String suburb;
    public boolean usable = true;
    public Reason reason;

    public Location(final FeedChannel channel) {
        this.locationType = LocationType.Channel;
        this.channel = channel;
    }

    public Location(final String suburb) {
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

    public Location() {

    }

    public Location(ArrayList<GeofenceIdentifier> identifiers) {
        this.locationType = LocationType.Geofence;
        this.geofenceIdentifiers = identifiers;
    }

    public static void main(final String[] args) {
        FeedChannels.loadChannels();
        Geofencing.loadGeofences();
        System.out.println(new Location(FeedChannels.fromString("innernorth")).toWords());
    }

    public FeedChannel getFeedChannel() {
        return this.channel;
    }

    public String getSuburb() {
        return this.suburb;
    }

    @Override
    public String toString() {

        switch(locationType){
            case Channel:
                return this.channel.toString();
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

    public static Location fromString(final String str, boolean supporter) {

        if(str.equalsIgnoreCase("all")) return new Location(Region.All);

        if(config.useGeofences()) {
            ArrayList<GeofenceIdentifier> identifiers = GeofenceIdentifier.fromString(str);

            if (identifiers.size() != 0) {
                return new Location(identifiers);
            }
        }


        if(config.useChannels()){
            final FeedChannel channel = FeedChannels.fromString(str);

            if(!config.isSupporterOnly() && !supporter) {
                if (channel != null) {
                    return new Location(channel);
                }
            }else if(!config.isSupporterOnly() && supporter){
                if(channel != null){
                    return Location.UNUSABLE(Reason.SupporterAttemptedPublic,str,LocationType.Channel);
                }
            }

        }
        if (MessageListener.suburbs.isSuburb(str)) {
            return new Location(str);
        }
        return null;
    }

    public static Location fromDbString(final String str) {
        final FeedChannel channel = FeedChannels.fromDbString(str);
        if (channel != null) {
            return new Location(channel);
        }
        if (MessageListener.suburbs.isSuburb(str)) {
            return new Location(str);
        }
        if (str.equals("civic")) {
            return new Location("city");
        }
        System.out.println(str + ", from db string is null");
        return null;
    }

    public String toWords() {
        switch(locationType){
            case Suburb:
                return this.suburb;
            case Channel:
                return this.channel.getName();
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
            case Region:
                return this.region.toString().toLowerCase();
        }
        return "";
    }

    public static Location fromDbString(String str, boolean supporter) {
        if(str.equals("all")) return Location.ALL;

        if(config.useGeofences() && supporter){
            ArrayList<GeofenceIdentifier> identifiers = GeofenceIdentifier.fromString(str);

            if(identifiers.size() !=  0){
                return new Location(identifiers);
            }
        }

        if(config.useChannels()) {
            final FeedChannel channel = FeedChannels.fromString(str);
            if (channel != null) {
                return new Location(channel);
            }
        }

        if (MessageListener.suburbs.isSuburb(str)) {
            return new Location(str);
        }
        if (str.equals("civic")) {
            return new Location("city");
        }
        System.out.println(str + ", from db string is null");
        return null;
    }

    private static Location UNUSABLE(Reason reason, String str, LocationType type) {
        Location location = new Location();
        location.usable = false;
        location.locationType = type;
        location.suburb = str;
        location.reason = reason;
        return location;
    }

    public String toDbString() {

        switch(locationType){
            case Channel:
                return this.channel.aliases.get(0);
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
        }

        return this.region.toString();
    }
}
