package core;

public class Location
{
    private boolean isSuburb;
    private Region region;
    private String suburb;

    public Location(final Region region) {
        this.isSuburb = false;
        this.region = region;
    }

    private Location(final String suburb) {
        this.isSuburb = false;
        this.suburb = suburb;
        this.isSuburb = true;
    }

    public static void main(final String[] args) {
        System.out.println(new Location(Region.BelconnenRegion).toWords());
    }

    public Region getRegion() {
        return this.region;
    }

    public String getSuburb() {
        return this.suburb;
    }

    @Override
    public String toString() {
        if (this.isSuburb) {
            return this.suburb;
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
        final Region region = Region.fromString(str);
        if (region != null) {
            return new Location(region);
        }
        if (Suburb.isSuburb(str)) {
            return new Location(str);
        }
        return null;
    }

    public static Location fromDbString(final String str) {
        final Region region = Region.fromDbString(str);
        if (region != null) {
            return new Location(region);
        }
        if (Suburb.isSuburb(str)) {
            return new Location(str);
        }
        if (str.equals("civic")) {
            return new Location("city");
        }
        System.out.println(str + ", from db string is null");
        return null;
    }

    public String toWords() {
        if (this.isSuburb) {
            return this.suburb;
        }
        return this.region.toWords();
    }
}
