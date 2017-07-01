package core;

import maps.GeofenceIdentifier;
import maps.ReverseGeocoder;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static core.MessageListener.*;
import static core.PokeSpawn.getNextKey;
import static core.PokeSpawn.printFormat;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static maps.Geofencing.getGeofence;
import static maps.Geofencing.loadGeofences;

/**
 * Created by Owner on 27/06/2017.
 */
public class RaidSpawn {

    static final String NORMAL_EGG = "https://raw.githubusercontent.com/ZeChrales/PogoAssets/master/static_assets/png/ic_raid_egg_normal.png";
    static final String RARE_EGG = "https://raw.githubusercontent.com/ZeChrales/PogoAssets/master/static_assets/png/ic_raid_egg_rare.png";

    final String name;
    final double lat;
    final double lon;
    final Timestamp raidEnd;
    final Timestamp battleStart;
    public final int bossId;
    final int bossCp;
    final String move_1;
    final String move_2;
    final int raidLevel;
    final String gymId;
    private final ArrayList<GeofenceIdentifier> geofenceIdentifiers;

    public HashMap<String,String> properties = new HashMap<>();
    private String imageUrl;
    private String formatKey;
    private Message builtMessage = null;

    public static void main(String[] args) {

        testing = true;
        loadConfig();
        loadGeofences();
        DBManager.novabotdbConnect();
        RaidSpawn spawn = new RaidSpawn("gym",
                "123",-35.34200996278955,149.05508042811897,
                new Timestamp(DBManager.getCurrentTime().getTime() + 504000),
                new Timestamp(DBManager.getCurrentTime().getTime() + 6000000),
                6,
                11003,
                "fire",
                "fire blast",
                2);

//        Message message = spawn.buildMessage();
//        System.out.println(message.getEmbeds().get(0).getTitle());
//        System.out.println(message.getEmbeds().get(0).getDescription());


        System.out.println(spawn.properties.get("geofence"));
        JDA jda = null;
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setAutoReconnect(true)
                    .setGame(Game.of("Pokemon Go"))
                    .setToken(config.getToken())
                    .buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }

