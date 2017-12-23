package notifier;

import Util.UtilityFunctions;
import core.AlertChannel;
import core.NovaBot;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokemon.PokeSpawn;
import pokemon.Pokemon;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;


public class PokeNotificationSender extends NotificationSender implements Runnable {
    private final ArrayList<PokeSpawn> newPokemon;

    public static final Logger notificationLog = LoggerFactory.getLogger("Poke-Notif-Sender");

    public PokeNotificationSender(NovaBot novaBot, ArrayList<PokeSpawn> newPokemon) {
        this.newPokemon = newPokemon;
        this.novaBot = novaBot;
    }

    @Override
    public void run() {
        try {
            for (final PokeSpawn pokeSpawn : this.newPokemon) {
                notificationLog.info("Checking if anyone wants: " + Pokemon.idToName(pokeSpawn.id));

                if (pokeSpawn.disappearTime.isBefore(ZonedDateTime.now(UtilityFunctions.UTC))) {
                    notificationLog.info("Already despawned, skipping");
                    continue;
                }

                HashSet<String> toNotify = new HashSet<>(novaBot.dbManager.getUserIDsToNotify(pokeSpawn));

                ArrayList<String> matchingPresets = novaBot.config.findMatchingPresets(pokeSpawn);

                for (String preset : matchingPresets) {
                    toNotify.addAll(novaBot.dbManager.getUserIDsToNotify(preset, pokeSpawn));
                }

                if (toNotify.size() == 0) {
                    notificationLog.info("no-one wants this pokemon");
                } else {
                    final Message message = pokeSpawn.buildMessage("formatting.ini");
                    notificationLog.info("Built message for pokespawn");

                    toNotify.forEach(userID -> this.notifyUser(userID, message));
                }

                for (GeofenceIdentifier geofenceIdentifier : pokeSpawn.getGeofences()) {
                    ArrayList<AlertChannel> channels = novaBot.config.getPokeChannels(geofenceIdentifier);
                    if (channels == null) continue;
                    for (AlertChannel channel : channels) {
                        if (channel != null) {
                            checkAndPost(channel, pokeSpawn);
                        }
                    }
                }
                ArrayList<AlertChannel> noGeofences = novaBot.config.getNonGeofencedPokeChannels();

                if (noGeofences != null) {
                    for (AlertChannel channel : noGeofences) {
                        if (channel != null) {
                            checkAndPost(channel, pokeSpawn);
                        }
                    }
                }
            }
        } catch (Exception e) {
            notificationLog.error("An exception ocurred in poke-notif-sender", e);
        }

    }

    private void checkAndPost(AlertChannel channel, PokeSpawn pokeSpawn) {
        if (novaBot.config.matchesFilter(novaBot.config.pokeFilters.get(channel.filterName), pokeSpawn, channel.filterName)) {
            notificationLog.info("Pokemon passed filter, posting to Discord");
            sendChannelAlert(pokeSpawn.buildMessage(channel.formattingName), channel.channelId);
        } else {
            notificationLog.info(String.format("Pokemon didn't pass %s filter, not posting", channel.filterName));
        }
    }

    private void notifyUser(final String userID, final Message message) {
        final User user = novaBot.jda.getUserById(userID);
        if (user == null) return;


        ZonedDateTime lastChecked = novaBot.lastUserRoleChecks.get(userID);
        ZonedDateTime currentTime = ZonedDateTime.now(UtilityFunctions.UTC);
        if (lastChecked == null || lastChecked.isBefore(currentTime.minusMinutes(10))) {
            notificationLog.info(String.format("Checking supporter status of %s", user.getName()));
            novaBot.lastUserRoleChecks.put(userID, currentTime);
            if (checkSupporterStatus(user)) {
                user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
            }
        } else {
            user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
        }
    }

    private void sendChannelAlert(Message message, String channelId) {
        notificationLog.info("Sending public alert message to channel " + channelId);
        novaBot.jda.getTextChannelById(channelId).sendMessage(message).queue(m -> notificationLog.info("Successfully sent message."));
    }
}
