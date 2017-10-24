package core;

import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashSet;

import static core.MessageListener.jda;

public class PokeChannel {

    public String channelId;

    private TextChannel channel = null;

    public String filterName;

    HashSet<GeofenceIdentifier> geofences = null;

    public PokeChannel (String channelId){
        this.channelId = channelId;
    }

    public PokeChannel (String channelId, String filterName){
        this(channelId);
        this.filterName = filterName;
    }

    public TextChannel getChannel() {
        if (channel == null){
            channel = jda.getTextChannelById(channelId);
        }

        return channel;
    }

}
