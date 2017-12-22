package notifier;

import core.AlertChannel;
import core.NovaBot;
import core.Util;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raids.RaidLobby;
import raids.RaidSpawn;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Owner on 27/06/2017.
 */
public class RaidNotificationSender extends NotificationSender implements Runnable {


    public static final Logger notificationLog = LoggerFactory.getLogger("Raid-Notif-Sender");
    private final ArrayList<RaidSpawn> currentRaids;
    private static boolean firstRun = true;

    public static int nextId = 1;

    public RaidNotificationSender(NovaBot novaBot, ArrayList<RaidSpawn> currentRaids) {
        this.novaBot = novaBot;
        this.currentRaids = currentRaids;
    }

    @Override
    public void run() {
        try {
            if (firstRun) {
                notificationLog.info("not sending messages on first run");
                firstRun = false;

                if (novaBot.config.isRaidOrganisationEnabled()) {
                    novaBot.lobbyManager.addLobbies(novaBot.dbManager.getActiveLobbies());
                }
                return;
            }

            for (final RaidSpawn raidSpawn : this.currentRaids) {
                notificationLog.info("Checking " + raidSpawn);

                if (raidSpawn.properties.get("time_left_start").startsWith("-") && raidSpawn.bossId == 0) {
                    notificationLog.info("Raid started but no boss Id, not posting");
                    continue;
                }

                if (raidSpawn.raidEnd.isBefore(ZonedDateTime.now(Util.UTC))) {
                    notificationLog.info("Raid already ended, not posting");
                    continue;
                }

                if (novaBot.config.isRaidOrganisationEnabled()) {
                    RaidLobby lobbyFromId = novaBot.lobbyManager.getLobbyByGymId(raidSpawn.gymId);

                    if (lobbyFromId != null && lobbyFromId.spawn.bossId == 0) {
                        notificationLog.info("Raid already has a lobby, but the egg has now hatched, updating lobby");
                        raidSpawn.setLobbyCode(lobbyFromId.lobbyCode);
                        lobbyFromId.spawn = raidSpawn;
                        lobbyFromId.alertEggHatched();
                    } else {

                        if (raidSpawn.raidLevel >= 3) {
                            if (nextId == 10000) {
                                nextId = 1;
                            }

                            raidSpawn.setLobbyCode(nextId);

                            novaBot.lobbyManager.newRaid(raidSpawn.getLobbyCode(), raidSpawn);

                            nextId++;
                        }
                    }
                }

                HashSet<String> toNotify = new HashSet<>();

                if (raidSpawn.bossId != 0) {
                    notificationLog.info("Checking if anyone wants: " + raidSpawn);

                    toNotify.addAll(novaBot.dbManager.getUserIDsToNotify(raidSpawn));
                }

                ArrayList<String> matchingPresets = novaBot.config.findMatchingPresets(raidSpawn);

                for (String preset : matchingPresets) {
                    toNotify.addAll(novaBot.dbManager.getUserIDsToNotify(preset, raidSpawn));
                }

                toNotify.forEach(id -> notifyUser(id, raidSpawn.buildMessage("formatting.ini"), raidSpawn.raidLevel >= 3 && novaBot.config.isRaidOrganisationEnabled()));

                if (!novaBot.config.isRaidChannelsEnabled()) continue;

                for (GeofenceIdentifier identifier : raidSpawn.getGeofences()) {
                    ArrayList<AlertChannel> channels = novaBot.config.getRaidChannels(identifier);

                    if (channels == null) continue;

                    for (AlertChannel channel : channels) {
                        if (channel != null) {
                            checkAndPost(channel, raidSpawn);
                        }
                    }
                }

                ArrayList<AlertChannel> noGeofences = novaBot.config.getNonGeofencedRaidChannels();

                if (noGeofences != null) {
                    for (AlertChannel channel : noGeofences) {
                        if (channel != null) {
                            checkAndPost(channel, raidSpawn);
                        }
                    }
                }
            }
        } catch (Exception e){
            notificationLog.error("An error occurred in Raid-Notif-Sender",e);
        }
    }

    private void checkAndPost(AlertChannel channel, RaidSpawn raidSpawn) {
        notificationLog.info(String.format("Checking %s against filter %s", raidSpawn, channel.filterName));
        if (novaBot.config.matchesFilter(novaBot.config.raidFilters.get(channel.filterName), raidSpawn)) {
            notificationLog.info("Raid passed filter, posting to Discord");
            sendChannelAlert(raidSpawn.buildMessage(channel.formattingName),channel.channelId, raidSpawn.raidLevel);
        }
    }

    private void notifyUser(final String userID, final Message message, boolean showTick) {
        final User user = novaBot.jda.getUserById(userID);
        if (user == null) return;

        ZonedDateTime lastChecked = novaBot.lastUserRoleChecks.get(userID);
        ZonedDateTime currentTime = ZonedDateTime.now(Util.UTC);
        if (lastChecked == null || lastChecked.isBefore(currentTime.minusMinutes(10))) {
            notificationLog.info(String.format("Checking supporter status of %s", user.getName()));
            novaBot.lastUserRoleChecks.put(userID, currentTime);
            if (checkSupporterStatus(user)) {
                user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(
                        msg -> {
                            if (showTick) {
                                msg.addReaction(WHITE_GREEN_CHECK).queue();
                            }
                        }
                                                                                             ));
            }
        } else {
            user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(
                    msg -> {
                        if (showTick) {
                            msg.addReaction(WHITE_GREEN_CHECK).queue();
                        }
                    }
                                                                                         ));
        }
    }

    private void sendChannelAlert(Message message, String channelId, int raidLevel) {
        notificationLog.info("Sending public alert message to channel " + channelId);
        novaBot.jda.getTextChannelById(channelId).sendMessage(message).queue(m -> {
            if (novaBot.config.isRaidOrganisationEnabled() && raidLevel >= 3) {
                System.out.println(String.format("adding reaction to raid with raidlevel %s", raidLevel));
                m.addReaction(WHITE_GREEN_CHECK).queue();
            }
        });
    }
}
