package nests;

import core.*;
import pokemon.Pokemon;

public class Nest
{
    private final Region region;
    private final String name;
    private final String street;
    private final String suburb;
    private final NestType type;
    private final NestStatus status;
    public final Pokemon pokemon;
    private final String coordStr;

    public Nest(final Region region, final String name, final String street, final String suburb, final NestType type, final NestStatus status, final Pokemon poke, final String coordinates) {
        this.region = region;
        this.name = name;
        this.street = street;
        this.suburb = suburb;
        this.type = type;
        this.status = status;
        this.pokemon = poke;
        if (region == null || status == null || type == null || poke.name == null) {
            System.out.println("fak");
        }
        final int coordSplit = coordinates.indexOf(",");
        final String coord1 = coordinates.substring(0, coordSplit);
        final String coord2 = coordinates.substring(coordSplit + 1, coordinates.substring(coordSplit + 1).indexOf(",") + coordSplit + 1);
        this.coordStr = coord2 + "," + coord1;
    }

    @Override
    public String toString() {
        return this.status.toString() + " **" + this.pokemon.name + "** " + this.type.toString().toLowerCase() + " at " + this.name + " on " + this.street + ", " + this.suburb + ", in the " + this.region.toWords();
    }

    public String getGMapsLink() {
        return "https://www.google.com/maps?q=loc:" + this.coordStr;
    }
}
