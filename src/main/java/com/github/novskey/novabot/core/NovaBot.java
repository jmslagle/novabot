package com.github.novskey.novabot.core;

import com.github.novskey.novabot.data.DBManager;
import com.github.novskey.novabot.maps.Geofencing;
import com.github.novskey.novabot.maps.ReverseGeocoder;
import com.github.novskey.novabot.notifier.NotificationsManager;
import com.github.novskey.novabot.notifier.RaidNotificationSender;
import com.github.novskey.novabot.parser.*;
import com.github.novskey.novabot.pokemon.Pokemon;
import com.github.novskey.novabot.raids.LobbyManager;
import com.github.novskey.novabot.raids.Raid;
import com.github.novskey.novabot.raids.RaidLobby;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.novskey.novabot.core.Spawn.printFormat24hr;

public class NovaBot {

    public final String WHITE_GREEN_CHECK = "\u2705";
    public final Logger novabotLog = LoggerFactory.getLogger("novabot");
    public final ConcurrentHashMap<String, ZonedDateTime> lastUserRoleChecks = new ConcurrentHashMap<>();
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public TextChannel roleLog;
    public Guild guild;
    public boolean testing = false;
    public Config config;
    public SuburbManager suburbs;
    public ArrayList<Invite> invites = new ArrayList<>();
    public JDA jda;
    public LobbyManager lobbyManager;
    public MessageChannel userUpdatesLog;
    public Geofencing geofencing;
    public DBManager dbManager;
    public ReverseGeocoder reverseGeocoder;
    public Commands commands;
    private NotificationsManager notificationsManager;
    public Parser parser;
    private ResourceBundle messagesBundle;
    private ResourceBundle timeUnitsBundle;


    public void alertRaidChats(String[] raidChatIds, String message) {
        for (String raidChatId : raidChatIds) {
            guild.getTextChannelById(raidChatId).sendMessageFormat(message).queue(
                    m -> m.addReaction(WHITE_GREEN_CHECK).queue()
                                                                                 );
        }
    }

