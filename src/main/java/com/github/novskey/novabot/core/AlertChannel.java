package com.github.novskey.novabot.core;

import com.github.novskey.novabot.maps.GeofenceIdentifier;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashSet;

public class AlertChannel {

    public final String channelId;

    private TextChannel channel = null;

    public String filterName;

    HashSet<GeofenceIdentifier> geofences = null;
    public String formattingName;

    public AlertChannel(String channelId) {
        this.channelId = channelId;
    }

    public AlertChannel(String channelId, String filterName) {
        this(channelId);
        this.filterName = filterName;
    }

    public TextChannel getChannel(JDA jda) {
        if (channel == null) {
            channel = jda.getTextChannelById(channelId);
        }

        return channel;
    }

}
