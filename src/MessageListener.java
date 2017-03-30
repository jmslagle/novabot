import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by Paris on 17/03/2017.
 */
public class MessageListener extends ListenerAdapter {


    private static final String NEST_FB_GROUP = "https://www.facebook.com/groups/PogoCBRNests/";
    private static final String NEST_MAP = "http://www.google.com/maps/d/u/0/viewer?mid=1d-QuaDK1tJRiHKODXErTQIDqIAY";

    String helpStr = "My commands are: \n```" +
            "!addpokemon <pokemon list> <miniv,maxiv> <channel list>\n" +
            "!addpokemon pokemon\n" +
            "!delpokemon <pokemon list> <miniv,maxiv> <channel list>\n" +
            "!delpokemon pokemon\n" +
            "!clearpokemon <pokemon list>\n" +
            "!clearchannel <channel list>\n" +
            "!reset\n" +
            "!settings\n" +
            "!help\n" +
            "!channellist or !channels```";

    private static ReporterChannels reporterChannels = new ReporterChannels();

    static {
        reporterChannels.add(new ReporterChannel(Region.Innernorth, "inner-north"));
        reporterChannels.add(new ReporterChannel(Region.Innersouth, "inner-south"));
        reporterChannels.add(new ReporterChannel(Region.Gungahlin, "gungahlin"));
        reporterChannels.add(new ReporterChannel(Region.Belconnen, "belconnen"));
        reporterChannels.add(new ReporterChannel(Region.Tuggeranong, "tuggeranong"));
        reporterChannels.add(new ReporterChannel(Region.Wodenweston, "woden-weston"));
        reporterChannels.add(new ReporterChannel(Region.Queanbeyan, "queanbeyan"));
        reporterChannels.add(new ReporterChannel(Region.Legacyrare, "legacy-rare"));
        reporterChannels.add(new ReporterChannel(Region.Ultrarare, "ultra-rare"));
        reporterChannels.add(new ReporterChannel(Region.Hundrediv, "100-iv"));
        reporterChannels.add(new ReporterChannel(Region.Dratini, "dratini-candy"));
        reporterChannels.add(new ReporterChannel(Region.Larvitar, "larvitar-candy"));
        reporterChannels.add(new ReporterChannel(Region.Mareep, "mareep-candy"));
        reporterChannels.add(new ReporterChannel(Region.Snorlax, "snorlax"));
        reporterChannels.add(new ReporterChannel(Region.Event, "event"));
    }

    static Guild cbrSightings;


    private final String regionHelp =
            "Accepted channels are:\n\n" +
                    "all\n" +
                    "wodenweston = woden-weston = woden-weston-region = woden-weston-supporter\n" +
                    "gungahlin = gungahlin-region = gungahlin-supporter\n" +
                    "innernorth = inner-north = inner-north-region = inner-north-supporter\n" +
                    "belconnen = belconnen-region = belconnen-supporter\n" +
                    "innersouth = inner-south = inner-south-region = inner-south-supporter\n" +
                    "tuggeranong = tuggeranong-region = tuggeranong-supporter\n" +
                    "queanbeyan = queanbeyan-region = queanbeyan-supporter\n" +
                    "legacy = legacyrare = legacy-rare = legacy-rare-supporter\n" +
                    "larvitar = larvitarcandy = larvitar-candy = larvitar-candy-supporter\n" +
                    "dratini = dratinicandy = dratini-candy = dratini-candy-supporter\n" +
                    "mareep = mareepcandy = mareep-candy = mareep-candy-supporter\n" +
                    "ultrarare = ultra-rare = ultra-rare-supporter\n" +
                    "100iv = 100% = 100iv-supporter\n" +
                    "snorlax = snorlax-supporter\n" +
                    "event";


//    private final String badRegionError =
//            "Oops! I couldn't recognise one or more of the channels you specified. " + regionHelp;

    private final String inputFormat =
            "```!addpokemon <pokemon1, pokemon2, pokemon3> <channel1, channel2, channel3>```\n" +
                    "For as many pokemon and channels as you want. Make sure you include the <>. For more information on regions use the !channellis command";

//    private final String badPokenameError =
//            "Oops! I couldn't recognise one or more of the pokemon you specified. Check your spelling and try again";

    private final String badInputFormat =
            "Oops! I couldn't read your input! Please make sure you follow this format:\n" + inputFormat;

    private final String badRegionFormat =
            "Oops! I couldn't read your channel input! Please make sure you follow this format:\n" +
                    inputFormat;

    private final String nestHelp = "My nest commands are: \n```" +
            "!nest <pokemon list> <status list>\n" +
            "!nest pokemon status\n" +
            "!nest pokemon\n" +
            "!confirmed\n" +
            "!suspected\n" +
            "!fb or !nestfb\n" +
            "!map or !nestmap\n" +
            "!help\n```";

