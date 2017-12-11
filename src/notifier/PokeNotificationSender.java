package notifier;

import core.AlertChannel;
import core.DBManager;
import core.MessageListener;
import core.Util;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import pokemon.PokeSpawn;
import pokemon.Pokemon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import static core.MessageListener.config;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

public class PokeNotificationSender extends NotificationSender implements Runnable {
    private final JDA jda;
    private final ArrayList<PokeSpawn> newPokemon;

    public static final SimpleLog notificationLog = SimpleLog.getLog("Poke-Notif-Sender");

    public PokeNotificationSender(final JDA jda, final ArrayList<PokeSpawn> newPokemon, boolean testing) {
        this.jda = jda;
        this.newPokemon = newPokemon;
    }

    private void notifyUser(final String userID, final Message message) {
        final User user = this.jda.getUserById(userID);
        if (user == null) return;


        Timestamp lastChecked = MessageListener.lastUserRoleChecks.get(userID);
        Timestamp currentTime = Util.getCurrentTime(config.getTimeZone());
        if (lastChecked == null || lastChecked.before(new Timestamp(currentTime.getTime() - (10 * 60 * 1000)))){
            notificationLog.log(INFO, String.format("Checking supporter status of %s", user.getName()));
            MessageListener.lastUserRoleChecks.put(userID,currentTime);
            checkSupporterStatus(user);
        }else {
            user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
        }
    }

    @Override
    public void run() {
        for (final PokeSpawn pokeSpawn : this.newPokemon) {
            notificationLog.log(INFO, "Checking if anyone wants: " + Pokemon.idToName(pokeSpawn.id));

            if (pokeSpawn.disappearTime.before(Util.getCurrentTime(config.getTimeZone()))) {
                notificationLog.log(INFO, "Already despawned, skipping");
                continue;
            }

//            if (!testing) {

            HashSet<String> toNotify = new HashSet<>(DBManager.getUserIDsToNotify(pokeSpawn));

            ArrayList<String> matchingPresets = config.findMatchingPresets(pokeSpawn);

            for (String preset : matchingPresets) {
                toNotify.addAll(DBManager.getUserIDsToNotify(preset, pokeSpawn));
            }

            if (toNotify.size() == 0) {
                notificationLog.log(INFO, "no-one wants this pokemon");
            } else {
                final Message message = pokeSpawn.buildMessage("formatting.ini");
                notificationLog.log(INFO, "Built message for pokespawn");

                toNotify.forEach(userID -> this.notifyUser(userID, message));
            }

            for (GeofenceIdentifier geofenceIdentifier : pokeSpawn.getGeofences()) {
                ArrayList<AlertChannel> channels = config.getPokeChannels(geofenceIdentifier);
                if (channels == null) continue;
                for (AlertChannel channel : channels) {
                    checkAndPost(channel, pokeSpawn);
                }
            }
            ArrayList<AlertChannel> noGeofences = config.getNonGeofencedPokeChannels();

            if (noGeofences != null) {
                for (AlertChannel channel : noGeofences) {
                    checkAndPost(channel, pokeSpawn);
                }
            }

//            } else {
//                if (DBManager.shouldNotify("107730875596169216", pokeSpawn)) {
//                    final Message message = pokeSpawn.buildMessage("formatting.ini");
//                    notificationLog.log(INFO, "Built message for pokespawn");
//
//                    notifyUser("107730875596169216", message);
//                }
//        }
        }

    }


    private void checkAndPost(AlertChannel channel, PokeSpawn pokeSpawn) {
        if (config.matchesFilter(config.pokeFilters.get(channel.filterName), pokeSpawn, channel.filterName)) {
            notificationLog.log(INFO, "Pokemon passed filter, posting to Discord");
            sendChannelAlert(pokeSpawn.buildMessage(channel.formattingName), channel.channelId);
        } else {
            notificationLog.log(INFO, String.format("Pokemon didn't pass %s filter, not posting", channel.filterName));
        }
    }

    private void sendChannelAlert(Message message, String channelId) {
        notificationLog.log(INFO, "Sending public alert message to channel " + channelId);
        jda.getTextChannelById(channelId).sendMessage(message).queue(m -> notificationLog.log(INFO, "Successfully sent message."));
    }
}
