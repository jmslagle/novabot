package core;

import nests.Nest;
import nests.NestSearch;
import nests.NestSheetManager;
import nests.NestStatus;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import notifier.Notifier;
import org.ini4j.Ini;
import parser.*;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter
{
    private static final String NEST_FB_GROUP = "https://www.facebook.com/groups/PogoCBRNests/";
    private static final String NEST_MAP = "http://www.google.com/maps/d/u/0/viewer?mid=1d-QuaDK1tJRiHKODXErTQIDqIAY";
    private static TextChannel roleLog;
    private static TextChannel nestsReports;
    public static Guild guild;
    private static final ReporterChannels reporterChannels;
    public static boolean testing;
    private final String regionHelp = "Accepted channels are:\n\nall\nwodenweston = woden-weston = woden-weston-region = woden-weston-supporter\ngungahlin = gungahlin-region = gungahlin-supporter\ninnernorth = inner-north = inner-north-region = inner-north-supporter\nbelconnen = belconnen-region = belconnen-supporter\ninnersouth = inner-south = inner-south-region = inner-south-supporter\ntuggeranong = tuggeranong-region = tuggeranong-supporter\nqueanbeyan = queanbeyan-region = queanbeyan-supporter\nlegacy = legacyrare = legacy-rare = legacy-rare-supporter\nlarvitar = larvitarcandy = larvitar-candy = larvitar-candy-supporter\ndratini = dratinicandy = dratini-candy = dratini-candy-supporter\nmareep = mareepcandy = mareep-candy = mareep-candy-supporter\nultrarare = ultra-rare = ultra-rare-supporter\n100iv = 100-iv = 100% = 100-iv-supporter\nsnorlax = snorlax-supporter\nevent\n0iv = 0-iv = 0% = 0-iv-supporter\ndexfiller = dex-filler\nbigfishlittlerat = big-fish-little-rat = big-fish-little-rat-cardboard-box\n";
    private final String inputFormat = "```!addpokemon <pokemon1, pokemon2, pokemon3> <channel1, channel2, channel3>```\nFor as many pokemon and channels as you want. Make sure you include the <>. For more information on regions use the !channellis command";
    private final String nestHelp = "My nest commands are: \n```!nest <pokemon list> <status list>\n!nest pokemon status\n!nest pokemon\n!reportnest [your text here]\n!confirmed\n!suspected\n!fb or !nestfb\n!map or !nestmap\n!help\n```";
    private final String helpStr = "My commands are: \n```!addpokemon <pokemon list> <miniv,maxiv> <location list>\n!addpokemon pokemon\n!delpokemon <pokemon list> <miniv,maxiv> <location list>\n!delpokemon pokemon\n!clearpokemon <pokemon list>\n!clearlocation <location list>\n!reset\n!settings\n!help\n!channellist or !channels```";
    private static MessageChannel userUpdatesLog;


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Config config;
    public static SuburbManager suburbs;

    HashMap<String,Message> messageMap = new HashMap<>();

    public static void main(final String[] args) {

        testing = true;

        loadConfig();
        loadSuburbs();

        System.out.println("Connecting to db");
//        if (MessageListener.testing) {
//            DBManager.novabotdbConnect("root", "mimi");
//            DBManager.rocketmapdbConnect("root", "mimi");
//        }
//        else {
            DBManager.rocketmapdbConnect();
            DBManager.novabotdbConnect();
//        }
        System.out.println("Connected");
        try {
            final JDA jda = new JDABuilder(AccountType.BOT)
                    .setAutoReconnect(true)
                    .setGame(Game.of("Pokemon Go"))
                    .setToken(config.getToken())
                    .addEventListener(new MessageListener()).buildBlocking();
            final Iterator<Guild> iterator = jda.getGuilds().iterator();
            while (iterator.hasNext()) {
                final Guild guild = MessageListener.guild = iterator.next();
                System.out.println(guild.getName());

                TextChannel channel = guild.getTextChannelById(config.getCommandChannelId());
                if(channel != null && config.showStartupMessage()){
                    channel.sendMessage("I'm awake again!").queue();
                }else{
                    System.out.println("couldn't find command channel by id specified in config");
                }
//                if (guild.getTextChannelsByName(MessageListener.testing ? "novabot-testing" : "novabot", true).size() > 0) {
//                    final TextChannel channel = guild.getTextChannelsByName(MessageListener.testing ? "novabot-testing" : "novabot", true).get(0);
//                    System.out.println(channel.getName());
//                    channel.sendMessage("Hi! I'm awake again!").queue();
//                }
            }

            if(config.loggingEnabled()) {
                MessageListener.roleLog = MessageListener.guild.getTextChannelById(config.getRoleLogId());
                MessageListener.userUpdatesLog = MessageListener.guild.getTextChannelById(config.getUserUpdatesId());
            }

            if(config.nestsEnabled()) {
                MessageListener.nestsReports = MessageListener.guild.getTextChannelsByName("nests-reports", true).get(0);
            }

            if(!testing) {

                final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(new Notifier(jda, testing), 0L, config.getPollingRate(), TimeUnit.SECONDS);
            }
        }
        catch (LoginException | InterruptedException | RateLimitedException ex2) {
            ex2.printStackTrace();
        }



        System.out.println("connected");
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
    public void onMessageDelete(MessageDeleteEvent event) {
        if(!config.loggingEnabled()) return;

        JDA jda = event.getJDA();
        final String id = event.getMessageId();
        TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());


        Message foundMessage = messageMap.get(id);

        if(foundMessage == null){
            MessageListener.userUpdatesLog.sendMessage("The message could not be retrieved from the log").queue();
            return;
        }

        final User[] deleter = {foundMessage.getAuthor()};

        System.out.println(String.format("Searching audit logs for message ID %s", id));
        guild.getAuditLogs().queue(
                success ->  guild.getAuditLogs().type(ActionType.MESSAGE_DELETE).forEach(entry -> {
                                System.out.println(entry);
                                if (entry.getTargetId().equals(id)) {
                                    deleter[0] = entry.getUser();
                                }
                            }
                ));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("A message was deleted from %s", channel.getName()),null);
        embedBuilder.addField("Deleted By",deleter[0].getAsMention(),true);
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
            roleStr = roleStr + role.getName() + " ";
        }
        MessageListener.roleLog.sendMessage(user.getAsMention() + " had " + roleStr + "role(s) added").queue();
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        if(!config.loggingEnabled()) return;

        final User user = event.getMember().getUser();
        String roleStr = "";
        for (final Role role : event.getRoles()) {
            roleStr = roleStr + role.getName() + " ";
        }
        MessageListener.roleLog.sendMessage(user.getAsMention() + " had " + roleStr + "role(s) removed").queue();
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        if(!config.loggingEnabled()) return;

        final JDA jda = event.getJDA();
        final User user = event.getMember().getUser();
        MessageListener.userUpdatesLog.sendMessage(
                user.getAsMention() +
                        " joined. The account was created " +
                        user.getCreationTime().atZoneSameInstant(ZoneId.of(config.getTimeZone())).format(formatter)).queue();
        DBManager.logNewUser(user.getId());

        if(guild.getMember(user).getEffectiveName().equalsIgnoreCase("novabot") && !user.isBot()){
            Member member = guild.getMember(user);
            guild.getController().kick(member).queue(
                    success -> userUpdatesLog.sendMessage("Kicked " + member.getEffectiveName() + " because their name was `novabot`").queue()
            );
        }
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
        final JDA jda = event.getJDA();
        final User author = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel channel = event.getChannel();
        final String msg = message.getContent();

        messageMap.put(message.getId(),message);

        if (event.isFromType(ChannelType.TEXT)) {
            final Guild guild = event.getGuild();
            final TextChannel textChannel = event.getTextChannel();
            MessageListener.guild = guild;

            if(channel.getId().equals(config.getCommandChannelId())){
                this.parseMsg(msg.toLowerCase(), author, textChannel);
            }
            else if (config.nestsEnabled() && channel.getName().equals(MessageListener.testing ? "nests-testing" : "nests")) {
                this.parseNestMsg(msg.toLowerCase().trim(), author, channel, event.getChannelType());
            }
            else if (!config.isSupporterOnly()){
                if (MessageListener.testing) {
                    if (message.isWebhookMessage()) {
                        return;
                    }
                }
                else if (!message.isWebhookMessage()) {
                    return;
                }
                if (!channel.getName().endsWith("supporter") && MessageListener.reporterChannels.containsChannel(channel.getName())) {
                    final Region channelRegion = ReporterChannels.getRegionByName(channel.getName());
                    System.out.println("Converted channel name to region: " + channelRegion);
                    final String msgTitle = message.getEmbeds().get(0).getTitle();
                    final int suburbStart = msgTitle.indexOf("[") + 1;
                    final int suburbEnd = msgTitle.indexOf("]");
                    final String suburb = msgTitle.substring(suburbStart, suburbEnd);
                    final int pokeStart = suburbEnd + 2;
                    final int pokeEnd;
                    if(!msgTitle.contains("Unown")){
                        pokeEnd = msgTitle.length();
                    }else{
                        pokeEnd = msgTitle.substring(pokeStart).indexOf(" ") + pokeStart;
                    }
                    final String pokeName = msgTitle.substring(pokeStart, pokeEnd).toLowerCase().trim();
                    String form = null;
                    if (pokeName.equals("Unown")) {
                        final int formStart = msgTitle.substring(pokeEnd).indexOf("[") + pokeEnd;
                        form = msgTitle.substring(formStart, formStart + 1);
                    }
                    final String msgBody = message.getEmbeds().get(0).getDescription();
                    final int timeEnd = msgBody.indexOf(")**");

                    final int ivStart = msgBody.substring(timeEnd).indexOf("(") + timeEnd + 1;
                    final int ivEnd = msgBody.indexOf("%");
                    final String ivStr = msgBody.substring(ivStart,ivEnd);
                    final float pokeIV = ivStr.equals("?") ? 0 : Float.parseFloat(msgBody.substring(ivStart, ivEnd));

                    final int cpStart = msgBody.indexOf("CP: ") + 4;
                    final int cpEnd = msgBody.substring(cpStart).indexOf("Lvl") + cpStart;
                    final String cpStr = msgBody.substring(cpStart,cpEnd).trim();
                    final int cp = cpStr.equals("?") ? 0 : Integer.parseInt(cpStr);

                    final int moveSetStart = msgBody.indexOf("Moveset: ") + 9;
                    final int moveSetEnd = channel.getName().endsWith("-supporter") ? msgBody.indexOf("Gender: ") : msgBody.length();
                    final int moveSetSplit = msgBody.substring(moveSetStart, moveSetEnd).indexOf("-") + moveSetStart;

                    final String move_1 = msgBody.substring(moveSetStart, moveSetSplit).trim().toLowerCase();
                    final String move_2 = msgBody.substring(moveSetSplit + 2, moveSetEnd).trim().toLowerCase();

                    final PokeSpawn pokeSpawn = new PokeSpawn(Pokemon.nameToID(pokeName), suburb, pokeIV, move_1, move_2, form, cp);
                    System.out.println(pokeSpawn.toString());
                    final ArrayList<String> ids = DBManager.getUserIDsToNotify(pokeSpawn);

                    if(!testing){
                        if (channelRegion == Region.Event) {
                            this.alertPublic(message, ids);
                        }
                        else if (!channel.getName().endsWith("-supporter")) {
                            System.out.println("alerting non-supporters");
                            this.alertPublic(message, ids);
                        }
                    }else{
                        jda.getUserById("107730875596169216").getPrivateChannel().sendMessage(message).queue();
                    }
                }
            }
        }
        else if (event.isFromType(ChannelType.PRIVATE)) {
            final PrivateChannel privateChannel = event.getPrivateChannel();
            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);

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
                    this.parseMsg(msg.toLowerCase(), author, privateChannel);
                }
            }
        }
        else if (event.isFromType(ChannelType.GROUP)) {
            final Group group = event.getGroup();
            final String groupName = (group.getName() != null) ? group.getName() : "";
            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
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
                    builder.append((CharSequence)author.getAsMention()).append((CharSequence)"");
                    final ArrayList<Nest> nests = NestSheetManager.getNestsByStatus(new NestStatus[] { NestStatus.Confirmed });
                    if (nests.size() == 0) {
                        builder.append((CharSequence)" sorry, I couldn't find any confirmed nests");
                        channel.sendMessage(builder.build()).queue();
                    }
                    else {
                        if (channelType != ChannelType.PRIVATE) {
                            channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                        }
                        builder.append((CharSequence)" I found ").append(nests.size()).append((CharSequence)" confirmed nests:\n\n");
                        for (final Nest nest : nests) {
                            builder.append(nest).append((CharSequence)"\n");
                            builder.append((CharSequence)"<").append((CharSequence)nest.getGMapsLink()).append((CharSequence)">\n\n");
                        }
                        if (!author.hasPrivateChannel()) {
                            author.openPrivateChannel().complete();
                        }
                        for (final Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            author.getPrivateChannel().sendMessage(message).queue();
                        }
                    }
                    return;
                }
                if (msg.equals("!suspected")) {
                    final MessageBuilder builder = new MessageBuilder();
                    builder.append((CharSequence)author.getAsMention()).append((CharSequence)"");
                    final ArrayList<Nest> nests = NestSheetManager.getNestsByStatus(new NestStatus[] { NestStatus.Suspected });
                    if (nests.size() == 0) {
                        builder.append((CharSequence)" sorry, I couldn't find any suspected nests");
                        channel.sendMessage(builder.build()).queue();
                    }
                    else {
                        if (channelType != ChannelType.PRIVATE) {
                            channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                        }
                        builder.append((CharSequence)" I found ").append(nests.size()).append((CharSequence)" suspected nests:\n\n");
                        for (final Nest nest : nests) {
                            builder.append(nest).append((CharSequence)"\n");
                            builder.append((CharSequence)"<").append((CharSequence)nest.getGMapsLink()).append((CharSequence)">\n\n");
                        }
                        if (!author.hasPrivateChannel()) {
                            author.openPrivateChannel().complete();
                        }
                        for (final Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            author.getPrivateChannel().sendMessage(message).queue();
                        }
                    }
                    return;
                }
                final UserCommand userCommand = Parser.parseInput(msg);
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
                builder2.append((CharSequence)author.getAsMention()).append((CharSequence)"");
                final ArrayList<Nest> nests2 = NestSheetManager.getNests(nestSearch);
                if (nests2.size() == 0) {
                    builder2.append((CharSequence)" sorry, I couldn't find any ").append((CharSequence)NestStatus.listToString(nestSearch.getStatuses())).append((CharSequence)" nests for ").append((CharSequence)Pokemon.listToString(nestSearch.getPokemon()));
                    channel.sendMessage(builder2.build()).queue();
                    break;
                }
                if (channelType != ChannelType.PRIVATE) {
                    channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                }
                builder2.append((CharSequence)" I found ").append(nests2.size()).append((CharSequence)" results for ").append((CharSequence)NestStatus.listToString(nestSearch.getStatuses())).append((CharSequence)" ").append((CharSequence)Pokemon.listToString(nestSearch.getPokemon())).append((CharSequence)" nests :\n\n");
                for (final Pokemon poke : nestSearch.getPokemon()) {
                    builder2.append((CharSequence)"**").append((CharSequence)poke.name).append((CharSequence)"**\n");
                    boolean foundPoke = false;
                    for (final Nest nest2 : nests2) {
                        if (nest2.pokemon.name.equals(poke.name)) {
                            foundPoke = true;
                            builder2.append((CharSequence)"  ").append(nest2).append((CharSequence)"\n");
                            builder2.append((CharSequence)"  <").append((CharSequence)nest2.getGMapsLink()).append((CharSequence)">\n\n");
                        }
                    }
                    if (!foundPoke) {
                        builder2.append((CharSequence)"  No results found\n");
                    }
                }
                if (!author.hasPrivateChannel()) {
                    author.openPrivateChannel().complete();
                }
                for (final Message message2 : builder2.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                    author.getPrivateChannel().sendMessage(message2).queue();
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
            if (DBManager.countPokemon(userID) > 3) {
                final User user = MessageListener.guild.getMemberById(userID).getUser();
                if (!user.hasPrivateChannel()) {
                    user.openPrivateChannel().complete();
                }
                user.getPrivateChannel().sendMessage("Hi " + user.getAsMention() + ", I noticed recently you have lost your supporter status. As a result I have cleared your settings, however as a non-supporter you can add up to 3 pokemon to your settings").queue();
                DBManager.resetUser(userID);
                MessageListener.roleLog.sendMessage(user.getAsMention() + " has lost supporter status with more than 3 pokemon in their settings. Their settings have been reset and they have been PMed").queue();
            }
            else {
                final MessageBuilder builder = new MessageBuilder();
                builder.setEmbed(message.getEmbeds().get(0));
                System.out.println("Notifying user: " + userID);
                final User user2 = MessageListener.guild.getMemberById(userID).getUser();
                if (!user2.hasPrivateChannel()) {
                    user2.openPrivateChannel().complete();
                }
                user2.getPrivateChannel().sendMessage(builder.build()).queue();
            }
        }
    }

    public static boolean isSupporter(final String userID) {
        final Member member = MessageListener.guild.getMemberById(userID);

        for (final Role role : member.getRoles()) {
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

        if (msg.equals("!settings")) {
            final UserPref userPref = DBManager.getUserPref(author.getId());
            System.out.println("!settings");
            if (userPref == null || userPref.isEmpty()) {
                channel.sendMessage(author.getAsMention() + ", you don't have any notifications set. Add some with the !addpokemon command.").queue();
            }
            else {
                String toSend = author.getAsMention() + ", you are currently set to receive notifications for:\n\n";
                toSend += userPref.allPokemonToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append((CharSequence)toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        }
        else if (msg.equals("!reset")) {
            DBManager.resetUser(author.getId());
            channel.sendMessage(author.getAsMention() + ", your notification settings have been reset").queue();
            return;
        }
        else if (msg.equals("!help")) {
            channel.sendMessage("My commands are: \n" +
                    "```!addpokemon <pokemon list> <miniv,maxiv> <location list>\n" +
                    "!addpokemon pokemon\n" +
                    "!delpokemon <pokemon list> <miniv,maxiv> <location list>\n" +
                    "!delpokemon pokemon\n" +
                    "!clearpokemon <pokemon list>\n" +
                    "!clearlocation <location list>\n" +
                    (config.statsEnabled() ? "!stats <pokemon list> <integer> <unit of time>\n" : "") +
                    "!reset\n" +
                    "!settings\n" +
                    (config.useGeofences() ? "!channellist or !channels\n" : "") +
                    "!help```").queue();
            return;
        }
        else if (config.useGeofences() &&(msg.equals("!channellist") || msg.equals("!channels"))) {
            channel.sendMessage("Accepted channels are:\n\nall\nwodenweston = woden-weston = woden-weston-region = woden-weston-supporter\ngungahlin = gungahlin-region = gungahlin-supporter\ninnernorth = inner-north = inner-north-region = inner-north-supporter\nbelconnen = belconnen-region = belconnen-supporter\ninnersouth = inner-south = inner-south-region = inner-south-supporter\ntuggeranong = tuggeranong-region = tuggeranong-supporter\nqueanbeyan = queanbeyan-region = queanbeyan-supporter\nlegacy = legacyrare = legacy-rare = legacy-rare-supporter\nlarvitar = larvitarcandy = larvitar-candy = larvitar-candy-supporter\ndratini = dratinicandy = dratini-candy = dratini-candy-supporter\nmareep = mareepcandy = mareep-candy = mareep-candy-supporter\nultrarare = ultra-rare = ultra-rare-supporter\n100iv = 100-iv = 100% = 100-iv-supporter\nsnorlax = snorlax-supporter\nevent\n0iv = 0-iv = 0% = 0-iv-supporter\ndexfiller = dex-filler\nbigfishlittlerat = big-fish-little-rat = big-fish-little-rat-cardboard-box\n").queue();
            return;
        }

        final UserCommand userCommand = Parser.parseInput(msg);
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

            if (cmdStr.contains("pokemon")) {
                final Pokemon[] pokemons = userCommand.buildPokemon();

                if (cmdStr.equals("!addpokemon")) {
                    if (!isSupporter(author.getId()) && DBManager.countPokemon(author.getId()) + pokemons.length > 3) {
                        channel.sendMessage(author.getAsMention() + " as a non-supporter, you may have a maximum of 3 pokemon notifications set up. What you tried to add would put you over this limit, please remove some pokemon with the !delpokemon command or try adding fewer pokemon.").queue();
                        return;
                    }
                    if (!DBManager.containsUser(author.getId())) {
                        DBManager.addUser(author.getId());
                    }
                    for (final Pokemon pokemon : pokemons) {
                        System.out.println("adding pokemon " + pokemon);
                        DBManager.addPokemon(author.getId(), pokemon);
                    }
                    String message2 = author.getAsMention() + " you will now be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());
                    message2 += userCommand.getIvMessage();

                    final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                    Location[] locations = { new Location(Region.All) };
                    if (locationsArg != null) {
                        locations = userCommand.getLocations();
                    }
                    message2 = message2 + " in " + Location.listToString(locations);
                    channel.sendMessage(message2).queue();
                }
                else if (cmdStr.equals("!delpokemon")) {
                    for (final Pokemon pokemon : pokemons) {
                        DBManager.deletePokemon(author.getId(), pokemon);
                    }
                    String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());
                    message2 += userCommand.getIvMessage();

                    final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                    Location[] locations = { new Location(Region.All) };
                    if (locationsArg != null) {
                        locations = userCommand.getLocations();
                    }
                    message2 = message2 + " in " + Location.listToString(locations);
                    channel.sendMessage(message2).queue();
                }
                else if (cmdStr.equals("!clearpokemon")) {
                    DBManager.clearPokemon(author.getId(), new ArrayList<Pokemon>(Arrays.asList(pokemons)));
                    final String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(pokemons) + " in any channels";
                    channel.sendMessage(message2).queue();
                }
            }
            else if (cmdStr.equals("!clearlocation")) {
                final Location[] locations2 = userCommand.getLocations();
                DBManager.clearLocations(author.getId(), locations2);
                final String message2 = author.getAsMention() + " you will no longer be notified of any pokemon in " + Location.listToString(locations2);
                channel.sendMessage(message2).queue();
            }
        }
    }

    static {

        reporterChannels = new ReporterChannels();
        MessageListener.testing = false;
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Innernorth, "inner-north"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Innersouth, "inner-south"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.GungahlinRegion, "gungahlin"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.BelconnenRegion, "belconnen"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.TuggeranongRegion, "tuggeranong"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Wodenweston, "woden-weston"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.QueanbeyanRegion, "queanbeyan"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Legacyrare, "legacy-rare"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Ultrarare, "ultra-rare"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Hundrediv, "100-iv"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.DratiniCandy, "dratini-candy"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.LarvitarCandy, "larvitar-candy"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.MareepCandy, "mareep-candy"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.SnorlaxCandy, "snorlax"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Event, "event"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Zeroiv, "0-iv"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.Hundrediv, "dex-filler"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.UnownAlphabet, "unown-alphabet"));
        MessageListener.reporterChannels.add(new ReporterChannel(Region.BigFishLittleRat, "big-fish-little-rat"));
    }
}
