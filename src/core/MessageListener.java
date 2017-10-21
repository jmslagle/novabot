package core;

import maps.GeofenceIdentifier;
import maps.Geofencing;
import nests.Nest;
import nests.NestSearch;
import nests.NestSheetManager;
import nests.NestStatus;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;
import notifier.PokeNotifier;
import notifier.RaidNotificationSender;
import notifier.RaidNotifier;
import org.ini4j.Ini;
import parser.*;
import pokemon.PokeSpawn;
import pokemon.Pokemon;
import raids.LobbyManager;
import raids.Raid;
import raids.RaidLobby;
import raids.RaidSpawn;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.*;

public class MessageListener extends ListenerAdapter
{
    private static final String NEST_FB_GROUP = "https://www.facebook.com/groups/PogoCBRNests/";
    private static final String NEST_MAP = "http://www.google.com/maps/d/u/0/viewer?mid=1d-QuaDK1tJRiHKODXErTQIDqIAY";
    private static TextChannel roleLog;
    private static TextChannel nestsReports;
    public static Guild guild;
    public static boolean testing;
    private final String regionHelp = "Accepted channels are:\n\nall\nwodenweston = woden-weston = woden-weston-region = woden-weston-supporter\ngungahlin = gungahlin-region = gungahlin-supporter\ninnernorth = inner-north = inner-north-region = inner-north-supporter\nbelconnen = belconnen-region = belconnen-supporter\ninnersouth = inner-south = inner-south-region = inner-south-supporter\ntuggeranong = tuggeranong-region = tuggeranong-supporter\nqueanbeyan = queanbeyan-region = queanbeyan-supporter\nlegacy = legacyrare = legacy-rare = legacy-rare-supporter\nlarvitar = larvitarcandy = larvitar-candy = larvitar-candy-supporter\ndratini = dratinicandy = dratini-candy = dratini-candy-supporter\nmareep = mareepcandy = mareep-candy = mareep-candy-supporter\nultrarare = ultra-rare = ultra-rare-supporter\n100iv = 100-iv = 100% = 100-iv-supporter\nsnorlax = snorlax-supporter\nevent\n0iv = 0-iv = 0% = 0-iv-supporter\ndexfiller = dex-filler\nbigfishlittlerat = big-fish-little-rat = big-fish-little-rat-cardboard-box\n";
    private final String inputFormat = "```!addpokemon <pokemon1, pokemon2, pokemon3> <channel1, channel2, channel3>```\nFor as many pokemon and channels as you want. Make sure you include the <>. For more information on regions use the !channellis command";
    private final String nestHelp = "My nest commands are: \n```!nest <pokemon list> <status list>\n!nest pokemon status\n!nest pokemon\n!reportnest [your text here]\n!confirmed\n!suspected\n!fb or !nestfb\n!map or !nestmap\n!help\n```";
    private final String helpStr = "My commands are: \n```!addpokemon <pokemon list> <miniv,maxiv> <location list>\n!addpokemon pokemon\n!delpokemon <pokemon list> <miniv,maxiv> <location list>\n!delpokemon pokemon\n!clearpokemon <pokemon list>\n!clearlocation <location list>\n!reset\n!settings\n!help\n!channellist or !channels```";
    private static MessageChannel userUpdatesLog;

    public static final String WHITE_GREEN_CHECK = "\u2705";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Config config;
    public static SuburbManager suburbs;

    HashMap<Long, Message> messageMap = new HashMap<>();
    public static ArrayList<Invite> invites = new ArrayList<>();


    static SimpleLog novabotLog = SimpleLog.getLog("novabot");
    public static JDA jda;

    public static LobbyManager lobbyManager;

    public static void main(final String[] args) {
        testing = false;
        try {
            SimpleLog.addFileLogs(new File("std.log"),new File("err.log"));
        } catch (IOException e) {
            e.printStackTrace();
        };

//        DBManager.dbLog.setLevel(DEBUG);

        loadConfig();
        loadSuburbs();


        if(config.useChannels()){
            FeedChannels.loadChannels();
        }

        if(config.useGeofences()){
            Geofencing.loadGeofences();
        }

        novabotLog.log(INFO,"Connecting to db");
//        if (MessageListener.testing) {
//            DBManager.novabotdbConnect("root", "mimi");
//            DBManager.rocketmapdbConnect("root", "mimi");
//        }
//        else {
            DBManager.rocketmapdbConnect();
            DBManager.novabotdbConnect();
//        }
        novabotLog.log(INFO,"Connected");
        try {
             jda = new JDABuilder(AccountType.BOT)
                    .setAutoReconnect(true)
                    .setGame(Game.of("Pokemon Go"))
                    .setToken(config.getToken())
                    .buildBlocking();


            if(config.useChannels()){
                FeedChannels.updateDiscordChannels(jda);
            }

            jda.addEventListener(new MessageListener());

            for (Guild guild1 : jda.getGuilds()) {
                final Guild guild = MessageListener.guild = guild1;
                novabotLog.log(DEBUG,guild.getName());

                TextChannel channel = guild.getTextChannelById(config.getCommandChannelId());
                if (channel != null) {
                    if(config.showStartupMessage()) {
                        channel.sendMessage("I'm awake again!").queue();
                    }
                } else{
                    novabotLog.log(INFO,String.format("couldn't find command channel by id from config: %s", config.getCommandChannelId()));
                }
//                if (guild.getTextChannelsByName(MessageListener.testing ? "novabot-testing" : "novabot", true).size() > 0) {
//                    final TextChannel channel = guild.getTextChannelsByName(MessageListener.testing ? "novabot-testing" : "novabot", true).get(0);
//                    System.out.println(channel.getName());
//                    channel.sendMessage("Hi! I'm awake again!").queue();
//                }
            }

            guild.getMember(jda.getSelfUser()).getRoles().forEach(System.out::println);

            guild.getInvites().queue(success -> invites.addAll(success));

            if(config.loggingEnabled()) {
                MessageListener.roleLog = MessageListener.guild.getTextChannelById(config.getRoleLogId());
                MessageListener.userUpdatesLog = MessageListener.guild.getTextChannelById(config.getUserUpdatesId());
            }

            if(config.nestsEnabled()) {
                MessageListener.nestsReports = MessageListener.guild.getTextChannelsByName("nests-reports", true).get(0);
            }

            if(!testing && config.useRmDb() && config.pokemonEnabled()) {
                final ScheduledExecutor executor = new ScheduledExecutor(1);
                executor.scheduleAtFixedRate(new PokeNotifier(jda, testing), 0L, config.getPokePollingRate(), TimeUnit.SECONDS);
            }

            if(config.useRmDb() && config.raidsEnabled()){
                ScheduledExecutor executorService = new ScheduledExecutor(1);
                executorService.scheduleAtFixedRate(new RaidNotifier(jda,testing),0,config.getRaidPollingRate(),TimeUnit.SECONDS);
            }


            if(config.isRaidOrganisationEnabled()){
                lobbyManager = new LobbyManager();
//                lobbyManager.addLobbies(DBManager.getActiveLobbies());
                RaidNotificationSender.nextId = DBManager.highestRaidLobbyId() + 1;
//                ScheduledExecutor executor = new ScheduledExecutor(1);
//                executor.scheduleAtFixedRate(new LobbyMonitor(lobbyManager),0,30,TimeUnit.SECONDS);
            }

            if(testing) {
                RaidSpawn spawn = new RaidSpawn("Australian Croatian Club",
                        "1", -35.265134, 149.122796,
                        new Timestamp(DBManager.getCurrentTime().getTime() + 504000),
                        new Timestamp(DBManager.getCurrentTime().getTime() + 6000000),
                        1,
                        11003,
                        "fire",
                        "fire blast",
                        4);

                spawn.setLobbyCode(1);
                lobbyManager.addLobby(new RaidLobby(spawn,"0001","293328612919345152","274467121315053569","11111"));
                Message message = spawn.buildMessage();
                jda.getUserById(107730875596169216L).openPrivateChannel().queue(c->c.sendMessage(message).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue()));

//                spawn = new RaidSpawn("O'Connor 2",
//                        "2", -35.265134, 149.122796,
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 404000),
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 5000000),
//                        2,
//                        11003,
//                        "fire",
//                        "fire blast",
//                        4);
//
//                spawn.setLobbyCode(2);
//                lobbyManager.newRaid(spawn.getLobbyCode(),spawn);
//                Message message2 = spawn.buildMessage();
//                jda.getUserById(107730875596169216L).openPrivateChannel().queue(c->c.sendMessage(message2).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue()));
//
//                spawn = new RaidSpawn("Bridge 2007",
//                        "3", -35.265134, 149.122796,
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 504000),
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 6000000),
//                        3,
//                        11003,
//                        "fire",
//                        "fire blast",
//                        4);
//
//                spawn.setLobbyCode(3);
//                lobbyManager.newRaid(spawn.getLobbyCode(),spawn);
//                Message message3 = spawn.buildMessage();
//                jda.getUserById(107730875596169216L).openPrivateChannel().queue(c->c.sendMessage(message3).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue()));
//
//                spawn = new RaidSpawn("O'Connor Wetlands",
//                        "4", -35.265134, 149.122796,
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 509000),
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 6003000),
//                        4,
//                        11003,
//                        "fire",
//                        "fire blast",
//                        4);
//
//                spawn.setLobbyCode(4);
//                lobbyManager.newRaid(spawn.getLobbyCode(),spawn);
//                Message message4 = spawn.buildMessage();
//                jda.getUserById(107730875596169216L).openPrivateChannel().queue(c->c.sendMessage(message4).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue()));
//
//                spawn = new RaidSpawn("Heatwave",
//                        "5", -35.265134, 149.122796,
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 500000),
//                        new Timestamp(DBManager.getCurrentTime().getTime() + 6105000),
//                        5,
//                        11003,
//                        "fire",
//                        "fire blast",
//                        4);
//
//                spawn.setLobbyCode(5);
//                lobbyManager.newRaid(spawn.getLobbyCode(),spawn);
//                Message message5 = spawn.buildMessage();
//                jda.getUserById(107730875596169216L).openPrivateChannel().queue(c->c.sendMessage(message5).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue()));

