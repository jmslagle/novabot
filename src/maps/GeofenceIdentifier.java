package maps;

import java.util.ArrayList;

import static core.MessageListener.config;

/**
 * Created by Owner on 19/05/2017.
 */
public class GeofenceIdentifier {

    public final String name;

    private final ArrayList<String> aliases;

    public GeofenceIdentifier(String name, ArrayList<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return name + ", Aliases: " + aliases;
    }

    public static String listToString(ArrayList<GeofenceIdentifier> geofenceIdentifiers) {
        String str = "";

        for (int i = 0; i < geofenceIdentifiers.size(); i++) {
            str += geofenceIdentifiers.get(i).name;

            if(i != geofenceIdentifiers.size()-1){
                 str += ", ";
            }
        }
        return str;
    }

    public static ArrayList<GeofenceIdentifier> fromString(String str) {

        if(Geofencing.geofencesMap.size() == 0 && config.useGeofences()){
            Geofencing.loadGeofences();
        }

        ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();

        if(str.equalsIgnoreCase("none")){
            geofenceIdentifiers.add(null);
            return geofenceIdentifiers;
        }

        for (GeofenceIdentifier identifier : Geofencing.geofencesMap.keySet()) {
            if(str.equals(identifier.name) || identifier.aliases.contains(str)) geofenceIdentifiers.add(identifier);
        }

        return geofenceIdentifiers;
    }

    public String getAliasList() {
        String str = "";

        for (int i = 0; i < aliases.size(); i++) {
            str += aliases.get(i);
            if(i != aliases.size() - 1){
                str += ", ";
            }
        }
        return str;
    }
}
