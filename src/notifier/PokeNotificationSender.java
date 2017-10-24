package notifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.DBManager;
import core.MessageListener;
import core.PokeChannel;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import pokemon.PokeSpawn;
import pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Random;

import static core.MessageListener.config;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

class PokeNotificationSender implements Runnable {
    private final JDA jda;
    private final ArrayList<PokeSpawn> newPokemon;
    private final boolean testing;

    Random random = new Random();

    private static SimpleLog notificationLog = SimpleLog.getLog("Poke-Notif-Sender");

    public PokeNotificationSender(final JDA jda, final ArrayList<PokeSpawn> newPokemon, boolean testing) {
        this.jda = jda;
        this.newPokemon = newPokemon;
        this.testing = testing;
    }

    private void notifyUser(final String userID, final Message message) {
        final User user = this.jda.getUserById(userID);
        final Thread thread = new Thread(new UserNotifier(user, message, false));
        thread.start();
//
//        UserNotifier notifier = new UserNotifier(user,message);
//        notifier.run();
    }

    @Override
    public void run() {
        for (final PokeSpawn pokeSpawn : this.newPokemon) {
            notificationLog.log(INFO, "Checking if anyone wants: " + Pokemon.idToName(pokeSpawn.id));

            if (pokeSpawn.disappearTime.before(DBManager.getCurrentTime())) {
                notificationLog.log(INFO, "Already despawned, skipping");
                continue;
            }

            if (!testing) {
                final ArrayList<String> userIDs = DBManager.getUserIDsToNotify(pokeSpawn);

                if (userIDs.size() == 0) {
                    notificationLog.log(INFO, "no-one wants this pokemon");
                } else {
                    Message supporterMessage = pokeSpawn.buildMessage();
                    Message publicMessage = pokeSpawn.buildPublicMessage();

                    notificationLog.log(INFO, "Built message for pokespawn");

                    userIDs.stream().filter(MessageListener::isSupporter).forEach(userID -> this.notifyUser(userID, supporterMessage));
                    userIDs.stream().filter((u) -> !MessageListener.isSupporter(u)).forEach(userID -> this.notifyUser(userID,publicMessage));
                }

                if(pokeSpawn.getGeofenceIds().size() == 0){
                    ArrayList<PokeChannel> noGeofences = config.getNonGeofencedChannels();

                    if(noGeofences != null) {
                        for (PokeChannel channel : config.getNonGeofencedChannels()) {
                            postToChannel(channel, pokeSpawn);
                        }
                    }
                }else {
                    for (GeofenceIdentifier geofenceIdentifier : pokeSpawn.getGeofenceIds()) {
                        for (PokeChannel channel : config.getPokeChannels(geofenceIdentifier)) {
                            postToChannel(channel,pokeSpawn);
                        }
                    }
                }

            } else {
                if (DBManager.shouldNotify("107730875596169216", pokeSpawn)) {
                    final Message message = pokeSpawn.buildMessage();
                    notificationLog.log(INFO, "Built message for pokespawn");

                    notifyUser("107730875596169216", message);
                }
            }
        }

    }

    private void postToChannel(PokeChannel channel, PokeSpawn pokeSpawn) {
        JsonElement pokeFilter = config.searchPokemonFilter(config.filters.get(channel.filterName), pokeSpawn.id);
//
        if (pokeSpawn.getGeofenceIds().size() > 0) {
            if (pokeFilter.isJsonObject()) {
                JsonObject obj = pokeFilter.getAsJsonObject();

                JsonElement maxObj = obj.get("max_iv");
                JsonElement minObj = obj.get("min_iv");

                float max = maxObj == null ? 100 : maxObj.getAsFloat();
                float min = minObj == null ? 0 : minObj.getAsFloat();

                if (pokeSpawn.iv <= max && pokeSpawn.iv >= min) {
                    notificationLog.log(INFO, String.format("Pokemon between specified ivs (%s,%s), posting to Discord", min, max));
                    sendPublicAlert(pokeSpawn.buildPublicMessage(), pokeSpawn.getGeofenceIds());
                } else {
                    notificationLog.log(INFO, String.format("Pokemon not specified ivs (%s,%s), posting to Discord", min, max));

                }
            } else {
                if (pokeFilter.getAsBoolean()) {
                    notificationLog.log(INFO, "Pokemon enabled in filter, posting to Discord");
                    sendPublicAlert(pokeSpawn.buildPublicMessage(), pokeSpawn.getGeofenceIds());
                } else {
                    notificationLog.log(INFO, "Pokemon not enabled in filter, not posting");
                }
            }
        }
    }

    private void sendPublicAlert(Message message, ArrayList<GeofenceIdentifier> geofences) {
        notificationLog.log(INFO, "Sending public alert message to geofenced channels");
        for (GeofenceIdentifier identifier : geofences) {
            for (PokeChannel channel : config.getPokeChannels(identifier)) {

                notificationLog.log(INFO, String.format("Sending message to channel %s from geofence %s", channel.channelId, identifier));

                channel.getChannel().sendMessage(message).queue(m -> notificationLog.log(INFO, "Successfully sent message."));

            }

        }
    }
}