//                DBManager.knownRaids.put(spawn.gymId, spawn);
            }
        }
        catch (LoginException | InterruptedException | RateLimitedException ex2) {
            ex2.printStackTrace();
        }

//        if(config.isRaidOrganisationEnabled()) {
//            lobbyManager = new LobbyManager();
////
//            RaidSpawn raidSpawn = new RaidSpawn("gym",
//                    "123", -35.34200996278955, 149.05508042811897,
//                    new Timestamp(DBManager.getCurrentTime().getTime() + 600000),
//                    new Timestamp(DBManager.getCurrentTime().getTime() + 504000),
//                    248,
//                    1234,
//                    "disco",
//                    "twist",
//                    4);
////
//            raidSpawn.setLobbyCode(2);
//
//            lobbyManager.newRaid(raidSpawn.getLobbyCode(), raidSpawn);
//            jda.getTextChannelById(config.getCommandChannelId()).sendMessage(raidSpawn.buildMessage()).queue(m -> m.addReaction(WHITE_GREEN_CHECK).queue());
//        }

        config.loadEmotes();




//        jda.getUserById(107730875596169216L).openPrivateChannel().queue(c->c.sendMessage(spawn.buildMessage()).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue()));
//
//        DBManager.knownRaids.put(spawn.gymId,spawn);

        novabotLog.log(INFO,"connected");
    }

    public static void loadSuburbs() {
        suburbs = new SuburbManager(new File("suburbs.txt"));
    }

    public static void loadConfig() {
        try {
            config = new Config(
                    new Ini(new File(testing ? "config.example.ini" : "config.ini")),
                    new File("gkeys.txt"),
                    new Ini(new File("formatting.ini"))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

//        if(testing) return;

        if(!config.isRaidOrganisationEnabled()) return;

        if(event.getUser().isBot()) return;

        if(!event.getReactionEmote().getName().equals(WHITE_GREEN_CHECK)) return;

        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();

        if(!message.getAuthor().isBot()) return;

        novabotLog.log(DEBUG,"white green check reaction added to a bot message that contains an embed!");

        String content;
        if(message.getEmbeds().size() > 0) {
            content = message.getEmbeds().get(0).getDescription();
        }else{
            content = message.getContent();
        }

        int joinIndex = content.indexOf("!joinraid") + 10;
        String lobbyCode = content.substring(joinIndex, content.substring(joinIndex).indexOf("`") + joinIndex).trim();

        novabotLog.log(INFO,"Message clicked was for lobbcode " + lobbyCode);

        RaidLobby lobby = lobbyManager.getLobby(lobbyCode);

        if(lobby == null) {
            event.getChannel().sendMessageFormat("%s, that lobby has ended and cannot be joined.",event.getMember()).queue();
            return;
        }

        if(!lobby.containsUser(event.getUser().getId())) {

            lobby.joinLobby(event.getUser().getId());

            if(event.getChannelType() == ChannelType.PRIVATE) {
                event.getChannel().sendMessageFormat("%s you have been placed in %s. There are now %s users in the lobby.", event.getUser(), lobby.getChannel(), lobby.memberCount()).queue();
            }

            alertRaidChats(config.getRaidChats(lobby.spawn.getGeofences()),String.format(
                    "%s joined %s raid in %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
                    guild.getMember(event.getUser()).getAsMention(),
                    (lobby.spawn.bossId == 0 ? String.format("lvl %s egg",lobby.spawn.raidLevel) : lobby.spawn.properties.get("pkmn")),
                    lobby.getChannel().getAsMention(),
                    lobby.memberCount(),
                    lobby.lobbyCode
            ));

//            if(lobby.spawn.bossId == 249){
//                jda.getTextChannelById(338321961040084992L).sendMessageFormat(
//                        "%s joined %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
//                        guild.getMember(event.getUser()).getAsMention(),
//                        lobby.getChannel().getAsMention(),
//                        lobby.memberCount(),
//                        lobby.lobbyCode).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue());
//            }
        }
    }

    private void alertRaidChats(String[] raidChatIds, String message) {
        for (String raidChatId : raidChatIds) {
            guild.getTextChannelById(raidChatId).sendMessageFormat(message).queue(
                    m -> m.addReaction(WHITE_GREEN_CHECK).queue()
            );
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if(!config.loggingEnabled()) return;

        JDA jda = event.getJDA();
        final long id = event.getMessageIdLong();
        TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());


        Message foundMessage = messageMap.get(id);

        if(foundMessage == null){
            MessageListener.userUpdatesLog.sendMessageFormat("A message was deleted from %s, but the message could not be retrieved from the log",channel).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("A message was deleted from %s", channel.getName()),null);
        embedBuilder.addField("Channel",channel.getAsMention(),true);
        embedBuilder.setDescription(String.format("%s%n %s:%n %s",
                foundMessage.getCreationTime().atZoneSameInstant(ZoneId.of(config.getTimeZone())).format(formatter),
                foundMessage.getAuthor().getAsMention(),
                foundMessage.getContent()));

        MessageListener.userUpdatesLog.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildMemberRoleAdd(final GuildMemberRoleAddEvent event) {
        if(!config.loggingEnabled()) return;

        final User user = event.getMember().getUser();
        String roleStr = "";
        for (final Role role : event.getRoles()) {
            if(lobbyManager.isLobbyRoleId(role.getId())) continue;

            roleStr = roleStr + role.getName() + " ";
        }

        if(roleStr.length() != 0) {
            MessageListener.roleLog.sendMessage(user.getAsMention() + " had " + roleStr + "role(s) added").queue();
        }
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        if(!config.loggingEnabled()) return;

        final User user = event.getMember().getUser();
        String roleStr = "";
        for (final Role role : event.getRoles()) {
            if(lobbyManager.isLobbyRoleId(role.getId())) continue;
            roleStr = roleStr + role.getName() + " ";
        }

        if(roleStr.length() != 0) {
            MessageListener.roleLog.sendMessage(user.getAsMention() + " had " + roleStr + "role(s) removed").queue();
        }
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        if(!config.loggingEnabled()) return;


        final JDA jda = event.getJDA();
        final Member member = event.getMember();

        guild.getInvites().queue(success -> {
            String theCode = null;

            for (Invite newInvite : success) {
                if(theCode != null) break;

                boolean found = false;
                for (Invite oldInvite : invites) {
                    if(oldInvite.getCode().equals(newInvite.getCode())){
                        found = true;

                        if (newInvite.getUses() > oldInvite.getUses()) {
                            theCode = newInvite.getCode();

                            RaidLobby lobby = lobbyManager.getLobbyByChannelId(newInvite.getChannel().getId());

                            if(lobby != null){
                                lobby.joinLobby(member.getUser().getId());
                            }
                            break;
                        }
                    }
                }

                if(!found && newInvite.getUses() == 1){
                    theCode = newInvite.getCode();
                    break;
                }
            }
            invites = (ArrayList<Invite>) success;

            MessageListener.userUpdatesLog.sendMessage(
                    member.getAsMention() +
                            " joined with code " + theCode + ". The account was created " +
                            member.getUser().getCreationTime().atZoneSameInstant(ZoneId.of(config.getTimeZone())).format(formatter)).queue();
            DBManager.logNewUser(member.getUser().getId());

            if (member.getEffectiveName().equalsIgnoreCase("novabot") && !member.getUser().isBot()) {
                guild.getController().kick(member).queue(
                        s -> userUpdatesLog.sendMessage("Kicked " + member.getEffectiveName() + " because their name was `novabot`").queue()
                );
            }
        });


    }

    @Override
    public void onUserNameUpdate(UserNameUpdateEvent event) {
        if(!config.loggingEnabled()) return;

        final JDA jda = event.getJDA();
        final User user = event.getUser();
        MessageListener.userUpdatesLog.sendMessage(user.getAsMention() + " has changed their username from " + event.getOldName() + " to " + event.getUser().getName()).queue();

        if(guild.getMember(user).getEffectiveName().equalsIgnoreCase("novabot") && !user.isBot()){
            Member member = guild.getMember(user);
            guild.getController().kick(member).queue(
                    success -> userUpdatesLog.sendMessage("Kicked " + member.getEffectiveName() + " because their name was `novabot`").queue()
            );
        }
    }

    @Override
    public void onGuildMemberNickChange(final GuildMemberNickChangeEvent event) {
        if(!config.loggingEnabled()) return;

        final JDA jda = event.getJDA();
        final User user = event.getMember().getUser();
        MessageListener.userUpdatesLog.sendMessage(user.getAsMention() + " has changed their nickname from " + event.getPrevNick() + " to " + event.getNewNick()).queue();

        if(guild.getMember(user).getEffectiveName().equalsIgnoreCase("novabot") && !user.isBot()){
            Member member = guild.getMember(user);
            guild.getController().kick(member).queue(
                    success -> userUpdatesLog.sendMessage("Kicked " + member.getEffectiveName() + " because their name was `novabot`").queue()
            );
        }
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if(testing) return;


        final JDA jda = event.getJDA();
        final User author = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel channel = event.getChannel();
        final String msg = message.getContent();

        messageMap.put(message.getIdLong(),message);

        if (event.isFromType(ChannelType.TEXT)) {
            final Guild guild = event.getGuild();
            final TextChannel textChannel = event.getTextChannel();
            MessageListener.guild = guild;

            if(config.isRaidOrganisationEnabled() && lobbyManager.isLobbyChannel(channel.getId())) {
                parseRaidLobbyMsg(author, msg, textChannel);
            }else if(config.isRaidOrganisationEnabled() && config.isRaidChannel(channel.getId())){
                parseRaidChatMsg(author,msg,textChannel);
            }else if (channel.getId().equals(config.getUserUpdatesId())) {
                this.parseModMsg(message, textChannel);
//            }else if(config){
            }else if(channel.getId().equals(config.getCommandChannelId())){
                this.parseMsg(msg.toLowerCase().trim(), author, textChannel);
            }else if (config.nestsEnabled() && channel.getName().equals(MessageListener.testing ? "nests-testing" : "nests")) {
                this.parseNestMsg(msg.toLowerCase().trim(), author, channel, event.getChannelType());
            }
            else if (!config.isSupporterOnly()){
//                if (MessageListener.testing) {
//                    if (message.isWebhookMessage()) {
//                        return;
//                    }
//                }
//                else if (!message.isWebhookMessage()) {
//                    return;
//                }

//                if(!message.isWebhookMessage()) return;

//                if(config.useChannels()) {
//                    FeedChannel feedChannel = FeedChannels.fromId(channel.getId());
//
//                    if (feedChannel != null) {
//                        novabotLog.log(DEBUG, "Channel is a feed channel");
//
//                        processPokeAlert(feedChannel, message);
//                    }
//                }
            }
        }
        else if (event.isFromType(ChannelType.PRIVATE)) {
            final PrivateChannel privateChannel = event.getPrivateChannel();
            novabotLog.log(INFO,String.format("[PRIV]<%s>: %s\n", author.getName(), msg));

            if(config.isSupporterOnly() && !isSupporter(author.getId())){
                channel.sendMessage(author.getAsMention() + ", sorry, I can only accept commands from supporters").queue();
                return;
            }

            if (config.nestsEnabled() && msg.equals("!nesthelp")) {
                channel.sendMessage("My nest commands are: \n```!nest <pokemon list> <status list>\n!nest pokemon status\n!nest pokemon\n!reportnest [your text here]\n!confirmed\n!suspected\n!fb or !nestfb\n!map or !nestmap\n!help\n```").queue();
                return;
            }
            if (!msg.startsWith("!nest") && !msg.startsWith("!map")) {
                if (!msg.startsWith("fb")) {
                    this.parseMsg(msg.toLowerCase().trim(), author, privateChannel);
                }
            }
        }
        else if (event.isFromType(ChannelType.GROUP)) {
            final Group group = event.getGroup();
            final String groupName = (group.getName() != null) ? group.getName() : "";
            novabotLog.log(INFO,String.format("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg));
        }
    }

    private void parseRaidChatMsg(User author, String msg, TextChannel textChannel) {
        if(!msg.startsWith("!") || author.isBot()) return;

        if(msg.startsWith("!joinraid")){
            String groupCode = msg.substring(msg.indexOf("raid ") + 5).trim();

            RaidLobby lobby = lobbyManager.getLobby(groupCode);

            if(lobby == null){
                textChannel.sendMessageFormat("%s sorry, there are no active raid lobbies with the lobby code `%s`",author,groupCode).queue();
                return;
            }else{
                if(lobby.containsUser(author.getId())){
                    textChannel.sendMessageFormat("%s you are already in that raid lobby!",author).queue();
                    return;
                }

                lobby.joinLobby(author.getId());
                alertRaidChats(config.getRaidChats(lobby.spawn.getGeofences()),String.format(
                        "%s joined %s raid in %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
                        author.getAsMention(),
                        (lobby.spawn.bossId == 0 ? String.format("lvl %s egg",lobby.spawn.raidLevel) : lobby.spawn.properties.get("pkmn")),
                        lobby.getChannel().getAsMention(),
                        lobby.memberCount(),
                        lobby.lobbyCode
                ));

//                if(lobby.spawn.bossId == 249){
//                    jda.getTextChannelById(338321961040084992L).sendMessageFormat(
//                            "%s joined %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
//                            guild.getMember(author),
//                            lobby.getChannel(),
//                            lobby.memberCount(),
//                            lobby.lobbyCode).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue());
//                }

            }


            return;
        }else if(msg.equals("!activeraids")) {
            ArrayList<GeofenceIdentifier> geofences = config.getRaidChatGeofences(textChannel.getId());

            if (geofences.size() > 0 || textChannel.getType() == ChannelType.PRIVATE) {

                String noLobbiesMsg = null;

                for (GeofenceIdentifier geofence : geofences) {

                    ArrayList<RaidLobby> lobbies = lobbyManager.getLobbiesByGeofence(geofence);

                    if (lobbies.size() == 0) {
                        if (noLobbiesMsg == null) {
                            noLobbiesMsg = String.format("%s, there are no active lobbies in %s", author.getAsMention(), geofence.name);
                        } else {
                            noLobbiesMsg += String.format(", %s", geofence.name);
                        }
                        continue;
                    }

                    textChannel.sendMessageFormat("%s, there are %s active lobbies in %s", author, lobbies.size(), geofence.name).queue();

                    for (RaidLobby lobby : lobbies) {
                        textChannel.sendMessage(lobby.getInfoMessage()).queue(m -> m.addReaction(WHITE_GREEN_CHECK).queue());
                    }
                }

                if (noLobbiesMsg != null) {
                    textChannel.sendMessage(noLobbiesMsg).queue();
                }

                return;
            }
        }
    }

    private void parseRaidLobbyMsg(User author, String msg, TextChannel textChannel) {
        if(!msg.startsWith("!")) return;

        RaidLobby lobby = lobbyManager.getLobbyByChannelId(textChannel.getId());

        if(msg.equals("!help")){
            textChannel.sendMessage("My raid lobby commands are: " +
                    "```" +
                    "!leave\n" +
                    "!map\n" +
                    "!timeleft\n" +
                    "!status\n" +
                    (lobby.spawn.bossId != 0 ? "!boss\n" : "") +
                    "!team\n" +
                    "!code\n" +
                    "```").queue();
            return;
        }

        if(msg.equals("!leave")){
            lobby.leaveLobby(author.getId());
            return;
        }

        if(msg.equals("!map")){
            textChannel.sendMessage(lobby.spawn.properties.get("gmaps")).queue();
            return;
        }

        if(msg.equals("!timeleft")){

            if(lobby.spawn.bossId != 0) {
                textChannel.sendMessageFormat("Raid ends at %s (%s remaining)", lobby.spawn.getDisappearTime(), lobby.spawn.timeLeft(lobby.spawn.raidEnd)).queue();
            }else {
                textChannel.sendMessageFormat("Raid starts at %s (%s remaining)", lobby.spawn.getStartTime(), lobby.spawn.timeLeft(lobby.spawn.battleStart)).queue();
            }
            return;
        }

        if(msg.equals("!status")){
            textChannel.sendMessage(lobby.getStatusMessage()).queue();
            return;
        }

        if(msg.equals("!boss")){
            if(lobby.spawn.bossId == 0){
                textChannel.sendMessage(String.format("The boss hasn't spawned yet. It will appear at %s",
                        lobby.spawn.properties.get("24h_start"))).queue();
            }else {
                textChannel.sendMessage(lobby.getBossInfoMessage()).queue();
            }
            return;
        }

        if(msg.equals("!team")){
            textChannel.sendMessage(lobby.getTeamMessage()).queue();
            return;
        }

        if(msg.equals("!code")){
            textChannel.sendMessageFormat("This lobby can be joined using the command `!joinraid %s`",lobby.lobbyCode).queue();
            return;
        }
    }

    private void parseModMsg(Message msg, TextChannel channel) {
        if(msg.getContent().startsWith("!joindate")) {

            String response = String.format("%s, the results of your search are:\n\n", msg.getAuthor().getAsMention());

            List<User> mentionedUsers = msg.getMentionedUsers();

            for (User mentionedUser : mentionedUsers) {
                OffsetDateTime joinDate = guild.getMember(mentionedUser).getJoinDate();
                String formattedDate = joinDate.toInstant().atZone(ZoneId.of(config.getTimeZone())).format(formatter);

                response += String.format("  %s joined at %s", mentionedUser.getAsMention(),formattedDate);
            }

            channel.sendMessage(response).queue();
        }
    }

    private void processPokeAlert(FeedChannel feedChannel, Message message) {
        MessageEmbed embed = message.getEmbeds().get(0);

        final String msgTitle = embed.getTitle();

        final int suburbStart = msgTitle.indexOf("[") + 1;
        final int suburbEnd = msgTitle.indexOf("]");
        final String suburb = msgTitle.substring(suburbStart, suburbEnd);

        final int pokeStart = suburbEnd + 2;
        final int pokeEnd;
        if (!msgTitle.contains("Unown")) {
            pokeEnd = msgTitle.length();
        } else {
            pokeEnd = msgTitle.substring(pokeStart).indexOf(" ") + pokeStart;
        }
        final String pokeName = msgTitle.substring(pokeStart, pokeEnd).toLowerCase().trim();

        String form = null;
        if (pokeName.equals("Unown")) {
            final int formStart = msgTitle.substring(pokeEnd).indexOf("[") + pokeEnd;
            form = msgTitle.substring(formStart, formStart + 1);
        }

        final String msgBody = embed.getDescription();

        final int timeEnd = msgBody.indexOf(")**");

        final int ivStart = msgBody.substring(timeEnd).indexOf("(") + timeEnd + 1;
        final int ivEnd = msgBody.indexOf("%");
        final String ivStr = msgBody.substring(ivStart, ivEnd);
        final float pokeIV = ivStr.equals("?") ? 0 : Float.parseFloat(msgBody.substring(ivStart, ivEnd));

        final int cpStart = msgBody.indexOf("CP: ") + 4;
        int cpEnd;
        if (msgBody.contains("(lvl ")) {
            cpEnd = msgBody.substring(cpStart).indexOf("(lvl") + cpStart;
        }else{
            cpEnd = msgBody.substring(cpStart).indexOf("Lvl") + cpStart;
        }

        final String cpStr = msgBody.substring(cpStart, cpEnd).trim();
        int cp = 0;

        try {
            cp = cpStr.equals("?") ? 0 : Integer.parseInt(cpStr);
        } catch (NumberFormatException e) {
            novabotLog.log(FATAL, feedChannel + ", " + msgTitle);
            novabotLog.log(FATAL, msgBody);
            e.printStackTrace();
        }

        final int moveSetStart = msgBody.indexOf("Moveset: ") + 9;
        final int moveSetEnd = msgBody.indexOf("Gender: ");
        final int moveSetSplit = msgBody.substring(moveSetStart, moveSetEnd).indexOf("-") + moveSetStart;

        final String move_1 = msgBody.substring(moveSetStart, moveSetSplit).trim().toLowerCase();
        final String move_2 = msgBody.substring(moveSetSplit + 2, moveSetEnd).trim().toLowerCase();

        final PokeSpawn pokeSpawn = new PokeSpawn(Pokemon.nameToID(pokeName), feedChannel, suburb, pokeIV, move_1, move_2, form, cp);
        novabotLog.log(DEBUG, pokeSpawn.toString());
        final ArrayList<String> ids = DBManager.getUserIDsToNotify(pokeSpawn);

        if (!testing) {
            novabotLog.log(DEBUG, "alerting non-supporters");
            this.alertPublic(message, ids);
        } else {
            jda.getUserById("107730875596169216").openPrivateChannel().queue(success -> success.sendMessage(message).queue());
        }
    }

    private void parseNestMsg(final String msg, final User author, final MessageChannel channel, final ChannelType channelType) {
        if (!msg.startsWith("!")) {
            return;
        }
        switch (msg) {
            case "!fb":
            case "!nestfb":
                channel.sendMessage("https://www.facebook.com/groups/PogoCBRNests/").queue();
                break;
            case "!map":
            case "!nestmap":
                channel.sendMessage("http://www.google.com/maps/d/u/0/viewer?mid=1d-QuaDK1tJRiHKODXErTQIDqIAY").queue();
                break;
            case "!help":
                channel.sendMessage("My nest commands are: \n```!nest <pokemon list> <status list>\n!nest pokemon status\n!nest pokemon\n!reportnest [your text here]\n!confirmed\n!suspected\n!fb or !nestfb\n!map or !nestmap\n!help\n```").queue();
                break;
            case "!reload":
                if(isAdmin(author)){
                    loadConfig();
                    loadSuburbs();
                }
                break;
            default:
                if (msg.startsWith("!reportnest")) {
                    final String report = msg.substring(msg.indexOf(" ") + 1);
                    MessageListener.nestsReports.sendMessage(author.getAsMention() + " reported: \"" + report + "\"").queue();
                    return;
                }
                if (msg.startsWith("!confirmed")) {
                    final MessageBuilder builder = new MessageBuilder();
                    builder.append(author.getAsMention()).append("");
                    final ArrayList<Nest> nests = NestSheetManager.getNestsByStatus(new NestStatus[] { NestStatus.Confirmed });
                    if (nests.size() == 0) {
                        builder.append(" sorry, I couldn't find any confirmed nests");
                        channel.sendMessage(builder.build()).queue();
                    }
                    else {
                        if (channelType != ChannelType.PRIVATE) {
                            channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                        }
                        builder.append(" I found ").append(nests.size()).append(" confirmed nests:\n\n");
                        for (final Nest nest : nests) {
                            builder.append(nest).append("\n");
                            builder.append("<").append(nest.getGMapsLink()).append(">\n\n");
                        }
                        if (!author.hasPrivateChannel()) {
                            author.openPrivateChannel().complete();
                        }
                        for (final Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            author.openPrivateChannel().queue(success -> success.sendMessage(message).queue());
                        }
                    }
                    return;
                }
                if (msg.equals("!suspected")) {
                    final MessageBuilder builder = new MessageBuilder();
                    builder.append(author.getAsMention()).append("");
                    final ArrayList<Nest> nests = NestSheetManager.getNestsByStatus(new NestStatus[] { NestStatus.Suspected });
                    if (nests.size() == 0) {
                        builder.append(" sorry, I couldn't find any suspected nests");
                        channel.sendMessage(builder.build()).queue();
                    }
                    else {
                        if (channelType != ChannelType.PRIVATE) {
                            channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                        }
                        builder.append(" I found ").append(nests.size()).append(" suspected nests:\n\n");
                        for (final Nest nest : nests) {
                            builder.append(nest).append("\n");
                            builder.append("<").append(nest.getGMapsLink()).append(">\n\n");
                        }
                        if (!author.hasPrivateChannel()) {
                            author.openPrivateChannel().complete();
                        }
                        for (final Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            author.openPrivateChannel().queue(success -> success.sendMessage(message).queue());
                        }
                    }
                    return;
                }
                final UserCommand userCommand = Parser.parseInput(msg,isSupporter(author.getId()));
                final ArrayList<InputError> exceptions = userCommand.getExceptions();
                final String cmdStr = (String)userCommand.getArg(0).getParams()[0];
                if (exceptions.size() > 0) {
                    String errorMessage = author.getAsMention() + ", I had " + ((exceptions.size() == 1) ? "a problem" : "problems") + " reading your input.\n\n";
                    final InputError error = InputError.mostSevere(exceptions);
                    errorMessage += error.getErrorMessage(userCommand);
                    channel.sendMessage(errorMessage).queue();
                    break;
                }
                if (!cmdStr.startsWith("!nest")) {
                    break;
                }
                final NestSearch nestSearch = userCommand.buildNestSearch();
                final MessageBuilder builder2 = new MessageBuilder();
                builder2.append(author.getAsMention()).append("");
                final ArrayList<Nest> nests2 = NestSheetManager.getNests(nestSearch);
                if (nests2.size() == 0) {
                    builder2.append(" sorry, I couldn't find any ").append(NestStatus.listToString(nestSearch.getStatuses())).append(" nests for ").append(Pokemon.listToString(nestSearch.getPokemon()));
                    channel.sendMessage(builder2.build()).queue();
                    break;
                }
                if (channelType != ChannelType.PRIVATE) {
                    channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                }
                builder2.append(" I found ").append(nests2.size()).append(" results for ").append(NestStatus.listToString(nestSearch.getStatuses())).append(" ").append(Pokemon.listToString(nestSearch.getPokemon())).append(" nests :\n\n");
                for (final Pokemon poke : nestSearch.getPokemon()) {
                    builder2.append("**").append(poke.name).append("**\n");
                    boolean foundPoke = false;
                    for (final Nest nest2 : nests2) {
                        if (nest2.pokemon.name.equals(poke.name)) {
                            foundPoke = true;
                            builder2.append("  ").append(nest2).append("\n");
                            builder2.append("  <").append(nest2.getGMapsLink()).append(">\n\n");
                        }
                    }
                    if (!foundPoke) {
                        builder2.append("  No results found\n");
                    }
                }
                if (!author.hasPrivateChannel()) {
                    author.openPrivateChannel().complete();
                }
                for (final Message message2 : builder2.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                    author.openPrivateChannel().queue(success -> success.sendMessage(message2).queue());
                }
                break;
        }
    }

    private boolean isAdmin(User author) {
        for (Role role : guild.getMember(author).getRoles()) {
            if(role.getId().equals(config.getAdminRole())) return true;
        }
        return false;
    }

    private void alertPublic(final Message message, final ArrayList<String> userIDs) {
        for (final String userID : userIDs) {
            if (isSupporter(userID)) {
                continue;
            }
            if (DBManager.countPokemon(userID, config.countLocationsInLimits()) > 3) {
                Member member = MessageListener.guild.getMemberById(userID);
                if(member == null) continue;;

                final User user = member.getUser();
                if (!user.hasPrivateChannel()) {
                    user.openPrivateChannel().complete();
                }
                user.openPrivateChannel().queue(success ->
                        success.sendMessage(String.format("Hi %s, I noticed recently you have lost your supporter status. As a result I have cleared your settings, however as a non-supporter you can add up to 3 pokemon and 3 raids to your settings", user.getAsMention())).queue()
                );
                DBManager.resetUser(userID);
                MessageListener.roleLog.sendMessage(String.format("%s has lost supporter status with more than 3 pokemon in their settings. Their settings have been reset and they have been PMed", user.getAsMention())).queue();
            }else if(DBManager.countRaids(userID, true) > 3) {
                final User user = MessageListener.guild.getMemberById(userID).getUser();
                if (!user.hasPrivateChannel()) {
                    user.openPrivateChannel().complete();
                }
                user.openPrivateChannel().queue(success ->
                        success.sendMessage(String.format("Hi %s, I noticed recently you have lost your supporter status. As a result I have cleared your settings, however as a non-supporter you can add up to 3 pokemon and 3 raids to your settings", user.getAsMention())).queue()
                );
                DBManager.resetUser(userID);
                MessageListener.roleLog.sendMessage(String.format("%s has lost supporter status with more than 3 raids in their settings. Their settings have been reset and they have been PMed", user.getAsMention())).queue();
            }else {
                final MessageBuilder builder = new MessageBuilder();
                builder.setEmbed(message.getEmbeds().get(0));
                novabotLog.log(DEBUG,String.format("Notifying user: %s%n", userID));
                Member member = MessageListener.guild.getMemberById(userID);

                if(member == null) return;

                final User user2 = member.getUser();
                if (!user2.hasPrivateChannel()) {
                    user2.openPrivateChannel().complete();
                }
                user2.openPrivateChannel().queue(success -> success.sendMessage(builder.build()).queue());
            }
        }
    }

    public static boolean isSupporter(final String userID) {
        final Member member = MessageListener.guild.getMemberById(userID);

        if(member == null || member.getRoles() == null) return false;

        for (final Role role : member.getRoles()) {
            if(role == null) continue;
            if(config.getSupporterRoles().contains(role.getId())) return true;
        }
        return false;
    }

    private void parseMsg(final String msg, final User author, final MessageChannel channel) {

        if (!msg.startsWith("!")) {
            return;
        }

        if(config.isSupporterOnly() && !isSupporter(author.getId())) {
            channel.sendMessage(author.getAsMention() + ", sorry, I can only accept commands from supporters").queue();
            return;
        }

        if (config.nestsEnabled() && msg.startsWith("!nest")) {
            channel.sendMessage(author.getAsMention() + " I only accept nest commands in the " + MessageListener.guild.getTextChannelsByName("nests", true).get(0).getAsMention() + " channel or via PM").queue();
            return;
        }

        if(msg.equals("!reload")) {
            if (isAdmin(author)) {
                loadConfig();
                loadSuburbs();
            }
            return;
        }

        if(msg.equals("!pause")){
            DBManager.pauseUser(author.getId());
            channel.sendMessage(String.format("%s, your notifications are currently PAUSED. " +
                    "You will not receive any notifications until you use the `!unpause` command.",author.getAsMention())).queue();
            return;
        }

        if(msg.equals("!unpause")){
            DBManager.unPauseUser(author.getId());
            channel.sendMessage(String.format("%s, your notifications are currently UNPAUSED. " +
                    "You will receive notifications until you use the `!pause` command.",author.getAsMention())).queue();
            return;
        }

        if(msg.startsWith("!joinraid")){
            String groupCode = msg.substring(msg.indexOf("raid ") + 5).trim();

            RaidLobby lobby = lobbyManager.getLobby(groupCode);

            if(lobby == null){
                channel.sendMessageFormat("%s sorry, there are no active raid lobbies with the lobby code `%s`",author,groupCode).queue();
                return;
            }else{
                if(lobby.containsUser(author.getId())){
                    channel.sendMessageFormat("%s you are already in that raid lobby!",author).queue();
                    return;
                }

                lobby.joinLobby(author.getId());

                alertRaidChats(config.getRaidChats(lobby.spawn.getGeofences()),String.format(
                        "%s joined %s raid in %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
                        author.getAsMention(),
                        (lobby.spawn.bossId == 0 ? String.format("lvl %s egg",lobby.spawn.raidLevel) : lobby.spawn.properties.get("pkmn")),
                        lobby.getChannel().getAsMention(),
                        lobby.memberCount(),
                        lobby.lobbyCode
                ));

//                if(lobby.spawn.bossId == 249){
//                    jda.getTextChannelById(338321961040084992L).sendMessageFormat(
//                            "%s joined %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
//                            guild.getMember(author).getAsMention(),
//                            lobby.getChannel().getAsMention(),
//                            lobby.memberCount(),
//                            lobby.lobbyCode).queue(m->m.addReaction(WHITE_GREEN_CHECK).queue());
//                }

                channel.sendMessageFormat("%s you have been placed in %s. There are now %s users in the lobby.",author,lobby.getChannel(),lobby.memberCount()).queue();
            }

            return;
        }

        if (msg.equals("!settings")) {
            final UserPref userPref = DBManager.getUserPref(author.getId());
            novabotLog.log(DEBUG,"!settings");
            if (userPref == null || (userPref.isRaidEmpty() && userPref.isPokeEmpty())) {
                channel.sendMessage(author.getAsMention() + ", you don't have any notifications set. Add some with the !addpokemon or !addraid commands.").queue();
            }
            else {
                String toSend = author.getAsMention() + ", you are currently set to receive notifications for:\n\n";
                toSend += userPref.allSettingsToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        }else if (msg.equals("!pokemonsettings")){
            final UserPref userPref = DBManager.getUserPref(author.getId());
            novabotLog.log(DEBUG,"!pokemonsettings");
            if (userPref == null || userPref.isPokeEmpty()) {
                channel.sendMessage(author.getAsMention() + ", you don't have any pokemon notifications set. Add some with the !addpokemon command.").queue();
            }
            else {
                String toSend = author.getAsMention() + ", you are currently set to receive notifications for:\n\n";
                toSend += userPref.allPokemonToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        }else if(msg.equals("!raidsettings")){
            final UserPref userPref = DBManager.getUserPref(author.getId());
            novabotLog.log(DEBUG,"!raidsettings");
            if (userPref == null || userPref.isRaidEmpty()) {
                channel.sendMessage(author.getAsMention() + ", you don't have any raid notifications set. Add some with the !addraid command.").queue();
            }
            else {
                String toSend = author.getAsMention() + ", you are currently set to raid receive notifications for:\n\n";
                toSend += userPref.allRaidsToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        }
        else if (msg.equals("!reset")) {
            DBManager.resetUser(author.getId());
            channel.sendMessage(author.getAsMention() + ", all your notification settings have been reset").queue();
            return;
        }else if(msg.equals("!resetpokemon")){
            DBManager.resetPokemon(author.getId());
            channel.sendMessage(author.getAsMention() + ", your pokemon notification settings have been reset").queue();
            return;
        }else if(msg.equals("!resetraids")){
            DBManager.resetRaids(author.getId());
            channel.sendMessage(author.getAsMention() + ", your raid notification settings have been reset").queue();
            return;
//        }else if(msg.equals("!activeraids")){
//            ArrayList<GeofenceIdentifier> geofences = config.getRaidChatGeofences(channel.getId());
//
//            if(geofences.size() > 0 || channel.getType() == ChannelType.PRIVATE){
//
//                String noLobbiesMsg = null;
//
//                for (GeofenceIdentifier geofence : geofences) {
//
//                    ArrayList<RaidLobby> lobbies = lobbyManager.getLobbiesByGeofence(geofence);
//
//                    if(lobbies.size() == 0){
//                        if(noLobbiesMsg == null){
//                            noLobbiesMsg = String.format("%s, there are no active lobbies in %s", author.getAsMention(),geofence.name);
//                        }else{
//                            noLobbiesMsg += String.format(", %s",geofence.name);
//                        }
//                        continue;
//                    }
//
//                    channel.sendMessageFormat("%s, there are %s active lobbies in %s",author,lobbies.size(),geofence.name).queue();
//
//                    for (RaidLobby lobby : lobbies) {
//                        channel.sendMessage(lobby.getInfoMessage()).queue(m -> m.addReaction(WHITE_GREEN_CHECK).queue());
//                    }
//                }
//
//                if(noLobbiesMsg != null){
//                    channel.sendMessage(noLobbiesMsg).queue();
//                }
//
//                return;
//            }else{
//                channel.sendMessageFormat(
//                        "%s, I only accept the `!activeraids` command in PM or the following raid discussion channels:%n%n" +
//                        "%s",author,config.raidChatsList()).queue();
//                return;
//            }
        }
        else if (msg.equals("!help")) {
            channel.sendMessage("My commands are: \n" +
                    (config.pokemonEnabled() ?
                            "**Pokemon Commands:**" +
                            "```!addpokemon <pokemon list> <miniv,maxiv> <location list>\n" +
                            "!addpokemon pokemon\n" +
                            "!delpokemon <pokemon list> <miniv,maxiv> <location list>\n" +
                            "!delpokemon pokemon\n" +
                            "!clearpokemon <pokemon list>\n" +
                            "!clearpokelocation <location list>\n" +
                            "!pokemonsettings\n" +
                            "!resetpokemon```" : "") +
                    (config.raidsEnabled() ?
                            "**Raid Commands:**" +
                            "```!addraid pokemon\n" +
                            "!addraid <pokemon list> <location list>\n" +
                            "!delraid pokemon\n" +
                            "!delraid <pokemon list> <location list>\n" +
                            "!clearraid <pokemon list>\n" +
                            "!clearraidlocation <location list>\n" +
                            "!raidsettings\n" +
                            "!resetraids```" : "") +
                    "**Other Commands:**" +
                    "```!clearlocation <location list>\n" +
                    "!pause\n" +
                    "!unpause\n" +
                    (config.statsEnabled() ? "!stats <pokemon list> <integer> <unit of time>\n" : "") +
                    (config.isRaidOrganisationEnabled()
                            && config.getRaidChatGeofences(channel.getLatestMessageId()).size() > 0
                            || channel.getType() == ChannelType.PRIVATE
                            ? "!joinraid <lobby code>\n"
                            : "") +
                    (config.isRaidOrganisationEnabled()
                            && config.getRaidChatGeofences(channel.getLatestMessageId()).size() > 0
                            || channel.getType() == ChannelType.PRIVATE
                            ? "!!activeraids\n"
                            : "") +
                    "!reset\n" +
                    "!settings\n" +
                    (config.useChannels() ? "!channellist or !channels\n" : "") +
                    (config.useGeofences() ? "!regionlist or !regions\n" : "") +
                    "!help```").queue();
            return;
        }
        else if (config.useChannels() &&(msg.equals("!channellist") || msg.equals("!channels"))) {
            channel.sendMessageFormat("%s, accepted channels are: ```%s```",author,FeedChannels.getListMessage()).queue();
            return;
        }else if (config.useGeofences() &&(msg.equals("!regionlist") || msg.equals("!regions"))) {
            channel.sendMessageFormat("%s, accepted regions are: ```%s```",author,Geofencing.getListMessage()).queue();
            return;
        }

        UserCommand userCommand;

        if(msg.startsWith("!addraid") || msg.startsWith("!delraid") || msg.startsWith("!clearraidlocation")) {
            userCommand  = Parser.parseInput(msg,true);
        }else {
            userCommand  = Parser.parseInput(msg, isSupporter(author.getId()));
        }
        final ArrayList<InputError> exceptions = userCommand.getExceptions();

        if (exceptions.size() > 0) {
            String errorMessage = author.getAsMention() + ", I had " + ((exceptions.size() == 1) ? "a problem" : "problems") + " reading your input.\n\n";
            final InputError error = InputError.mostSevere(exceptions);
            errorMessage += error.getErrorMessage(userCommand);
            channel.sendMessage(errorMessage).queue();
        }
        else {
            final String cmdStr = (String)userCommand.getArg(0).getParams()[0];

            if(cmdStr.equals("!stats")){
                Pokemon[] pokemons = userCommand.buildPokemon();

                String str = author.getAsMention() + ", here's what I found:\n\n";

                for (Pokemon pokemon : pokemons) {

                    core.TimeUnit timeUnit = (core.TimeUnit) userCommand.getArg(ArgType.TimeUnit).getParams()[0];

                    int intervalLength = (int) userCommand.getArg(ArgType.Int).getParams()[0];

                    int count = DBManager.countSpawns(pokemon.getID(),timeUnit,intervalLength);

                    str+= String.format("  %s %s%s have been seen in the last %s %s%n%n",
                            count,
                            pokemon.name,
                            count == 1 ? "" : "s",
                            intervalLength,
                            timeUnit.toString().toLowerCase());

                }

                channel.sendMessage(str).queue();

                return;
            }

            if(cmdStr.contains("raid")){
                Raid[] raids = userCommand.buildRaids();

                ArrayList<String> nonRaidBosses = new ArrayList<>();

                for (Pokemon pokemon : userCommand.getUniquePokemon()) {
                    if(!Raid.POSSIBLE_BOSSES.contains(pokemon.getID())){
                        nonRaidBosses.add(Pokemon.idToName(pokemon.getID()));
                    }
                }

                if(nonRaidBosses.size() != 0){
                    String message = author.getAsMention() + " I had a problem reading your input.\n\n" +
                            "The following pokemon you entered are not possible raid bosses:\n\n";

                    for (String nonRaidBoss : nonRaidBosses) {
                        message += String.format("  %s%n", nonRaidBoss);
                    }

                    channel.sendMessage(message).queue();
                    return;
                }

                if(cmdStr.equals("!addraid")){
                    NotificationLimit limit = config.getNotificationLimit(guild.getMember(author));

                    boolean isSupporter = isSupporter(author.getId());
                    if ((!isSupporter || limit == null) &&
                            DBManager.countRaids(author.getId(), config.countLocationsInLimits()) + raids.length
                                    > config.getNonSupporterLimit().raidLimit) {
                        channel.sendMessageFormat("%s as a non-supporter, you may have a maximum of %s raid notifications set up. " +
                                "What you tried to add would put you over this limit, please remove some raids with the !delraid command or try adding fewer raids.",
                                author,config.getNonSupporterLimit().raidLimit).queue();
                        return;
                    }

                    if(limit != null && limit.raidLimit != -1 && DBManager.countRaids(author.getId(), config.countLocationsInLimits()) + raids.length > limit.raidLimit) {
                        channel.sendMessageFormat("%s at your supporter level you may have a maximum of %s raid notifications set up. " +
                                "What you tried to add would take you over this limit, please remove some raids with the !delraid command or try adding fewer raids.",author,limit.raidLimit).queue();
                        return;
                    }else if(limit == null && isSupporter){
                        novabotLog.fatal(String.format("LIMIT IS NULL: %s, is supporter: %s", author.getName(),isSupporter));
                    }

                    if(!DBManager.containsUser(author.getId())){
                        DBManager.addUser(author.getId());
                    }

                    for (Raid raid : raids) {
                        novabotLog.log(DEBUG,"adding raid " + raid);
                        DBManager.addRaid(author.getId(),raid);
                    }

                    String message2 = author.getAsMention() + " you will now be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());

                    final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                    Location[] locations = {new Location(Region.All)};
                    if (locationsArg != null) {
                        locations = userCommand.getLocations();
                    }
                    message2 = message2 + " raids in " + Location.listToString(locations);
                    channel.sendMessage(message2).queue();

                    return;
                }else if(cmdStr.equals("!delraid")){
                    if(!DBManager.containsUser(author.getId())){
                        DBManager.addUser(author.getId());
                    }

                    for (Raid raid : raids) {
                        novabotLog.log(DEBUG,"deleting raid " + raid);
                        DBManager.deleteRaid(author.getId(),raid);
                    }

                    String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());

                    final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                    Location[] locations = {new Location(Region.All)};
                    if (locationsArg != null) {
                        locations = userCommand.getLocations();
                    }
                    message2 = message2 + " raids in " + Location.listToString(locations);
                    channel.sendMessage(message2).queue();

                    return;
                }else if(cmdStr.equals("!clearraid")){
                    if(!DBManager.containsUser(author.getId())){
                        DBManager.addUser(author.getId());
                    }
                    novabotLog.log(DEBUG,"clearing raids " + raids);
                    DBManager.clearRaid(author.getId(),new ArrayList<>(Arrays.asList(raids)));

                    String message2 = String.format("%s you will no longer be notified of %s in any location", author.getAsMention(), Pokemon.listToString(userCommand.getUniquePokemon()));
                    channel.sendMessage(message2).queue();
                    return;
                }else if(cmdStr.equals("!clearraidlocation")){
                    final Location[] locations2 = userCommand.getLocations();
                    DBManager.clearLocationsRaids(author.getId(), locations2);
                    final String message2 = author.getAsMention() + " you will no longer be notified of any raids in " + Location.listToString(locations2);
                    channel.sendMessage(message2).queue();
                }
            }

            if (cmdStr.contains("pokemon")) {
                final Pokemon[] pokemons = userCommand.buildPokemon();

                switch (cmdStr) {
                    case "!addpokemon": {
                        NotificationLimit limit = config.getNotificationLimit(guild.getMember(author));

                        boolean isSupporter = isSupporter(author.getId());
                        if ((!isSupporter || limit == null)
                                && DBManager.countPokemon(author.getId(), config.countLocationsInLimits()) + pokemons.length
                                > config.getNonSupporterLimit().pokemonLimit) {
                            channel.sendMessageFormat("%s as a non-supporter, you may have a maximum of %s pokemon notifications set up." +
                                    " What you tried to add would put you over this limit, please remove some pokemon with the !delpokemon command or try adding fewer pokemon.",
                                    author,config.getNonSupporterLimit().pokemonLimit).queue();
                            return;
                        }

                        if(limit != null && limit.pokemonLimit != -1 && DBManager.countPokemon(author.getId(), config.countLocationsInLimits()) + pokemons.length > limit.pokemonLimit) {
                            channel.sendMessageFormat("%s at your supporter level you may have a maximum of %s pokemon notifications set up. " +
                                    "What you tried to add would take you over this limit, please remove some pokemon with the !delpokemon command or try adding fewer pokemon.",author,limit.pokemonLimit).queue();
                            return;
                        }else if(limit == null && isSupporter){
                            novabotLog.fatal(String.format("LIMIT IS NULL: %s, is supporter: %s", author.getName(),isSupporter));
                        }

                        if (!DBManager.containsUser(author.getId())) {
                            DBManager.addUser(author.getId());
                        }
                        for (final Pokemon pokemon : pokemons) {
                            novabotLog.log(DEBUG,"adding pokemon " + pokemon);
                            DBManager.addPokemon(author.getId(), pokemon);
                        }
                        String message2 = author.getAsMention() + " you will now be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());
                        message2 += userCommand.getIvMessage();

                        final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                        Location[] locations = {new Location(Region.All)};
                        if (locationsArg != null) {
                            locations = userCommand.getLocations();
                        }
                        message2 = message2 + " in " + Location.listToString(locations);
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!delpokemon": {
                        for (final Pokemon pokemon : pokemons) {
                            DBManager.deletePokemon(author.getId(), pokemon);
                        }
                        String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());
                        message2 += userCommand.getIvMessage();

                        final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                        Location[] locations = {new Location(Region.All)};
                        if (locationsArg != null) {
                            locations = userCommand.getLocations();
                        }
                        message2 = message2 + " in " + Location.listToString(locations);
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!clearpokemon": {
                        DBManager.clearPokemon(author.getId(), new ArrayList<>(Arrays.asList(pokemons)));
                        final String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(pokemons) + " in any channels";
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!clearpokelocation": {
                        final Location[] locations2 = userCommand.getLocations();
                        DBManager.clearLocationsPokemon(author.getId(), locations2);
                        final String message2 = author.getAsMention() + " you will no longer be notified of any pokemon in " + Location.listToString(locations2);
                        channel.sendMessage(message2).queue();
                    }
                }
            }else if(cmdStr.equals("!clearlocation")){
                final Location[] locations2 = userCommand.getLocations();
                DBManager.clearLocationsPokemon(author.getId(), locations2);
                DBManager.clearLocationsRaids(author.getId(), locations2);
                final String message2 = author.getAsMention() + " you will no longer be notified of any pokemon or raids in " + Location.listToString(locations2);
                channel.sendMessage(message2).queue();
            }
        }
    }

    static {
        MessageListener.testing = false;
    }
}