        for (GeofenceIdentifier identifier : spawn.getGeofences()) {
            String id = config.getGeofenceChannelId(identifier);

            if(id != null){
                jda.getTextChannelById(id).sendMessage(spawn.buildMessage()).queue();
            }
        }
    }

    public RaidSpawn(String name, String gymId, double lat, double lon, Timestamp raidEnd, Timestamp battleStart, int bossId, int bossCp,String move_1, String move_2, int raidLevel) {
        this.name = name;
        properties.put("gym_name",name);

        this.gymId = gymId;

        this.lat = lat;
        this.lon = lon;

        this.geofenceIdentifiers = getGeofence(lat,lon);

        properties.put("geofence", GeofenceIdentifier.listToString(geofenceIdentifiers));

        properties.put("gmaps",getGmapsLink());
        properties.put("applemaps",getAppleMapsLink());

        ReverseGeocoder.geocodedLocation(lat,lon).getProperties().forEach((key,value)->{
            properties.put(key,value);
        });

        this.raidEnd = raidEnd;
        properties.put("24h_end",getDisappearTime());
        properties.put("time_left",timeLeft(raidEnd));

        this.battleStart = battleStart;
        properties.put("24h_start",getStartTime());
        properties.put("time_left_start",timeLeft(battleStart));


        this.bossId = bossId;
        this.bossCp = bossCp;
        this.move_1 = move_1;
        this.move_2 = move_2;
        if(bossId != 0) {
            properties.put("pkmn", Util.capitaliseFirst(Pokemon.idToName(bossId)));
            properties.put("cp", String.valueOf(bossCp));
            properties.put("quick_move",move_1);
            properties.put("charge_move",move_2);
        }

        this.raidLevel = raidLevel;
        properties.put("level", String.valueOf(raidLevel));

    }

    private String getAppleMapsLink() {
        return String.format("http://maps.apple.com/maps?daddr=%s,%s&z=10&t=s&dirflg=w", this.lat, this.lon);
    }

    private String getGmapsLink() {
        return String.format("https://www.google.com/maps?q=loc:%s,%s", this.lat, this.lon);
    }

    private String timeLeft(Timestamp untilTime) {
        Timestamp currentTime = DBManager.getCurrentTime();

        long diff = untilTime.getTime() - currentTime.getTime();

        String time;
        if(MILLISECONDS.toHours(diff) > 0){
            time = String.format("%02d:%02d:%02d",MILLISECONDS.toHours(Math.abs(diff)),
                    MILLISECONDS.toMinutes(Math.abs(diff)) -
                            (MILLISECONDS.toHours(Math.abs(diff)) * 60),
                    MILLISECONDS.toSeconds(Math.abs(diff)) -
                            MILLISECONDS.toMinutes(Math.abs(diff) * 60)
            );
        }else {
            time = String.format("%02d:%02d",
                    MILLISECONDS.toMinutes(Math.abs(diff)),
                    MILLISECONDS.toSeconds(Math.abs(diff)) -
                            (MILLISECONDS.toMinutes(Math.abs(diff)) * 60)
            );
        }

        if(diff < 0){
            time = "-" + time;
        }

        return time;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", name,gymId,lat,lon,raidEnd,battleStart,bossId,bossCp,raidLevel,move_1,move_2);
    }

    public Message buildMessage() {
        if(builtMessage != null) return builtMessage;

        final MessageBuilder messageBuilder = new MessageBuilder();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(getColor());

        if(bossId == 0) {
            formatKey = "raidEgg";
            embedBuilder.setTitle(config.formatStr(properties,config.getTitleFormatting(formatKey)),config.formatStr(properties,config.getTitleUrl(formatKey)));
            embedBuilder.setDescription(config.formatStr(properties,config.getBodyFormatting(formatKey)));
        }else{
            formatKey = "raidBoss";
            embedBuilder.setTitle(config.formatStr(properties,config.getTitleFormatting(formatKey)),config.formatStr(properties,config.getTitleUrl(formatKey)));
            embedBuilder.setDescription(config.formatStr(properties,config.getBodyFormatting(formatKey)));
        }
        embedBuilder.setThumbnail(getIcon());
        if (config.showMap(formatKey)) {
            embedBuilder.setImage(getImage());
        }
        embedBuilder.setFooter(config.getFooterText(), null);
        embedBuilder.setTimestamp(Instant.now());
        messageBuilder.setEmbed(embedBuilder.build());

        this.builtMessage = messageBuilder.build();
        return builtMessage;
    }

    private String getStartTime() {
        return printFormat.format(battleStart);
    }

    private String getDisappearTime() {
        return printFormat.format(raidEnd);
    }

    private String getImage() {
        if (this.imageUrl == null) {
            return this.imageUrl = "https://maps.googleapis.com/maps/api/staticmap?" + String.format("zoom=%s&size=%sx%s&markers=color:red|%s,%s&key=%s", config.getMapZoom(formatKey), config.getMapWidth(formatKey), config.getMapHeight(formatKey), this.lat, this.lon, getNextKey());
        }
        return this.imageUrl;
    }

    public Color getColor() {
        switch(raidLevel) {
            case 1:
                return new Color(0x9d9d9d);
            case 2:
                return new Color(0xdb3b78);
            case 3:
                return new Color(0xff8000);
            case 4:
                return new Color(0xffe100);
        }
        return Color.WHITE;
    }

    public String getIcon() {
        if(bossId == 0){
            switch(raidLevel){
                case 1:
                case 2:
                    return NORMAL_EGG;
                case 3:
                case 4:
                    return RARE_EGG;
            }
        }
        return Pokemon.getIcon(bossId);
    }

    public ArrayList<GeofenceIdentifier> getGeofences() {
        return geofenceIdentifiers;
    }

    public ArrayList<GeofenceIdentifier> getGeofenceIds() {
        return geofenceIdentifiers;
    }

    public String getSuburb() {
        return properties.get("city");
    }
}
