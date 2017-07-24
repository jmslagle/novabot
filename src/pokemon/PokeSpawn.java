package pokemon;

import core.DBManager;
import core.FeedChannel;
import core.Region;
import core.Util;
import maps.GeofenceIdentifier;
import maps.ReverseGeocoder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static core.MessageListener.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static maps.Geofencing.getGeofence;
import static maps.Geofencing.loadGeofences;

public class PokeSpawn
{
    private static final String STATIC_MAPS_BASE = "https://maps.googleapis.com/maps/api/staticmap?";
    public FeedChannel feedChannel;
    private int level;
    private String imageUrl;
    public Timestamp disappearTime;
    private double lat;
    private double lon;
    private float weight;
    private float height;
    private int gender;
    public String form;
    public int id;
    private String suburb;
    public Region region;
    private ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();
    public float iv;
    private String move_1;
    private String move_2;
    private static final DecimalFormat df;
    private int iv_attack;
    private int iv_defense;
    private int iv_stamina;
    private static int lastKey;
    private int cp;

    public HashMap<String,String> pokeProperties = new HashMap<>();

    public static final SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss");

    public PokeSpawn(final int id, final String suburb, final Region region, final float iv, final String move_1, final String move_2) {
        this.imageUrl = null;
        this.disappearTime = null;
        this.form = null;
        this.suburb = null;
        this.region = null;
        this.id = id;
        this.suburb = suburb;
        this.region = region;
        this.iv = iv;
        this.move_1 = move_1;
        this.move_2 = move_2;
    }


