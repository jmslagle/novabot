package core;

import maps.Geofencing;
import maps.ReverseGeocoder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class PokeSpawn
{
    private static final String STATIC_MAPS_BASE = "https://maps.googleapis.com/maps/api/staticmap?";
    private String imageUrl;
    private Time disappearTime;
    private double lat;
    private double lon;
    private float weight;
    private float height;
    private int gender;
    public String form;
    public int id;
    private String suburb;
    Region region;
    float iv;
    private String move_1;
    private String move_2;
    private static final DecimalFormat df;
    private int iv_attack;
    private int iv_defense;
    private int iv_stamina;
    private static int lastKey;

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
        System.out.println(new PokeSpawn(12, 12.0, 12.0, new Time(1L), 1, 1, 1, "", "", 13.0f, 13.0f, 3, 1).getGender());
    }

    public PokeSpawn(final int id, final double lat, final double lon, final Time disappearTime, final int attack, final int defense, final int stamina, final String move1, final String move2, final float weight, final float height, final int gender, final int form) {
        this.imageUrl = null;
        this.disappearTime = null;
        this.form = null;
        this.suburb = null;
        this.region = null;
        this.disappearTime = disappearTime;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        if (disappearTime == null) {
            System.out.println("remaining time is null");
        }
        this.iv_attack = attack;
        this.iv_defense = defense;
        this.iv_stamina = stamina;
        this.iv = (attack + defense + stamina) / 45.0f * 100.0f;
        this.move_1 = ((move1 == null) ? "unkn" : move1);
        this.move_2 = ((move2 == null) ? "unkn" : move2);
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        if (form != 0 && id == 201) {
            this.id = id * 10 + form;
        }
        this.form = ((Pokemon.intToForm(form) == null) ? null : String.valueOf(Pokemon.intToForm(form)));
    }

    public PokeSpawn(final int id, final String suburb, final float pokeIV, final String move_1, final String move_2, final String form) {
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
    }

    public String getSuburb() {
        if (this.suburb != null) {
            return this.suburb;
        }
        final String foundSuburb = ReverseGeocoder.getSuburb(this.lat, this.lon);
        if (foundSuburb.equals("")) {
            return this.suburb = "Unknown";
        }
        return this.suburb = ReverseGeocoder.getSuburb(this.lat, this.lon);
    }

    public Region getRegion() {
        if (this.region == null) {
            return this.region = Geofencing.getRegion(this.lat, this.lon);
        }
        return this.region;
    }

    @Override
    public String toString() {
        if (this.disappearTime == null) {
            return ((this.region == null) ? "null" : this.region.toString()) + ": [" + ((this.suburb == null) ? "null" : this.suburb) + "]" + Pokemon.idToName(this.id) + " " + PokeSpawn.df.format(this.iv) + "%, " + this.move_1 + ", " + this.move_2;
        }
        return ((this.region == null) ? "null" : this.region.toString()) + ": [" + ((this.suburb == null) ? "null" : this.suburb) + "]" + Pokemon.idToName(this.id) + " " + PokeSpawn.df.format(this.iv) + "%, " + this.move_1 + ", " + this.move_2 + ", for " + this.timeLeft() + ", disappears at " + this.disappearTime;
    }

    private String timeLeft() {
        final long diff = this.disappearTime.getTime() - DBManager.getCurrentTime().getTime();
        final SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(diff));
    }

    public Message buildMessage() {
        final MessageBuilder messageBuilder = new MessageBuilder();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        String name = Util.capitaliseFirst(Pokemon.idToName(this.id));
        if (name.startsWith("Unown")) {
            name = "Unown";
        }
        embedBuilder.setColor(Color.gray);
        embedBuilder.setTitle("[" + this.getSuburb() + "] " + name + " " + ((this.form != null) ? ("[" + this.form + "] ") : ""), this.getGmapsLink());
        embedBuilder.setDescription("**Available until: " + this.disappearTime + " (" + this.timeLeft() + ")**\n\nDue to recent changes from Niantic, IV and moveset etc are no longer accurate");
        embedBuilder.setThumbnail(Pokemon.getIcon(this.id));
        embedBuilder.setImage(this.getImage());
        embedBuilder.setFooter("CBR Sightings", "https://google.com");
        embedBuilder.setTimestamp(Instant.now());
        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
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
            return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=15&size=250x225&markers=color:red|%s,%s&key=%s", this.lat, this.lon, getNextKey());
        }
        return this.imageUrl;
    }

    private static String getNextKey() {
        if (PokeSpawn.lastKey == ReverseGeocoder.GMAPS_KEYS.size() - 1) {
            PokeSpawn.lastKey = 0;
            return ReverseGeocoder.GMAPS_KEYS.get(PokeSpawn.lastKey);
        }
        ++PokeSpawn.lastKey;
        return ReverseGeocoder.GMAPS_KEYS.get(PokeSpawn.lastKey);
    }

    private String getGmapsLink() {
        return "https://www.google.com/maps?q=loc:" + this.lat + "," + this.lon;
    }

    private String getIv() {
        return PokeSpawn.df.format(this.iv);
    }

    static {
        (df = new DecimalFormat("#.##")).setRoundingMode(RoundingMode.CEILING);
        PokeSpawn.lastKey = 0;
    }
}