    public void loadConfig() {
        try {
            config = new Config(
                    new Ini(new File(testing ? "config.example.ini" : "config.ini")),
                    this
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGeofences() {
        geofencing = new Geofencing(this);
        geofencing.loadGeofences();
    }

    public void loadSuburbs() {
        suburbs = new SuburbManager(new File("suburbs.txt"), this);
    }

    public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        NovaBot novaBot = new NovaBot();
        novaBot.setup();
        novaBot.start();
    }

    public void parseModMsg(Message msg, TextChannel channel) {
        if (msg.getContentDisplay().startsWith("!joindate")) {

            StringBuilder response = new StringBuilder(String.format("%s, the results of your search are:\n\n", msg.getAuthor().getAsMention()));

            List<User> mentionedUsers = msg.getMentionedUsers();

            for (User mentionedUser : mentionedUsers) {
                OffsetDateTime joinDate      = guild.getMember(mentionedUser).getJoinDate();
                String         formattedDate = joinDate.toInstant().atZone(config.getTimeZone()).format(formatter);

                response.append(String.format("  %s joined at %s", mentionedUser.getAsMention(), formattedDate));
            }

            channel.sendMessage(response.toString()).queue();
        }
    }

    public void parseMsg(final String msg, final User author, final MessageChannel channel) {

        if (!msg.startsWith(getLocalString("Prefix"))) {
            return;
        }

        if (msg.equals(getLocalString("ReloadCommand"))) {
            if (isAdmin(author)) {
                loadConfig();
                loadSuburbs();
                loadGeofences();
            }
            channel.sendMessageFormat("%s, %s", author,getLocalString("ReloadMessage")).queue();
            return;
        }

        if (msg.equals(getLocalString("PauseCommand"))) {
            dbManager.pauseUser(author.getId());
            channel.sendMessageFormat("%s, %s", author,getLocalString("PauseMessage")).queue();
            return;
        }

        if (msg.equals(getLocalString("UnPauseCommand"))) {
            dbManager.unPauseUser(author.getId());
            channel.sendMessageFormat("%s, %s", author,getLocalString("UnPauseMessage")).queue();
            return;
        }

        if (msg.startsWith(getLocalString("JoinRaidCommand")) && config.isRaidOrganisationEnabled()) {
            String groupCode = msg.substring(msg.indexOf(" ") + 1).trim();

            RaidLobby lobby = lobbyManager.getLobby(groupCode);

            if (lobby == null) {
                channel.sendMessageFormat("%s %s", author, getLocalString("NoRaidLobbyMessage").replace("<lobbycode>",groupCode)).queue();
                return;
            } else {
                if (lobby.containsUser(author.getId())) {
                    channel.sendMessageFormat("%s %s", author,getLocalString("AlreadyInLobbyMessage")).queue();
                    return;
                }

                lobby.joinLobby(author.getId());

                String alertMsg = getLocalString("AlertRaidChatsMessage");
                alertMsg = alertMsg.replaceAll("<user>",author.getAsMention());
                alertMsg = alertMsg.replaceAll("<boss-or-egg>", (lobby.spawn.bossId == 0 ? String.format("lvl %s egg", lobby.spawn.raidLevel) : lobby.spawn.properties.get("pkmn")));
                alertMsg = alertMsg.replaceAll("<channel>",lobby.getChannel().getAsMention());
                alertMsg = alertMsg.replaceAll("<membercount>", String.valueOf(lobby.memberCount()));
                alertMsg = alertMsg.replaceAll("<lobbycode>", groupCode);

                alertRaidChats(config.getRaidChats(lobby.spawn.getGeofences()),alertMsg);

                String joinMsg = getLocalString("JoinRaidLobbyMessage");
                joinMsg = joinMsg.replaceAll("<channel>",lobby.getChannel().getAsMention());
                joinMsg = joinMsg.replaceAll("<lobbysize>", String.valueOf(lobby.memberCount()));
                channel.sendMessageFormat("%s %s", joinMsg).queue();
            }

            return;
        }

        if (msg.equals(getLocalString("SettingsCommand"))) {
            final UserPref userPref = dbManager.getUserPref(author.getId());
            novabotLog.debug("!settings");
            if (userPref == null || (userPref.isRaidEmpty() && userPref.isPokeEmpty() && userPref.isPresetEmpty())) {
                channel.sendMessage(author.getAsMention() + ", " + getLocalString("NoSettingsMessage")).queue();
            } else {
                String toSend = author.getAsMention() + ", " + getLocalString("SettingsMessageStart");
                toSend += userPref.allSettingsToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        } else if (msg.equals(getLocalString("PokemonSettingsCommand")) && config.pokemonEnabled()) {
            final UserPref userPref = dbManager.getUserPref(author.getId());
            novabotLog.debug("!pokemonsettings");
            if (userPref == null || userPref.isPokeEmpty()) {
                channel.sendMessage(author.getAsMention() + ", " + getLocalString("NoPokemonSettingsMessage")).queue();
            } else {
                String toSend = author.getAsMention() + ", " + getLocalString("PokemonSettingsMessageStart");
                toSend += userPref.allPokemonToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        } else if (msg.equals(getLocalString("RaidSettingsCommand")) && config.raidsEnabled()) {
            final UserPref userPref = dbManager.getUserPref(author.getId());
            novabotLog.debug("!raidsettings");
            if (userPref == null || userPref.isRaidEmpty()) {
                channel.sendMessage(author.getAsMention() + ", " + getLocalString("NoRaidSettingsMessage")).queue();
            } else {
                String toSend = author.getAsMention() + ", " + getLocalString("RaidSettingsMessageStart");
                toSend += userPref.allRaidsToString();
                final MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                final Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        } else if (config.presets.size() > 0 && (msg.equals(getLocalString("PresetSettingsCommand")))) {
            UserPref userPref = dbManager.getUserPref(author.getId());
            novabotLog.debug("!presetsettings");
            if (userPref == null || userPref.isPresetEmpty()) {
                channel.sendMessageFormat("%s, " + getLocalString("NoPresetSettingsMessage"), author).queue();
            } else {
                String         toSend  = String.format("%s, %s%s", author.getAsMention(), getLocalString("PresetSettingsMessage"), userPref.allPresetsToString());
                MessageBuilder builder = new MessageBuilder();
                builder.append(toSend);
                Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
                for (final Message message : messages) {
                    channel.sendMessage(message).queue();
                }
            }
            return;
        } else if (config.presets.size() > 0 && (msg.equals(getLocalString("PresetsCommand")) || msg.equals(getLocalString("PresetListCommand")))) {
            MessageBuilder builder = new MessageBuilder();
            builder.appendFormat("%s, %s%s", author, getLocalString("PresetListMessageStart"),config.getPresetsList());

            Queue<Message> messages = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
            for (final Message message : messages) {
                channel.sendMessage(message).queue();
            }
            return;
        } else if (msg.equals(getLocalString("ResetCommand"))) {
            dbManager.resetUser(author.getId());
            channel.sendMessageFormat("%s, %s",author,getLocalString("ResetMessage")).queue();
            return;
        } else if (msg.equals(getLocalString("ResetPokemonCommand")) && config.pokemonEnabled()) {
            dbManager.resetPokemon(author.getId());
            channel.sendMessageFormat("%s, %s",author,getLocalString("ResetPokemonMessage")).queue();
            return;
        } else if (msg.equals(getLocalString("ResetRaidsCommand")) && config.raidsEnabled()) {
            dbManager.resetRaids(author.getId());
            channel.sendMessageFormat("%s, %s", author,getLocalString("ResetRaidsMessage")).queue();
            return;
        } else if (msg.equals(getLocalString("ResetPresetsCommand")) && config.presetsEnabled()) {
            dbManager.resetPresets(author.getId());
            channel.sendMessageFormat("%s, %s", author,getLocalString("ResetPresetsMessage")).queue();
            return;
//        }
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
        } else if (msg.equals(getLocalString("HelpCommand"))) {
            channel.sendMessageFormat(getLocalString("HelpMessageStart") +
                                (config.pokemonEnabled() ? getLocalString("HelpMessagePokemonCommands") : "") +
                                (config.raidsEnabled() ? getLocalString("HelpMessageRaidCommands") : "") +
                                (config.presets.size() > 0 ? getLocalString("HelpMessagePresetCommands") : "") +
                                getLocalString("HelpMessageOtherCommandsStart") +
                                (config.statsEnabled() ? getLocalString("HelpMessageStatsCommand") : "") +
                                (config.isRaidOrganisationEnabled()
                                 && config.getRaidChatGeofences(channel.getLatestMessageId()).size() > 0
                                 || channel.getType() == ChannelType.PRIVATE
                                 ? getLocalString("HelpMessageJoinLobbyCommand")
                                 : "") +
//                    (config.isRaidOrganisationEnabled()
//                            && config.getRaidChatGeofences(channel.getLatestMessageId()).size() > 0
//                            || channel.getType() == ChannelType.PRIVATE
//                            ? "!!activeraids\n"
//                            : "") +
                                (config.useGeofences() ? getLocalString("HelpMessageRegionCommands") : "") +
                                 getLocalString("HelpMessageOtherCommands")).queue();
            return;
        } else if (config.useGeofences() && (msg.equals(getLocalString("RegionListCommand")) || msg.equals(getLocalString("RegionsCommand")))) {
            channel.sendMessageFormat("%s, %s```%s```", author, getLocalString("RegionListMessageStart"),Geofencing.getListMessage()).queue();
            return;
        }

        UserCommand userCommand;

        if (msg.startsWith(getLocalString("AddRaidCommand")) || msg.startsWith(getLocalString("DelRaidCommand")) || msg.startsWith(getLocalString("ClearRaidLocationCommand"))) {
            userCommand = parser.parseInput(msg);
        } else {
            userCommand = parser.parseInput(msg);
        }
        final ArrayList<InputError> exceptions = userCommand.getExceptions();

        if (exceptions.size() > 0) {
            String           errorMessage = author.getAsMention() + ", I had " + ((exceptions.size() == 1) ? "a problem" : "problems") + " reading your input.\n\n";
            final InputError error        = InputError.mostSevere(exceptions);
            errorMessage += error.getErrorMessage(userCommand);
            channel.sendMessage(errorMessage).queue();
        } else {
            final String cmdStr = (String) userCommand.getArg(0).getParams()[0];

            if (cmdStr.equals(getLocalString("StatsCommand"))) {
                Pokemon[] pokemons = userCommand.buildPokemon();

                StringBuilder str = new StringBuilder(author.getAsMention() + ", " + getLocalString("StatsMessageStart"));

                for (Pokemon pokemon : pokemons) {

                    com.github.novskey.novabot.core.TimeUnit timeUnit = (com.github.novskey.novabot.core.TimeUnit) userCommand.getArg(ArgType.TimeUnit).getParams()[0];

                    int intervalLength = (int) userCommand.getArg(ArgType.Int).getParams()[0];

                    int count = dbManager.countSpawns(pokemon.getID(), timeUnit, intervalLength);

                    str.append(String.format("  %s %s%s %s %s %s%n%n",
                                             count,
                                             pokemon.name,
                                             count == 1 ? "" : "s",
                                             getLocalString("StatsMessageResult"),
                                             intervalLength,
                                             getLocalString(String.valueOf(timeUnit).toLowerCase())));

                }

                channel.sendMessage(str.toString()).queue();

                return;
            }

            if (cmdStr.contains("raid")) {
                Raid[] raids = userCommand.buildRaids();

                ArrayList<String> nonRaidBosses = new ArrayList<>();

                for (Pokemon pokemon : userCommand.getUniquePokemon()) {
                    if (!config.raidBosses.contains(pokemon.getID())) {
                        nonRaidBosses.add(Pokemon.idToName(pokemon.getID()));
                    }
                }

                if (nonRaidBosses.size() != 0) {
                    StringBuilder message = new StringBuilder(author.getAsMention() + getLocalString("ProblemReadingInput") +
                                                              getLocalString("NotPossibleRaidBossesError"));

                    for (String nonRaidBoss : nonRaidBosses) {
                        message.append(String.format("  %s%n", nonRaidBoss));
                    }

                    channel.sendMessage(message.toString()).queue();
                    return;
                }

                switch (cmdStr) {
                    case "!addraid": {
                        NotificationLimit limit = config.getNotificationLimit(guild.getMember(author));

                        boolean isSupporter = isSupporter(author.getId());

                        if (limit != null && limit.raidLimit != null && dbManager.countRaids(author.getId(), config.countLocationsInLimits()) + raids.length > limit.raidLimit) {
                            channel.sendMessageFormat("%s at your supporter level you may have a maximum of %s raid notifications set up. " +
                                                      (limit.raidLimit > 0 ? "What you tried to add would take you over this limit, please remove some raids with the !delraid command or try adding fewer raids." : ""), author, limit.raidLimit).queue();
                            return;
                        } else if (limit == null && isSupporter) {
                            novabotLog.error(String.format("LIMIT IS NULL: %s, is supporter: %s", author.getName(), true));
                        }

                        if (dbManager.notContainsUser(author.getId())) {
                            dbManager.addUser(author.getId());
                        }

                        for (Raid raid : raids) {
                            novabotLog.debug("adding raid " + raid);
                            dbManager.addRaid(author.getId(), raid);
                        }

                        String message2 = author.getAsMention() + " you will now be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());

                        final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                        Location[]     locations    = {Location.ALL};
                        if (locationsArg != null) {
                            locations = userCommand.getLocations();
                        }
                        message2 = message2 + " raids in " + Location.listToString(locations);
                        channel.sendMessage(message2).queue();

                        return;
                    }
                    case "!delraid": {
                        if (dbManager.notContainsUser(author.getId())) {
                            dbManager.addUser(author.getId());
                        }

                        for (Raid raid : raids) {
                            novabotLog.debug("deleting raid " + raid);
                            dbManager.deleteRaid(author.getId(), raid);
                        }

                        String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());

                        final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                        Location[]     locations    = {Location.ALL};
                        if (locationsArg != null) {
                            locations = userCommand.getLocations();
                        }
                        message2 = message2 + " raids in " + Location.listToString(locations);
                        channel.sendMessage(message2).queue();

                        return;
                    }
                    case "!clearraid": {
                        if (dbManager.notContainsUser(author.getId())) {
                            dbManager.addUser(author.getId());
                        }
                        novabotLog.debug("clearing raids " + Arrays.toString(raids));
                        dbManager.clearRaid(author.getId(), new ArrayList<>(Arrays.asList(raids)));

                        String message2 = String.format("%s you will no longer be notified of %s in any location", author.getAsMention(), Pokemon.listToString(userCommand.getUniquePokemon()));
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!clearraidlocation": {
                        final Location[] locations2 = userCommand.getLocations();
                        dbManager.clearLocationsRaids(author.getId(), locations2);
                        final String message2 = author.getAsMention() + " you will no longer be notified of any raids in " + Location.listToString(locations2);
                        channel.sendMessage(message2).queue();
                        break;
                    }
                }
            }

            if (cmdStr.contains("poke")) {
                final Pokemon[] pokemons = userCommand.buildPokemon();

                switch (cmdStr) {
                    case "!addpokemon": {
                        NotificationLimit limit = config.getNotificationLimit(guild.getMember(author));

                        boolean isSupporter = isSupporter(author.getId());

                        if (limit != null && limit.pokemonLimit != null && dbManager.countPokemon(author.getId(), config.countLocationsInLimits()) + pokemons.length > limit.pokemonLimit) {
                            channel.sendMessageFormat("%s at your supporter level you may have a maximum of %s pokemon notifications set up. " +
                                                      (limit.pokemonLimit > 0 ? "What you tried to add would take you over this limit, please remove some pokemon with the !delpokemon command or try adding fewer pokemon." : ""), author, limit.pokemonLimit).queue();
                            return;
                        } else if (limit == null && isSupporter) {
                            novabotLog.error(String.format("LIMIT IS NULL: %s, is supporter: %s", author.getName(), isSupporter));
                        }

                        if (dbManager.notContainsUser(author.getId())) {
                            dbManager.addUser(author.getId());
                        }
                        for (final Pokemon pokemon : pokemons) {
                            novabotLog.debug("adding pokemon " + pokemon);
                            dbManager.addPokemon(author.getId(), pokemon);
                        }
                        String message2 = author.getAsMention() + " you will now be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());
                        message2 += userCommand.getIvMessage();

                        final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                        Location[]     locations    = {Location.ALL};
                        if (locationsArg != null) {
                            locations = userCommand.getLocations();
                        }
                        message2 = message2 + " in " + Location.listToString(locations);
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!delpokemon": {
                        for (final Pokemon pokemon : pokemons) {
                            dbManager.deletePokemon(author.getId(), pokemon);
                        }
                        String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(userCommand.getUniquePokemon());
                        message2 += userCommand.getIvMessage();

                        final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                        Location[]     locations    = {Location.ALL};
                        if (locationsArg != null) {
                            locations = userCommand.getLocations();
                        }
                        message2 = message2 + " in " + Location.listToString(locations);
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!clearpokemon": {
                        dbManager.clearPokemon(author.getId(), new ArrayList<>(Arrays.asList(pokemons)));
                        final String message2 = author.getAsMention() + " you will no longer be notified of " + Pokemon.listToString(pokemons) + " in any locations";
                        channel.sendMessage(message2).queue();
                        return;
                    }
                    case "!clearpokelocation": {
                        final Location[] locations2 = userCommand.getLocations();
                        dbManager.clearLocationsPokemon(author.getId(), locations2);
                        final String message2 = author.getAsMention() + " you will no longer be notified of any pokemon in " + Location.listToString(locations2);
                        channel.sendMessage(message2).queue();
                    }
                }
            } else if (cmdStr.contains("preset")) {

                final Argument locationsArg = userCommand.getArg(ArgType.Locations);
                Location[]     locations    = {Location.ALL};
                if (locationsArg != null) {
                    locations = userCommand.getLocations();
                }

                switch (cmdStr) {
                    case "!loadpreset": {
                        NotificationLimit limit = config.getNotificationLimit(guild.getMember(author));

                        boolean isSupporter = isSupporter(author.getId());

                        if (limit != null && limit.presetLimit != null && dbManager.countPresets(author.getId(), config.countLocationsInLimits()) + locations.length > limit.presetLimit) {
                            channel.sendMessageFormat("%s at your supporter level you may have a maximum of %s preset notifications set up. " +
                                                      (limit.presetLimit > 0 ? "What you tried to add would take you over this limit, please remove some presets with the !delpreset command or try adding fewer presets." : ""), author, limit.presetLimit).queue();
                            return;
                        } else if (limit == null && isSupporter) {
                            novabotLog.error(String.format("LIMIT IS NULL: %s, is supporter: %s", author.getName(), isSupporter));
                        }

                        Object[] presetsObj =  userCommand.getArg(ArgType.Preset).getParams();
                        String[] presets = Arrays.copyOf(presetsObj,presetsObj.length,String[].class);

                        for (String preset : presets) {
                            for (Location location : locations) {
                                dbManager.addPreset(author.getId(),preset,location);
                            }
                        }

                        String message    = String.format("%s you will now be notified of anything in the %s preset%s", author.getAsMention(), Arrays.toString(presets),presets.length > 1 ? "s" : "");

                        message += " in " + Location.listToString(locations);
                        channel.sendMessage(message).queue();
                        return;
                    }
                    case "!delpreset": {
                        Object[] presetsObj =  userCommand.getArg(ArgType.Preset).getParams();
                        String[] presets = Arrays.copyOf(presetsObj,presetsObj.length,String[].class);

                        for (String preset : presets) {
                            for (Location location : locations) {
                                dbManager.addPreset(author.getId(), preset, location);
                            }
                        }

                        String message = String.format("%s you will no longer be notified of anything in the %s preset%s", author.getAsMention(), Arrays.toString(presets), presets.length > 1 ? "s" : "");

                        message += " in " + Location.listToString(locations);
                        channel.sendMessage(message).queue();
                        return;
                    }
                    case "!clearpreset": {
                        Object[] presetsObj =  userCommand.getArg(ArgType.Preset).getParams();
                        String[] presets = Arrays.copyOf(presetsObj,presetsObj.length,String[].class);
                        dbManager.clearPreset(author.getId(), presets);
                        channel.sendMessageFormat("%s you will no longer be notified of %s preset%s in any locations",author,Arrays.toString(presets),presets.length > 1 ? "s" : "").queue();
                        return;
                    }
                    case "!clearpresetlocation": {
                        locations = userCommand.getLocations();
                        dbManager.clearLocationsPresets(author.getId(),locations);
                        channel.sendMessageFormat("%s you will no longer be notified of any presets in %s",author,Location.listToString(locations)).queue();
                        return;
                    }
                }
            } else if (cmdStr.equals("!clearlocation")) {
                final Location[] locations2 = userCommand.getLocations();
                dbManager.clearLocationsPokemon(author.getId(), locations2);
                dbManager.clearLocationsRaids(author.getId(), locations2);
                final String message2 = author.getAsMention() + " you will no longer be notified of any pokemon or raids in " + Location.listToString(locations2);
                channel.sendMessage(message2).queue();
            }
        }
    }

    private String getLocalString(String key){
        return formatStr(formatStr(getLocalString(getLocalString(key,messagesBundle),timeUnitsBundle),messagesBundle),messagesBundle);
    }

    private String getLocalString(String key, ResourceBundle resourceBundle){
        if(resourceBundle.containsKey(key)) {
            return formatStr(resourceBundle.getString(key), resourceBundle);
        }else{
            return key;
        }
    }
    
    private String formatStr(String string, ResourceBundle resourceBundle) {
        for (String key : resourceBundle.keySet()) {
            if(resourceBundle.containsKey(key)) {
                string = string.replace("<" + key + ">", resourceBundle.getString(key));
            }
        }
        return string;
    }

    public void parseRaidChatMsg(User author, String msg, TextChannel textChannel) {
        if (!msg.startsWith("!") || author.isBot()) return;

        if (msg.startsWith("!joinraid")) {
            String groupCode = msg.substring(msg.indexOf("raid ") + 5).trim();

            RaidLobby lobby = lobbyManager.getLobby(groupCode);

            if (lobby == null) {
                textChannel.sendMessageFormat("%s sorry, there are no active raid lobbies with the lobby code `%s`", author, groupCode).queue();
            } else {
                if (lobby.containsUser(author.getId())) {
                    textChannel.sendMessageFormat("%s you are already in that raid lobby!", author).queue();
                    return;
                }

                lobby.joinLobby(author.getId());
                alertRaidChats(config.getRaidChats(lobby.spawn.getGeofences()), String.format(
                        "%s joined %s raid in %s. There are now %s users in the lobby. Join the lobby by clicking the ✅ or by typing `!joinraid %s`.",
                        author.getAsMention(),
                        (lobby.spawn.bossId == 0 ? String.format("lvl %s egg", lobby.spawn.raidLevel) : lobby.spawn.properties.get("pkmn")),
                        lobby.getChannel().getAsMention(),
                        lobby.memberCount(),
                        lobby.lobbyCode
                                                                                             ));
            }

//        } else if (msg.equals("!activeraids")) {
//            ArrayList<GeofenceIdentifier> geofences = config.getRaidChatGeofences(textChannel.getId());
//
//            if (geofences.size() > 0 || textChannel.getType() == ChannelType.PRIVATE) {
//
//                StringBuilder noLobbiesMsg = null;
//
//                for (GeofenceIdentifier geofence : geofences) {
//
//                    ArrayList<RaidLobby> lobbies = lobbyManager.getLobbiesByGeofence(geofence);
//
//                    if (lobbies.size() == 0) {
//                        if (noLobbiesMsg == null) {
//                            noLobbiesMsg = new StringBuilder(String.format("%s, there are no active lobbies in %s", author.getAsMention(), geofence.name));
//                        } else {
//                            noLobbiesMsg.append(String.format(", %s", geofence.name));
//                        }
//                        continue;
//                    }
//
//                    textChannel.sendMessageFormat("%s, there are %s active lobbies in %s", author, lobbies.size(), geofence.name).queue();
//
//                    for (RaidLobby lobby : lobbies) {
//                        textChannel.sendMessage(lobby.getInfoMessage()).queue(m -> m.addReaction(WHITE_GREEN_CHECK).queue());
//                    }
//                }
//
//                if (noLobbiesMsg != null) {
//                    textChannel.sendMessage(noLobbiesMsg.toString()).queue();
//                }
//
//            }
        }
    }

    public void parseRaidLobbyMsg(User author, String msg, TextChannel textChannel) {
        if (!msg.startsWith("!")) return;

        RaidLobby lobby = lobbyManager.getLobbyByChannelId(textChannel.getId());

        if (msg.equals("!help")) {
            textChannel.sendMessage("My raid lobby commands are: " +
                                    "```" +
                                    "!leave\n" +
                                    "!map\n" +
                                    "!timeleft\n" +
                                    "!status\n" +
                                    (lobby.spawn.bossId != 0 ? "!boss\n" : "") +
                                    (lobby.spawn.bossId != 0 ? "!maxcp\n" : "") +
                                    "!team\n" +
                                    "!code\n" +
                                    "```").queue();
            return;
        }

        if (msg.equals("!leave")) {
            lobby.leaveLobby(author.getId());
            return;
        }

        if (msg.equals("!map")) {
            textChannel.sendMessage(lobby.spawn.properties.get("gmaps")).queue();
            return;
        }

        if (msg.equals("!timeleft")) {

            if (lobby.spawn.bossId != 0) {
                textChannel.sendMessageFormat("Raid ends at %s (%s remaining)", lobby.spawn.getDisappearTime(printFormat24hr), lobby.spawn.timeLeft(lobby.spawn.raidEnd)).queue();
            } else {
                textChannel.sendMessageFormat("Raid starts at %s (%s remaining)", lobby.spawn.getStartTime(printFormat24hr), lobby.spawn.timeLeft(lobby.spawn.battleStart)).queue();
            }
            return;
        }

        if (msg.equals("!status")) {
            textChannel.sendMessage(lobby.getStatusMessage()).queue();
            return;
        }

        if (msg.equals("!boss")) {
            if (lobby.spawn.bossId == 0) {
                textChannel.sendMessageFormat("The boss hasn't spawned yet. It will appear at %s",
                                              lobby.spawn.properties.get("24h_start")).queue();
            } else {
                textChannel.sendMessage(lobby.getBossInfoMessage()).queue();
            }
            return;
        }

        if (msg.equals("!maxcp")) {
            if (lobby.spawn.bossId == 0) {
                textChannel.sendMessageFormat("The boss hasn't spawned yet. It will appear at %s",
                                              lobby.spawn.properties.get("24h_start")).queue();
            } else {
                textChannel.sendMessage(lobby.getMaxCpMessage()).queue();
            }
            return;
        }

        if (msg.equals("!team")) {
            textChannel.sendMessage(lobby.getTeamMessage()).queue();
            return;
        }

        if (msg.equals("!code")) {
            textChannel.sendMessageFormat("This lobby can be joined using the command `!joinraid %s`", lobby.lobbyCode).queue();
        }
    }

    public void setup() {
        messagesBundle = ResourceBundle.getBundle("Messages");
        timeUnitsBundle = ResourceBundle.getBundle("TimeUnits");
        TimeUnit.SetBundle(timeUnitsBundle);
        loadConfig();
        loadSuburbs();
        if (config.useGeofences() && (geofencing == null || !geofencing.loaded)) {
            loadGeofences();
        }

        Spawn.setNovaBot(this);

        commands = new Commands(config);
        parser = new Parser(this);

        reverseGeocoder = new ReverseGeocoder(this);

        dbManager = new DBManager(this);

        dbManager.scanDbConnect();
        dbManager.novabotdbConnect();

        if (config.isRaidOrganisationEnabled()) {
            lobbyManager = new LobbyManager(this);
            RaidNotificationSender.nextId = dbManager.highestRaidLobbyId() + 1;
        }
    }

    public void shutDown() {
        novabotLog.info("Shutting down...");
        if (jda != null) {
            jda.shutdown();
        }
        System.exit(0);
    }

    private boolean isAdmin(User author) {
        for (Role role : guild.getMember(author).getRoles()) {
            if (role.getId().equals(config.getAdminRole())) return true;
        }
        return false;
    }

    private boolean isSupporter(final String userID) {
        final Member member = guild.getMemberById(userID);

        if (member == null || member.getRoles() == null) return false;

        for (final Role role : member.getRoles()) {
            if (role == null) continue;
            if (config.getSupporterRoles().contains(role.getId())) return true;
        }
        return false;
    }

    public void start() {
        novabotLog.info("Connecting to db");
        novabotLog.info("Connected");
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setAutoReconnect(true)
                    .setGame(Game.of(Game.GameType.DEFAULT, "Pokemon Go"))
                    .setToken(config.getToken())
                    .buildBlocking();

            jda.addEventListener(new MessageListener(this));


            for (Guild guild1 : jda.getGuilds()) {
                guild = guild1;
                novabotLog.debug(guild.getName());

                if(config.getCommandChannelId() != null) {
                    TextChannel channel = guild.getTextChannelById(config.getCommandChannelId());
                    if (channel != null) {
                        if (config.showStartupMessage()) {
                            channel.sendMessage(getLocalString("StartUpMessage")).queue();
                        }
                    } else {
                        novabotLog.info(String.format("couldn't find command channel by id from config: %s", config.getCommandChannelId()));
                    }
                }
            }

            config.loadEmotes();

            guild.getMember(jda.getSelfUser()).getRoles().forEach(System.out::println);

            guild.getInvites().queue(success -> invites.addAll(success));

            if (config.loggingEnabled()) {
                roleLog = jda.getTextChannelById(config.getRoleLogId());
                userUpdatesLog = jda.getTextChannelById(config.getUserUpdatesId());
            }

            if (config.useScanDb()) {
                notificationsManager = new NotificationsManager(this, testing);
            }

        } catch (LoginException | InterruptedException | RateLimitedException ex2) {
            ex2.printStackTrace();
        }


        novabotLog.info("connected");
    }
}
