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

import static core.MessageListener.config;

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
    private int cp;

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
        System.out.println(new PokeSpawn(12, -35.0, 149.0, new Time(1L), 1, 1, 1, "", "", 13.0f, 13.0f, 3, 1, 2142).hashCode());
        System.out.println(new PokeSpawn(12, -35.0, 149.0, new Time(214L), 1, 1, 1, "", "", 13.0f, 13.0f, 3, 1, 2142).hashCode());
    }

    public PokeSpawn(final int id, final double lat, final double lon, final Time disappearTime, final int attack, final int defense, final int stamina, final String move1, final String move2, final float weight, final float height, final int gender, final int form, int cp) {
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
        this.cp = cp;
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
        return ((this.region == null) ? "null" : this.region.toString()) + ": [" + ((this.suburb == null) ? "null" : this.suburb) + "]" + Pokemon.idToName(this.id) + " " + PokeSpawn.df.format(this.iv) + "%,CP: " + this.cp + ", " + this.move_1 + ", " + this.move_2 + ", for " + this.timeLeft() + ", disappears at " + this.disappearTime;
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
        embedBuilder.setColor(getColor());
        embedBuilder.setTitle("[" + this.getSuburb() + "] " + name + " " + ((this.form != null) ? ("[" + this.form + "] ") : ""), this.getGmapsLink());
        embedBuilder.setDescription(
                String.format("**Available until: %s (%s)**%n%n" +
                        "Lvl30+ IVs: %sA/%sD/%sS (%s%%)%n" +
                        "Lvl30+ CP: %s%n" +
                        "Lvl30+ Moveset: %s - %s%n" +
                        "Gender: %s, Height: %s, Weight: %s", disappearTime,timeLeft(),iv_attack,iv_defense,iv_stamina,getIv(),cp == 0 ? "?" : cp,move_1,move_2,getGender(),getHeight(),getWeight()));
        embedBuilder.setThumbnail(Pokemon.getIcon(this.id));
        embedBuilder.setImage(this.getImage());
        embedBuilder.setFooter("CBR Sightings", null);
        embedBuilder.setTimestamp(Instant.now());
        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
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
            return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=15&size=250x225&markers=color:red|%s,%s&key=%s", this.lat, this.lon, getNextKey());
        }
        return this.imageUrl;
    }

    private static String getNextKey() {
        if (PokeSpawn.lastKey == config.getKeys().size() - 1) {
            PokeSpawn.lastKey = 0;
            return config.getKeys().get(PokeSpawn.lastKey);
        }
        ++PokeSpawn.lastKey;
        return config.getKeys().get(PokeSpawn.lastKey);
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

    @Override
    public int hashCode() {
        int hash = (int) (lat * lon);

        hash *= Suburb.indexOf(suburb);

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
}
