package core;

import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;

class FeedChannel
{
    public String channelId;
    public TextChannel channel = null;
    public ArrayList<String> aliases;

    public FeedChannel(final TextChannel channel, final ArrayList<String> aliases) {
        this.channel = channel;
        this.aliases = aliases;
    }

    public FeedChannel(String id, ArrayList<String> aliases) {
        this.channelId = id;
        this.aliases = aliases;
    }

    @Override
    public String toString() {
        return (channel == null ? channelId : channel.getName()) + ", aliases: " + aliases;
    }

    public String getName() {
        if(channel == null){
            return aliases.get(0);
        }else{
            return String.format("#%s",channel.getName());
        }
    }
}
