package maps;

import java.util.ArrayList;

/**
 * Created by Owner on 19/05/2017.
 */
public class GeofenceIdentifier {

    String name;

    ArrayList<String> aliases;

    public GeofenceIdentifier(String name, ArrayList<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    @Override
    public String toString() {
        return name + ", Aliases: " + aliases;
    }
}