    public static String badPokenameError(ArrayList<String> badNames) {

        String toSend = "Oops! I couldn't recognise the following pokemon you specified: \n\n";

        for (String badName : badNames) {
            toSend += "  " + badName + "\n";
        }

        return toSend;
    }

    public static String badRegionError(ArrayList<String> badNames) {
        String toSend = "Oops! I couldn't recognise the following channels you specified: \n\n";

        for (String badName : badNames) {
            toSend += "  " + badName + "\n";
        }

        return toSend;
    }

    private String badStatusErrors(ArrayList<String> badStatusNames) {
        String toSend = "Oops! I couldn't recognise the following nest statuses you specified: \n\n";

        for (String badName : badStatusNames) {
            toSend += "  " + badName + "\n";
        }

        return toSend;
    }


    public static void main(String[] args) {

        System.out.println("Connecting to db");
//        DBManager.connect("root", "mimi");
        DBManager.connect("novabot", "Password123");
        System.out.println("Connected");

        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setAutoReconnect(true)
                    .setToken("MjkyODI5NTQzODM0NTgzMDQw.C7KPXw.-_xS4cfCczUAH7AkloDbFf3uGgc")           //The token of the account that is logging in.
//                    .setToken("MjkzMzI4Mjc0MDk0ODE3Mjgw.C7I4JQ.GFPR4D0KhFNiae53NtZj1_xpv0g")           //The token of the account that is logging in.
                    .addListener(new MessageListener())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.

            for (Guild guild : jda.getGuilds()) {
                cbrSightings = guild;
                System.out.println(guild.getName());
                if (guild.getTextChannelsByName("novabot", true).size() > 0) {
                    TextChannel channel = guild.getTextChannelsByName("novabot", true).get(0);
                    System.out.println(channel.getName());
                    channel.sendMessage("Hi! I'm awake again!").queue();
                }
            }
        } catch (LoginException e) {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        } catch (InterruptedException e) {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        } catch (RateLimitedException e) {
            //The login process is one which can be ratelimited. If you attempt to login in multiple times, in rapid succession
            // (multiple times a second), you would hit the ratelimit, and would see this exception.
            //As a note: It is highly unlikely that you will ever see the exception here due to how infrequent login is.
            e.printStackTrace();
        }