    public static void main(final String[] args) {
        System.out.println(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Australia/Adelaide")));

        testing = true;
        loadConfig();
        loadGeofences();
        System.out.println(config.getNbIp());
        System.out.println(config.getNbUser());

        DBManager.novabotdbConnect();

//        PokeSpawn spawn = new PokeSpawn(149, -35.214385, 149.0405493, new Timestamp(DBManager.getCurrentTime().getTime() + 6000), 1, 1, 1, "Dragon Tail", "Outrage", 13.0f, 13.0f, 3, 0, 2142,0.743 );
        PokeSpawn spawn = new PokeSpawn(149,
                -35.214385,
                149.0405493,
                new Timestamp(DBManager.getCurrentTime().getTime() + 6000),
                0,
                0,
                0,
                "unkn",
                "unkn",
                0,
                0,
                0,
                0,
                0,
                0);

        System.out.println(spawn.pokeProperties.get("geofence"));
        System.out.println(spawn.encountered());
//        System.out.println(config.formatStr(spawn.pokeProperties,config.getTitleFormatting()));
//        if(spawn.encountered()){
//            System.out.println(config.formatStr(spawn.pokeProperties,config.getEncounterBodyFormatting()));
//        }else {
//            System.out.println(config.formatStr(spawn.pokeProperties, config.getBodyFormatting()));
//        }
//        System.out.println(new PokeSpawn(12, -35.0, 149.0, new Time(1L), 1, 1, 1, "", "", 13.0f, 13.0f, 3, 1, 2142,0.743 ).hashCode());
//        System.out.println(new PokeSpawn(12, -35.0, 149.0, new Time(214L), 1, 1, 1, "", "", 13.0f, 13.0f, 3, 1, 2142,.743 ).hashCode());
    }

    public PokeSpawn(int id, FeedChannel feedChannel, String suburb, float pokeIV, String move_1, String move_2, String form, int cp) {
        this.imageUrl = null;
        this.disappearTime = null;
        this.form = null;
        this.suburb = null;
        this.feedChannel = feedChannel;
        this.id = id;
        this.suburb = suburb;
        this.pokeProperties.put("city",suburb);
        this.iv = pokeIV;
        this.move_2 = move_2;
        this.move_1 = move_1;
        this.form = form;
        this.cp = cp;
    }


    public PokeSpawn(final int id, final double lat, final double lon, final Timestamp disappearTime, final int attack, final int defense, final int stamina, final String move1, final String move2, final float weight, final float height, final int gender, final int form, int cp, double cpModifier) {
        this.imageUrl = null;
        this.disappearTime = null;
        this.form = null;
        this.suburb = null;
        this.region = null;
        this.disappearTime = disappearTime;
        pokeProperties.put("24h_time",getDespawnTime());
        pokeProperties.put("time_left",timeLeft());

        this.id = id;
        pokeProperties.put("pkmn_id", String.valueOf(id));


        String name = Util.capitaliseFirst(Pokemon.idToName(this.id));
        if (name.startsWith("Unown")) {
            name = "Unown";
        }

        pokeProperties.put("pkmn",name);

        this.lat = lat;
        pokeProperties.put("lat", String.valueOf(lat));

        this.lon = lon;
        pokeProperties.put("lng", String.valueOf(lon));

        ReverseGeocoder.geocodedLocation(lat,lon).getProperties().forEach((key,value)->{
            pokeProperties.put(key,value);
        });

        this.geofenceIdentifiers = getGeofence(lat,lon);

        pokeProperties.put("geofence", GeofenceIdentifier.listToString(geofenceIdentifiers));

        pokeProperties.put("gmaps",getGmapsLink());

        pokeProperties.put("applemaps",getAppleMapsLink());

        this.iv_attack = attack;
        pokeProperties.put("atk", String.valueOf(iv_attack));

        this.iv_defense = defense;
        pokeProperties.put("def", String.valueOf(iv_defense));

        this.iv_stamina = stamina;
        pokeProperties.put("sta", String.valueOf(iv_stamina));

        this.iv = (attack + defense + stamina) / 45.0f * 100.0f;
        pokeProperties.put("iv", getIv());

        this.move_1 = ((move1 == null) ? "unkn" : move1);
        pokeProperties.put("quick_move", move_1);

        this.move_2 = ((move2 == null) ? "unkn" : move2);
        pokeProperties.put("charge_move", move_2);

        this.weight = weight;
        pokeProperties.put("weight", getWeight());

        this.height = height;
        pokeProperties.put("height", getHeight());

        this.gender = gender;
        pokeProperties.put("gender", getGender());

        if (form != 0 && id == 201) {
            this.id = id * 10 + form;
        }
        this.form = ((Pokemon.intToForm(form) == null) ? null : String.valueOf(Pokemon.intToForm(form)));
        pokeProperties.put("form", (this.form == null ? "" : this.form));

        this.cp = cp;
        pokeProperties.put("cp", cp == 0 ? "?" : String.valueOf(cp));

        this.level = getLevel(cpModifier);
        pokeProperties.put("level", String.valueOf(level));
    }

    public PokeSpawn(final int id, final String suburb, final float pokeIV, final String move_1, final String move_2, final String form, int cp) {
        this.imageUrl = null;
        this.disappearTime = null;
        this.form = null;
        this.suburb = null;
        this.region = null;
        this.id = id;
        this.suburb = suburb;
        this.iv = pokeIV;
        this.move_2 = move_2;
        this.move_1 = move_1;
        this.form = form;
        this.cp = cp;
    }

    public static int getLevel(double cpModifier){
        double unRoundedLevel;

        if(cpModifier < 0.734){
            unRoundedLevel = (58.35178527 * cpModifier * cpModifier - 2.838007664 * cpModifier + 0.8539209906);
        }else{
            unRoundedLevel = 171.0112688 * cpModifier - 95.20425243;
        }

        return (int) Math.round(unRoundedLevel);
    }

    public String getSuburb() {

        return pokeProperties.get("city");

//        if (this.suburb != null) {
//            return this.suburb;
//        }
//        final String foundSuburb = ReverseGeocoder.getSuburb(this.lat, this.lon);
//        if (foundSuburb.equals("")) {
//            return this.suburb = "Unknown";
//        }
//        return this.suburb = ReverseGeocoder.getSuburb(this.lat, this.lon);
    }

//    public Region getRegion() {
//        if (this.region == null) {
//            return this.region = Geofencing.getRegion(this.lat, this.lon);
//        }
//        return this.region;
//    }

    @Override
    public String toString() {
        if (this.disappearTime == null) {
            return ((this.region == null) ? "null" : this.region.toString()) + ": [" + ((this.suburb == null) ? "null" : this.suburb) + "]" + Pokemon.idToName(this.id) + " " + PokeSpawn.df.format(this.iv) + "%, " + this.move_1 + ", " + this.move_2;
        }
        return ((this.region == null) ? "null" : this.region.toString()) + ": [" + ((this.suburb == null) ? "null" : this.suburb) + "]" + Pokemon.idToName(this.id) + " " + PokeSpawn.df.format(this.iv) + "%,CP: " + this.cp + ", " + this.move_1 + ", " + this.move_2 + ", for " + this.timeLeft() + ", disappears at " + this.disappearTime;
    }

    private String timeLeft() {
//        ZonedDateTime.of(LocalDate.of, ZoneId.of("Australia/Canberra"))

        Timestamp currentTime = DBManager.getCurrentTime();
//
//        System.out.println(disappearTime);
//        System.out.println(currentTime);
//
//        System.out.println(disappearTime.getTime() - currentTime.getTime());

        long diff = this.disappearTime.getTime() - currentTime.getTime();

        String time = String.format("%02dm %02ds",
                MILLISECONDS.toMinutes(Math.abs(diff)),
                MILLISECONDS.toSeconds(Math.abs(diff)) -
                        (MILLISECONDS.toMinutes(Math.abs(diff)) * 60)
        );

        if(diff < 0){
            time = "-" + time;
        }

        return time;
    }

    public Message buildMessage() {
        final MessageBuilder messageBuilder = new MessageBuilder();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(getColor());
        embedBuilder.setTitle(config.formatStr(pokeProperties,config.getTitleFormatting("pokemon")),config.formatStr(pokeProperties,config.getTitleUrl("pokemon")));
        embedBuilder.setDescription(config.formatStr(pokeProperties,(encountered()) ? config.getEncounterBodyFormatting() : config.getBodyFormatting("pokemon")));
        embedBuilder.setThumbnail(Pokemon.getIcon(this.id));
        if(config.showMap("pokemon")) {
            embedBuilder.setImage(this.getImage());
        }
        embedBuilder.setFooter(config.getFooterText(), null);
        embedBuilder.setTimestamp(Instant.now());
        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
    }

    private boolean encountered() {

        return iv != 0 || !move_1.equals("unkn") || !move_2.equals("unkn") || cp > 0;

    }

    private Color getColor() {
        if(iv < 25)
            return new Color(0x9d9d9d);
        if(iv < 50)
            return new Color(0xffffff);
        if(iv < 81)
            return new Color(0x1eff00);
        if(iv < 90)
            return new Color(0x0070dd);
        if(iv < 100)
            return new Color(0xa335ee);
        if(iv == 100)
            return new Color(0xff8000);

        return Color.GRAY;
    }

    private String getGender() {
        if (this.gender == 1) {
            return "\u2642";
        }
        if (this.gender == 2) {
            return "\u2640";
        }
        if (this.gender == 3) {
            return "\u26b2";
        }
        return "?";
    }

    private String getHeight() {
        return PokeSpawn.df.format(this.height);
    }

    private String getWeight() {
        return PokeSpawn.df.format(this.weight);
    }

    private String getImage() {

        if (this.imageUrl == null) {
            return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=%s&size=%sx%s&markers=color:red|%s,%s&key=%s", config.getMapZoom("pokemon"), config.getMapWidth("pokemon"), config.getMapHeight("pokemon"), this.lat, this.lon, getNextKey());
        }
        return this.imageUrl;
    }

    public static String getNextKey() {
        if (PokeSpawn.lastKey == config.getKeys().size() - 1) {
            PokeSpawn.lastKey = 0;
            return config.getKeys().get(PokeSpawn.lastKey);
        }
        ++PokeSpawn.lastKey;
        return config.getKeys().get(PokeSpawn.lastKey);
    }

    private String getGmapsLink() {
        return String.format("https://www.google.com/maps?q=loc:%s,%s", this.lat, this.lon);
    }

    private String getIv() {
        return PokeSpawn.df.format(this.iv);
    }

    static {
        (df = new DecimalFormat("#.##")).setRoundingMode(RoundingMode.CEILING);
        PokeSpawn.lastKey = 0;
    }

    @Override
    public int hashCode() {
        int hash = (int) (lat * lon);

        hash *= suburbs.indexOf(suburb);

        hash *= id;

        hash += iv * 1000;

        hash *= gender + 1;

        hash += (weight * height);

        hash += PokeMove.indexOf(move_1) * PokeMove.indexOf(move_2);

        hash += (iv_attack + iv_defense + iv_stamina);

        hash += cp;

        hash += disappearTime.hashCode();

        return hash;
    }

    public String getDespawnTime() {
        return printFormat.format(disappearTime);
    }

    public String getAppleMapsLink() {
        return String.format("http://maps.apple.com/maps?daddr=%s,%s&z=10&t=s&dirflg=w", this.lat, this.lon);
    }

    public ArrayList<GeofenceIdentifier> getGeofenceIds() {
        return geofenceIdentifiers;
    }
}
