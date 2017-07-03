package core;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.DEBUG;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

public class FeedChannels
{
    private static ArrayList<FeedChannel> channels = new ArrayList<>();

    private static SimpleLog feedChannelsLog = SimpleLog.getLog("FeedChannels");

    public static void main(String[] args) {
        loadChannels();
    }

    public static FeedChannel fromString(final String str) {
        feedChannelsLog.log(DEBUG,"Converting string: " + str + " to FeedChannel");

        for (FeedChannel channel : channels) {
            if(channel.aliases.contains(str)) return channel;
        }

        return null;
    }

    public static FeedChannel fromDbString(String str) {
        return null;
    }

    public static void loadChannels() {
        File file = new File("channels.txt");

        channels = new ArrayList<>();

        try (Scanner in = new Scanner(file)) {

            String id = null;
            ArrayList<String> aliases = new ArrayList<>();

            while(in.hasNext()){
                String line = in.nextLine();

                int idEnd = line.indexOf("=");

                id = line.substring(0,idEnd).trim();

                int aliasesStart = line.indexOf("[");
                int aliasesEnd = line.indexOf("]") + 1;

                aliases = Util.parseList(line.substring(aliasesStart,aliasesEnd));

                channels.add(new FeedChannel(id,aliases));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        feedChannelsLog.log(INFO,channels);
    }

    public static FeedChannel fromId(String id) {
        for (FeedChannel channel : channels) {
            if(channel.channel.getId().equals(id)) return channel;
        }

        return null;
    }

    public static void updateDiscordChannels(JDA jda) {
        feedChannelsLog.log(INFO,"Fetching discord channels by IDs");
        for (FeedChannel channel : channels) {
            channel.channel = jda.getTextChannelById(channel.channelId);

            if(!channel.aliases.contains(channel.channel.getName())){
                channel.aliases.add(channel.channel.getName());
            }
        }
        feedChannelsLog.log(INFO, "Done");
        feedChannelsLog.log(INFO, channels);
    }

    public static String getListMessage() {
        String str = "";

        for (FeedChannel channel : channels) {
            str += String.format("  %s, aliases: %s%n",channel.getName(),channel.getAliasList());
        }

        return str;
    }
}