        System.out.println("connected");
    }

    /**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     * right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     * a method from a super class properly. You should do this every time you override a method!
     * <p>
     * As stated above, this method is overriding a hook method in the
     * {@link net.dv8tion.jda.core.hooks.ListenerAdapter ListenerAdapter} class. It has convience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     * <p>
     * In this example, when a message is received it is printed to the console.
     *
     * @param event An event containing information about a {@link net.dv8tion.jda.core.entities.Message Message} that was
     *              sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                  //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContent();              //This returns a human readable version of the Message. Similar to
        // what you would see in the client.

        boolean bot = author.isBot();                     //This boolean is useful to determine if the User that
        // sent the Message is a BOT or not!

        if (author.getId().equals(jda.getSelfUser().getId())) {

            System.out.printf("<%s>: %s\n", author.getName(), msg);
            return;
        }

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.

            cbrSightings = guild;

            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name = (member == null) ? "WEBHOOK" : member.getEffectiveName();    //This will either use the Member's nickname if they have one,
            // otherwise it will default to their username. (User#getName())

//            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);

            if (channel.getName().equals("novabot")) {
                parseMsg(msg.toLowerCase(), author, textChannel);
            } else if (channel.getName().equals("nests")) {
                parseNestMsg(msg.toLowerCase(), author, textChannel, ChannelType.TEXT);
            } else {
//                return;
                if (!message.isWebhookMessage()) return;
//                if (message.isWebhookMessage()) return;

                if (reporterChannels.containsChannel(channel.getName())) {

                    Region channelRegion = ReporterChannels.getRegionByName(channel.getName());
                    System.out.println("Converted channel name to region: " + channelRegion);

                    String msgTitle = message.getEmbeds().get(0).getTitle();

                    int suburbStart = msgTitle.indexOf("[") + 1;
                    int suburbEnd = msgTitle.indexOf("]");

                    String suburb = msgTitle.substring(suburbStart, suburbEnd);

                    int pokeStart = suburbEnd + 2;

                    int pokeEnd = msgTitle.substring(pokeStart).indexOf(" ") + pokeStart;

                    String pokeName = msgTitle.substring(pokeStart, pokeEnd).toLowerCase().trim();

//                    System.out.println(pokeName);

                    int ivStart = pokeEnd + 1;

                    int ivEnd = msgTitle.indexOf("%");

                    float pokeIV = Float.parseFloat(msgTitle.substring(ivStart, ivEnd));

//                    System.out.println(pokeIV);

                    String msgBody = message.getEmbeds().get(0).getDescription();

                    int moveSetStart = msgBody.indexOf("Moveset: ");
                    int moveSetEnd = channel.getName().endsWith("-supporter") ? msgBody.indexOf("Gender: ") : msgBody.length();
                    int moveSetSplit = msgBody.substring(moveSetStart, moveSetEnd).indexOf("-") + moveSetStart;

                    String move_1 = msgBody.substring(moveSetStart, moveSetSplit).trim().toLowerCase();
                    String move_2 = msgBody.substring(moveSetSplit + 2, moveSetEnd).trim().toLowerCase();

                    PokeSpawn pokeSpawn = new PokeSpawn(Pokemon.nameToID(pokeName), suburb, channelRegion, pokeIV, move_1, move_2);

                    System.out.println(pokeSpawn.toString());

                    ArrayList<String> ids = DBManager.getUserIDsToNotify(pokeSpawn);

                    if (channelRegion == Region.Event) {
                        System.out.println("Alerting all users");
                        alertSupporters(pokeSpawn, message, channel, ids);
                        alertPublic(pokeSpawn, message, channel, ids);
                    } else if (channel.getName().endsWith("-supporter")) {
                        System.out.println("alerting supporters");
                        alertSupporters(pokeSpawn, message, channel, ids);
                    } else {
                        System.out.println("alerting non-supporters");
                        alertPublic(pokeSpawn, message, channel, ids);
                    }
                }
            }

            //Now that you have a grasp on the things that you might see in an event, specifically MessageReceivedEvent,
            // we will look at sending / responding to messages!
            //This will be an extremely simplified example of command processing.

        } else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel

        {
            //The message was sent in a PrivateChannel.
            //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
            PrivateChannel privateChannel = event.getPrivateChannel();

//            if(author)

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);

            if (msg.equals("!nesthelp")) {
                channel.sendMessage(nestHelp).queue();
                return;
            }

            if (msg.startsWith("!nest") || msg.startsWith("!map") || msg.startsWith("fb")) {
                parseNestMsg(msg, author, channel, ChannelType.PRIVATE);
            } else {
                parseMsg(msg.toLowerCase(), author, privateChannel);
            }

        } else if (event.isFromType(ChannelType.GROUP))   //If this message was sent to a Group. This is CLIENT only!

        {
            //The message was sent in a Group. It should be noted that Groups are CLIENT only.
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.
            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
        }
    }

    private void parseNestMsg(String msg, User author, MessageChannel channel, ChannelType channelType) {
        switch (msg) {
            case "!fb":
            case "!nestfb":
                channel.sendMessage(NEST_FB_GROUP).queue();
                break;
            case "!map":
            case "!nestmap":
                channel.sendMessage(NEST_MAP).queue();
                break;
            case "!help":
                channel.sendMessage(nestHelp).queue();
                break;
            default:
                if (msg.startsWith("!nest")) {

                    if (countChars(msg, '<') + countChars(msg, '>') < 2) {
                        int pokeStart = msg.indexOf(" ") + 1;

                        MessageBuilder builder = new MessageBuilder();
                        builder.append(author.getAsMention() + "");

                        ArrayList<Nest> nests;

                        Pokemon pokemon;

                        if (countChars(msg.trim(), ' ') == 1) {
                            pokemon = new Pokemon(msg.substring(pokeStart).trim());

                            if (pokemon.name == null) {
                                channel.sendMessage(author.getAsMention() + " sorry, I don't recognise that pokemon").queue();
                                return;
                            }
                            nests = NestSheetManager.getNestsByPokemon(pokemon);
                        } else {
                            int pokeEnd = msg.substring(pokeStart).indexOf(" ") + pokeStart;

                            pokemon = new Pokemon(msg.substring(pokeStart, pokeEnd).trim());

                            if (pokemon.name == null) {
                                channel.sendMessage(author.getAsMention() + " sorry, I don't recognise that pokemon").queue();
                                return;
                            }


                            NestStatus status = NestStatus.fromString(msg.substring(pokeEnd).trim());

                            if (status == null) {
                                channel.sendMessage(author.getAsMention() + " sorry, I don't recognise that nest status. Try either 'confirmed' or 'suspected'").queue();
                                return;
                            }

                            nests = NestSheetManager.getNests(new Pokemon[]{pokemon}, new NestStatus[]{status});
                        }

                        if (nests.size() == 0) {
                            builder.append(" sorry, I couldn't find any currently identified nests for " + pokemon.name);
                            channel.sendMessage(builder.build()).queue();
                        } else {
                            if (channelType != ChannelType.PRIVATE) {
                                channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                            }

                            builder.append(" I found " + nests.size() + " results for " + pokemon.name + ":\n\n");

                            for (Nest nest : nests) {
                                builder.append(nest + "\n");
                                builder.append("<" + nest.getGMapsLink() + ">\n\n");
                            }

                            if (!author.hasPrivateChannel()) {
                                author.openPrivateChannel().complete();
                            }

                            for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                                author.getPrivateChannel().sendMessage(message).queue();
                            }
                        }
                    } else {
                        int pokeStart = msg.indexOf('<') + 1;

                        int pokeEnd = msg.substring(pokeStart).indexOf('>') + pokeStart;

                        String pokeList = msg.substring(pokeStart, pokeEnd);

                        String[] pokemonStrings = pokeList.split(",");

                        Pokemon[] pokemon = new Pokemon[pokemonStrings.length];

                        ArrayList<String> badPokeNames = new ArrayList<>();

                        boolean nullPokemon = false;

                        for (int i = 0; i < pokemonStrings.length; i++) {
                            String mon = pokemonStrings[i].trim();
                            if (!Pokemon.VALID_NAMES.contains(mon)) {
                                nullPokemon = true;
                                badPokeNames.add(mon);
                            }
                            pokemon[i] = new Pokemon(mon);
                            pokemonStrings[i] = mon;
                        }

                        if (!nullPokemon) {

                            NestStatus[] statuses;


                            ArrayList<String> badStatusNames = new ArrayList<>();

                            boolean nullStatus = false;
                            if (countChars(msg, '<') + countChars(msg, '>') == 2) {
                                statuses = new NestStatus[]{NestStatus.Confirmed, NestStatus.Suspected};
                            } else {

                                int statusStart = msg.substring(pokeEnd).indexOf('<') + 1 + pokeEnd;
                                int regionEnd = msg.substring(statusStart).indexOf('>') + statusStart;

                                String statusList = msg.substring(statusStart, regionEnd);

                                String[] statusStrings = statusList.split(",");

                                statuses = new NestStatus[statusStrings.length];

                                nullStatus = false;


                                for (int i = 0; i < statusStrings.length; i++) {
                                    statuses[i] = NestStatus.fromString(statusStrings[i].trim());
                                    if (statuses[i] == null) {
                                        nullStatus = true;
                                        badStatusNames.add(statusStrings[i].trim());
                                    }
                                }
                            }

                            if (!nullStatus) {

                                ArrayList<Nest> nests = NestSheetManager.getNests(pokemon, statuses);

                                MessageBuilder builder = new MessageBuilder();
                                builder.append(author.getAsMention());

                                if (nests.size() == 0) {
                                    builder.append(" sorry, I couldn't find any " + NestStatus.listToString(statuses) +
                                            " nests for " + Pokemon.listToString(pokemon));
                                    channel.sendMessage(builder.build()).queue();
                                } else {
                                    if (channelType != ChannelType.PRIVATE) {
                                        channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                                    }

                                    builder.append(" I found " + nests.size() + " results for " + NestStatus.listToString(statuses)
                                            + " " + Pokemon.listToString(pokemon) +
                                            " nests :\n\n");

                                    for (Pokemon poke : pokemon) {
                                        builder.append("**" + poke.name + "**\n");
                                        boolean foundPoke = false;
                                        for (Nest nest : nests) {
                                            if (nest.pokemon.name.equals(poke.name)) {
                                                foundPoke = true;
                                                builder.append("  " + nest + "\n");
                                                builder.append("  <" + nest.getGMapsLink() + ">\n\n");
                                            }
                                        }

                                        if (!foundPoke) builder.append("  No results found\n");
                                    }

                                    if (!author.hasPrivateChannel()) {
                                        author.openPrivateChannel().complete();
                                    }

                                    for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                                        author.getPrivateChannel().sendMessage(message).queue();
                                    }
                                }

                            } else {
                                MessageBuilder builder = new MessageBuilder();
                                builder.append(author.getAsMention() + ", " + badStatusErrors(badStatusNames));

                                for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                                    channel.sendMessage(message).queue();
                                }

                            }
                        } else {
                            MessageBuilder builder = new MessageBuilder();
                            builder.append(author.getAsMention() + "," + badPokenameError(badPokeNames));

                            for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                                channel.sendMessage(message).queue();
                            }
                        }
                    }

                } else if (msg.startsWith("!confirmed")) {
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(author.getAsMention() + "");

                    ArrayList<Nest> nests = NestSheetManager.getNestsByStatus(new NestStatus[]{NestStatus.Confirmed});

                    if (nests.size() == 0) {
                        builder.append(" sorry, I couldn't find any confirmed nests");

                        channel.sendMessage(builder.build()).queue();
                    } else {
                        if (channelType != ChannelType.PRIVATE) {
                            channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                        }

                        builder.append(" I found " + nests.size() + " confirmed nests:\n\n");

                        for (Nest nest : nests) {
                            builder.append(nest + "\n");
                            builder.append("<" + nest.getGMapsLink() + ">\n\n");
                        }

                        if (!author.hasPrivateChannel()) {
                            author.openPrivateChannel().complete();
                        }

                        for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            author.getPrivateChannel().sendMessage(message).queue();
                        }
                    }

                } else if (msg.equals("!suspected")) {
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(author.getAsMention() + "");

                    ArrayList<Nest> nests = NestSheetManager.getNestsByStatus(new NestStatus[]{NestStatus.Suspected});

                    if (nests.size() == 0) {
                        builder.append(" sorry, I couldn't find any suspected nests");

                        channel.sendMessage(builder.build()).queue();
                    } else {
                        if (channelType != ChannelType.PRIVATE) {
                            channel.sendMessage(author.getAsMention() + " I have PM'd you your search results").queue();
                        }

                        builder.append(" I found " + nests.size() + " suspected nests:\n\n");

                        for (Nest nest : nests) {
                            builder.append(nest + "\n");
                            builder.append("<" + nest.getGMapsLink() + ">\n\n");
                        }

                        if (!author.hasPrivateChannel()) {
                            author.openPrivateChannel().complete();
                        }

                        for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            author.getPrivateChannel().sendMessage(message).queue();
                        }
                    }
                }
                break;
        }
    }

    private void alertPublic(PokeSpawn pokeSpawn, Message message, MessageChannel channel, ArrayList<String> userIDs) {
        for (String userID : userIDs) {

            if (isSupporter(userID)) continue;

            if (DBManager.countPokemon(userID) > 3) {
                User user = cbrSightings.getMemberById(userID).getUser();

                if (!user.hasPrivateChannel()) {
                    user.openPrivateChannel().complete();
                }

                user.getPrivateChannel().sendMessage(
                        "Hi " + user.getAsMention() + ", I noticed recently you have lost your supporter status. " +
                                "As a result I have cleared your settings, however as a non-supporter you can add up to 3 pokemon to your settings").queue();

                DBManager.resetUser(userID);

                cbrSightings.getTextChannelsByName("trello", true).get(0).sendMessage(
                        user.getAsMention() + " has lost supporter status with more than 3 pokemon in their settings. " +
                                "Their settings have been reset and they have been PMed"
                ).queue();
                continue;
            }

            MessageBuilder builder = new MessageBuilder();
//            builder.append(cbrSightings.getName() + "> A " + Pokemon.idToName(pokeSpawn.id) + " was mentioned in #"
//                    + channel.getName() + ":\n");
            builder.setEmbed(message.getEmbeds().get(0));

            System.out.println("Notifying user: " + userID);
            User user = cbrSightings.getMemberById(userID).getUser();
            if (!user.hasPrivateChannel())
                user.openPrivateChannel().complete();
            user.getPrivateChannel().sendMessage(builder.build()).queue();
        }
    }

    private void alertSupporters(PokeSpawn pokeSpawn, Message message, MessageChannel channel, ArrayList<String> userIDs) {
        for (String userID : userIDs) {
            if (!isSupporter(userID)) continue;

            MessageBuilder builder = new MessageBuilder();
//            builder.append(cbrSightings.getName() + "> A " + Pokemon.idToName(pokeSpawn.id) + " was mentioned in #"
//                    + channel.getName() + ":\n");
            builder.setEmbed(message.getEmbeds().get(0));

            System.out.println("Notifying user: " + userID);
            User user = cbrSightings.getMemberById(userID).getUser();
            if (!user.hasPrivateChannel())
                user.openPrivateChannel().complete();
            user.getPrivateChannel().sendMessage(builder.build()).queue();
        }
    }

    private boolean isSupporter(String userID) {

        Member member = cbrSightings.getMemberById(userID);

        for (Role role : member.getRoles()) {
            if (role.getName().toLowerCase().contains("supporter")) return true;
        }

        return false;
    }

    private boolean containsPokemon(Pokemon msgPokemon, Set<Pokemon> pokemonSet) {
        boolean contains = false;

        if (pokemonSet == null) return false;

        for (Pokemon pokemon : pokemonSet) {
            if (pokemon == null) continue;

            if (pokemon.equals(msgPokemon)) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    private void parseMsg(String msg, User author, MessageChannel channel) {
//        if (!isSupporter(author.getId())){
//            channel.sendMessage("Sorry " + author.getAsMention() + ", I can only accept commands from supporters").queue();
//            return;
//        }

        if(msg.startsWith("!nest")){
            channel.sendMessage(author.getAsMention() + " I only accept nest commands in the "
                    + cbrSightings.getTextChannelsByName("nests",true).get(0).getAsMention() + " channel or via PM").queue();
            return;
        }

        if (msg.startsWith("!addpokemon")) {
            if (countChars(msg, '<') + countChars(msg, '>') == 0) {
                int pokeStart = msg.indexOf(" ") + 1;

                Pokemon pokemon = new Pokemon(msg.substring(pokeStart).trim());

                if (pokemon.name == null) {
                    channel.sendMessage(author.getAsMention() + " sorry, I don't recognise that pokemon").queue();
                    return;
                }

                channel.sendMessage(author.getAsMention() + " you will now be notified of " + pokemon.name + " in all channels").queue();

                DBManager.addPokemon(author.getId(), pokemon);

            } else {

                if((countChars(msg,'<') + countChars(msg,'>')) % 2 != 0){
                    channel.sendMessage(author.getAsMention() + " " + badInputFormat).queue();
                    return;
                }

                if (!DBManager.containsUser(author.getId())) {
                    DBManager.addUser(author.getId());
                }

                int pokeStart = msg.indexOf('<') + 1;

                int pokeEnd = msg.substring(pokeStart).indexOf('>') + pokeStart;

                String pokeList = msg.substring(pokeStart, pokeEnd);

                String[] pokemonStrings = pokeList.split(",");

                ArrayList<String> badPokeNames = new ArrayList<>();

                String toSend = author.getAsMention() + ", you will now be notified of ";

                boolean nullPokemon = false;

                for (int i = 0; i < pokemonStrings.length; i++) {
                    String mon = pokemonStrings[i].trim();
                    if (!Pokemon.VALID_NAMES.contains(mon)) {
                        nullPokemon = true;
                        badPokeNames.add(mon);
                    }
                    pokemonStrings[i] = mon;
                    toSend += (i < pokemonStrings.length - 1) ? mon + ", " : mon;
                }

                if (!nullPokemon) {

                    if (countChars(msg, '<') + countChars(msg, '>') == 2) {
                        for (String name : pokemonStrings) {
                            Pokemon pokemon = new Pokemon(name);
                            DBManager.addPokemon(author.getId(), pokemon);
                        }

                        toSend += " in all regions";
                        channel.sendMessage(toSend).queue();
                        return;
                    }

                    float miniv = 0;
                    float maxiv = 100;


                    int ivsEnd = pokeEnd;

                    if (countChars(msg, '<') + countChars(msg, '>') == 6) {
                        int ivsStart = msg.substring(pokeEnd).indexOf('<') + 1 + pokeEnd;
                        ivsEnd = msg.substring(ivsStart).indexOf('>') + ivsStart;

                        int ivSplit = msg.substring(ivsStart, ivsEnd).indexOf(',') + ivsStart;

                        if ((ivSplit - ivsStart) < 0) { //no , in iv block, assume min IV only
                            miniv = Float.parseFloat(msg.substring(ivsStart, ivsEnd));
                            maxiv = 100;

                            toSend += " " + miniv + "% or above" ;
                        } else {
                            miniv = Float.parseFloat(msg.substring(ivsStart, ivSplit));
                            maxiv = Float.parseFloat(msg.substring(ivSplit + 1, ivsEnd));

                            toSend += " between " + miniv + " and " + maxiv + "%";

                        }
                    }

                    int regionStart = msg.substring(ivsEnd).indexOf('<') + 1 + ivsEnd;
                    int regionEnd = msg.substring(regionStart).indexOf('>') + regionStart;

                    String regionList = msg.substring(regionStart, regionEnd);

                    String[] regionStrings = regionList.split(",");

                    toSend += regionStrings.length == 1 ? " in channel: " : " in channels: ";

                    Region[] regions = new Region[regionStrings.length];

                    boolean nullRegion = false;


                    ArrayList<String> badRegionNames = new ArrayList<>();

                    for (int i = 0; i < regionStrings.length; i++) {
                        regions[i] = Region.fromString(regionStrings[i].trim());
                        if (regions[i] == null) {
                            nullRegion = true;
                            badRegionNames.add(regionStrings[i].trim());
                        }
                    }

                    if (!nullRegion) {

                        if (!isSupporter(author.getId()) && (pokemonStrings.length * regions.length + DBManager.countPokemon(author.getId()) > 3)) {
                            channel.sendMessage(author.getAsMention() + " as a non-supporter, you may have a maximum of 3 pokemon " +
                                    "notifications set up. What you tried to add would put you over this limit, please remove some pokemon" +
                                    " with the !delpokemon command or try adding fewer pokemon.").queue();
                            return;
                        }


                        for (int i = 0; i < regions.length; i++) {
                            String region = regionStrings[i];
                            toSend += (i < regions.length - 1) ? region + ", " : region;
                        }
                        channel.sendMessage(toSend).queue();

                        for (String name : pokemonStrings) {
                            for (Region region : regions) {
                                Pokemon pokemon = new Pokemon(name, region, miniv, maxiv);

                                DBManager.addPokemon(author.getId(), pokemon);
                            }
                        }
                    } else {
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(author.getAsMention() + "," + badRegionError(badRegionNames));

                        for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            channel.sendMessage(message).queue();
                        }

                    }
                } else {
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(author.getAsMention() + "," + badPokenameError(badPokeNames));

                    for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                        channel.sendMessage(message).queue();
                    }
                }
            }

        } else if (msg.startsWith("!delpokemon"))

        {
            if (countChars(msg, '<') + countChars(msg, '>') == 0) {
                int pokeStart = msg.indexOf(" ") + 1;

                Pokemon pokemon = new Pokemon(msg.substring(pokeStart).trim());

                if (pokemon.name == null) {
                    channel.sendMessage(author.getAsMention() + " sorry, I don't recognise that pokemon").queue();
                    return;
                }

                channel.sendMessage(author.getAsMention() + " you will no longer be notified of " + pokemon.name + " in any channel").queue();

                ArrayList<Pokemon> pokemons = new ArrayList<>();
                pokemons.add(pokemon);

                DBManager.clearPokemon(author.getId(), pokemons);

            } else {


                if((countChars(msg,'<') + countChars(msg,'>')) % 2 != 0){
                    channel.sendMessage(author.getAsMention() + " " + badInputFormat).queue();
                    return;
                }


                int pokeStart = msg.indexOf('<') + 1;

                int pokeEnd = msg.substring(pokeStart).indexOf('>') + pokeStart;

                String pokeList = msg.substring(pokeStart, pokeEnd);

                String[] pokemonStrings = pokeList.split(",");

                ArrayList<String> badPokeNames = new ArrayList<>();

                String toSend = author.getAsMention() + ", you will no longer be notified of ";

                boolean nullPokemon = false;

                for (int i = 0; i < pokemonStrings.length; i++) {
                    String mon = pokemonStrings[i].trim();
                    if (!Pokemon.VALID_NAMES.contains(mon)) {
                        nullPokemon = true;
                        badPokeNames.add(mon);
                    }
                    pokemonStrings[i] = mon;
                    toSend += (i < pokemonStrings.length - 1) ? mon + ", " : mon;
                }

                if (!nullPokemon) {

                    if (countChars(msg, '<') + countChars(msg, '>') == 2) {
                        for (String name : pokemonStrings) {
                            Pokemon pokemon = new Pokemon(name);
                            DBManager.addPokemon(author.getId(), pokemon);
                        }

                        toSend += " in all regions";
                        channel.sendMessage(toSend).queue();
                        return;
                    }

                    float miniv = 0;
                    float maxiv = 100;


                    int ivsEnd = pokeEnd;

                    if (countChars(msg, '<') + countChars(msg, '>') == 6) {
                        int ivsStart = msg.substring(pokeEnd).indexOf('<') + 1 + pokeEnd;
                        ivsEnd = msg.substring(ivsStart).indexOf('>') + ivsStart;

                        int ivSplit = msg.substring(ivsStart, ivsEnd).indexOf(',') + ivsStart;

                        if ((ivSplit - ivsStart) < 0) { //no , in iv block, assume min IV only
                            miniv = Float.parseFloat(msg.substring(ivsStart, ivsEnd));
                            maxiv = 100;

                            toSend += miniv + "% or above " ;
                        } else {
                            miniv = Float.parseFloat(msg.substring(ivsStart, ivSplit));
                            maxiv = Float.parseFloat(msg.substring(ivSplit + 1, ivsEnd));

                            toSend += " between " + miniv + " and " + maxiv + "%";

                        }
                    }

                    int regionStart = msg.substring(ivsEnd).indexOf('<') + 1 + ivsEnd;
                    int regionEnd = msg.substring(regionStart).indexOf('>') + regionStart;

                    String regionList = msg.substring(regionStart, regionEnd);

                    String[] regionStrings = regionList.split(",");

                    toSend += regionStrings.length == 1 ? " in channel: " : " in channels: ";

                    Region[] regions = new Region[regionStrings.length];

                    boolean nullRegion = false;

                    ArrayList<String> badRegionNames = new ArrayList<>();

                    for (int i = 0; i < regionStrings.length; i++) {
                        regions[i] = Region.fromString(regionStrings[i].trim());
                        if (regions[i] == null) {
                            nullRegion = true;
                            badRegionNames.add(regionStrings[i].trim());
                        }
                    }

                    if (!nullRegion) {
                        for (int i = 0; i < regions.length; i++) {
                            String region = regionStrings[i];
                            toSend += (i < regions.length - 1) ? region + ", " : region;
                        }
                        channel.sendMessage(toSend).queue();

                        for (String name : pokemonStrings) {
                            for (Region region : regions) {
                                Pokemon pokemon = new Pokemon(name, region, miniv, maxiv);

                                DBManager.deletePokemon(author.getId(), pokemon);
                            }
                        }
                    } else {
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(author.getAsMention() + "," + badRegionError(badRegionNames));

                        for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                            channel.sendMessage(message).queue();
                        }

                    }
                } else {
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(author.getAsMention() + "," + badPokenameError(badPokeNames));

                    for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                        channel.sendMessage(message).queue();
                    }
                }
            }
        } else if (msg.startsWith("!clearpokemon"))

        {
            int pokeStart = msg.indexOf('<') + 1;

            int pokeEnd = msg.substring(pokeStart).indexOf('>') + pokeStart;

            String pokeList = msg.substring(pokeStart, pokeEnd);

            String[] pokemonStrings = pokeList.split(",");

            ArrayList<String> badNames = new ArrayList<>();

            String toSend = author.getAsMention() + ", you will no longer be notified of ";

            boolean nullPokemon = false;

            for (int i = 0; i < pokemonStrings.length; i++) {
                String mon = pokemonStrings[i].trim();
                if (!Pokemon.VALID_NAMES.contains(mon)) {
                    nullPokemon = true;
                    badNames.add(mon);
                }
                pokemonStrings[i] = mon;
                toSend += (i < pokemonStrings.length - 1) ? mon + ", " : mon;
            }

            toSend += " in any channels";
            if (!nullPokemon) {

                ArrayList<Pokemon> pokemons = new ArrayList<>();

                for (String name : pokemonStrings) {
                    pokemons.add(new Pokemon(name));
                }

                channel.sendMessage(toSend).queue();

                DBManager.clearPokemon(author.getId(), pokemons);

            } else {
                MessageBuilder builder = new MessageBuilder();
                builder.append(author.getAsMention() + "," + badPokenameError(badNames));

                for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                    channel.sendMessage(message).queue();
                }
            }

        } else if (msg.startsWith("!clearchannel"))

        {

            int regionStart = msg.indexOf('<') + 1;
            int regionEnd = msg.substring(regionStart).indexOf('>') + regionStart;

            String regionList = msg.substring(regionStart, regionEnd);

            String[] regionStrings = regionList.split(",");

            String toSend = author.getAsMention() + ", you will no longer be notified of anything";

            toSend += regionStrings.length == 1 ? " in channel: " : " in channels: ";

            Region[] regions = new Region[regionStrings.length];

            boolean nullRegion = false;

            ArrayList<String> badRegionNames = new ArrayList<>();

            for (int i = 0; i < regionStrings.length; i++) {
                regions[i] = Region.fromString(regionStrings[i].trim());
                if (regions[i] == null) {
                    nullRegion = true;
                    badRegionNames.add(regionStrings[i].trim());
                }
            }

            if (!nullRegion) {
                for (int i = 0; i < regions.length; i++) {
                    String region = regionStrings[i];
                    toSend += (i < regions.length - 1) ? region + ", " : region;
                }
                channel.sendMessage(toSend).queue();

                DBManager.clearRegions(author.getId(), regions);

            } else {

                MessageBuilder builder = new MessageBuilder();
                builder.append(author.getAsMention() + "," + badRegionError(badRegionNames));

                for (Message message : builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)) {
                    channel.sendMessage(message).queue();
                }

            }

        } else if (msg.equals("!settings"))

        {
            UserPref userPref = DBManager.getUserPref(author.getId());
            System.out.println("!settings");

            if (userPref == null || userPref.isEmpty()) {
                channel.sendMessage(author.getAsMention() + ", you don't have any notifications set. Add some with the !addpokemon command.").queue();
            } else {
                String toSend = author.getAsMention() + ", you are currently set to receive notifications for:\n\n";

                toSend += userPref.allPokemonToString();

                MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);

                Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);

                for (Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
        } else if (msg.equals("!reset"))

        {
            DBManager.resetUser(author.getId());
            channel.sendMessage(author.getAsMention() + ", your notification settings have been reset").queue();

        } else if (msg.equals("!help"))

        {
            channel.sendMessage(helpStr).queue();
        } else if (msg.equals("!channellist") || msg.equals("!channels"))

        {
            channel.sendMessage(regionHelp).queue();
        } else

        {
            channel.sendMessage(author.getAsMention() + ", I don't recognize that message. Use !help to see my commands");
        }

    }


    private int countChars(String msg, char c) {
        int count = 0;

        for (char c1 : msg.toCharArray()) {
            if (c1 == c) count++;
        }

        return count;
    }


}
