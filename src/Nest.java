/**
 * Created by Paris on 28/03/2017.
 */
public class Nest {

    Region region;
    String name;
    String street;
    String suburb;
    NestType type;
    NestStatus status;
    Pokemon pokemon;
    String coordStr;

    public Nest(Region region, String name, String street, String suburb, NestType type, NestStatus status, Pokemon poke, String coordinates) {
        this.region = region;
        this.name = name;
        this.street = street;
        this.suburb = suburb;
        this.type = type;
        this.status = status;
        this.pokemon = poke;

        if(region == null || status == null || type == null || poke.name == null){
            System.out.println("fak");
        }

        int coordSplit = coordinates.indexOf(",");
        String coord1 = coordinates.substring(0,coordSplit);
        String coord2 = coordinates.substring(coordSplit+1,coordinates.substring(coordSplit+1).indexOf(",") + coordSplit+1) ;

        this.coordStr = coord2+","+coord1;
    }

    @Override
    public String toString() {
        return status.toString() + " **" + pokemon.name + "** " + type.toString().toLowerCase() + " at " + name + " on " + street + ", " + suburb + ", in the " + region.toString() + " region";
    }

    public String getGMapsLink(){
//        return "https://www.google.com/maps/d/u/0/viewer?mid=1d-QuaDK1tJRiHKODXErTQIDqIAY&ll="+coordStr;
        return "https://www.google.com/maps?q=loc:"+coordStr;
    }
}
