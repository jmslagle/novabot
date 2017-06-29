package maps;

import java.util.ArrayList;

/**
 * Created by Owner on 19/05/2017.
 */
public class GeofenceIdentifier {

    public String name;

    ArrayList<String> aliases;

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
            str += geofenceIdentifiers.get(0).name;

            if(i != geofenceIdentifiers.size()-1){
                 str += ", ";
            }
        }
        return str;
    }

    public static GeofenceIdentifier fromString(String str) {

        if(Geofencing.geofencesMap.size() == 0){
            Geofencing.loadGeofences();
        }

        for (GeofenceIdentifier identifier : Geofencing.geofencesMap.keySet()) {
            if(str.equals(identifier.name) || identifier.aliases.contains(str)) return identifier;
        }

        return null;
    }
}
