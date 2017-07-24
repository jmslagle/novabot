package notifier;

import core.DBManager;
import core.MessageListener;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import raids.RaidLobby;
import raids.RaidSpawn;

import java.util.ArrayList;

import static core.MessageListener.WHITE_GREEN_CHECK;
import static core.MessageListener.config;
import static core.MessageListener.lobbyManager;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

/**
 * Created by Owner on 27/06/2017.
 */
public class RaidNotificationSender implements Runnable {


    private static SimpleLog notificationLog = SimpleLog.getLog("Raid-Notif-Sender");
    private final JDA jda;
    private final ArrayList<RaidSpawn> currentRaids;
    private final boolean testing;
    private static boolean firstRun = true;

    public static int nextId = 1;

    public RaidNotificationSender(JDA jda, ArrayList<RaidSpawn> currentRaids, boolean testing) {
        this.jda = jda;
        this.currentRaids = currentRaids;
        this.testing = testing;
    }


    private void notifyUser(final String userID, final Message message, boolean showTick) {
        final User user = this.jda.getUserById(userID);
        final Thread thread = new Thread(new UserNotifier(user, message, true));
        thread.start();
//
//        UserNotifier notifier = new UserNotifier(user,message);
//        notifier.run();
    }

    @Override
    public void run() {
        if(firstRun){
            notificationLog.log(INFO,"not sending messages on first run");
            firstRun = false;

            if(config.isRaidOrganisationEnabled()) {
                lobbyManager.addLobbies(DBManager.getActiveLobbies());
            }
            return;
        }
        for (final RaidSpawn raidSpawn : this.currentRaids) {
            notificationLog.log(INFO,"Checking " + raidSpawn);

            if (raidSpawn.properties.get("time_left_start").startsWith("-") && raidSpawn.bossId == 0) {
                notificationLog.log(INFO, "Raid started but no boss Id, not posting");
                continue;
            }

            if (raidSpawn.raidEnd.before(DBManager.getCurrentTime())){
                notificationLog.log(INFO,"Raid already ended, not posting");
                continue;
            }

            if(config.isRaidOrganisationEnabled()) {
                RaidLobby lobbyFromId = lobbyManager.getLobbyByGymId(raidSpawn.gymId);

                if(lobbyFromId != null && lobbyFromId.spawn.bossId == 0){
                    notificationLog.log(INFO,"Raid already has a lobby, but the egg has now hatched, updating lobby");
                    raidSpawn.setLobbyCode(lobbyFromId.lobbyCode);
                    lobbyFromId.spawn = raidSpawn;
                    lobbyFromId.alertEggHatched();
                }else {

                    if(raidSpawn.raidLevel >= 3) {
                        if (nextId == 10000) {
                            nextId = 1;
                        }

                        raidSpawn.setLobbyCode(nextId);

                        MessageListener.lobbyManager.newRaid(raidSpawn.getLobbyCode(), raidSpawn);

                        nextId++;
                    }
                }
            }

            if (raidSpawn.bossId != 0) {
                notificationLog.log(INFO, "Checking if anyone wants: " + raidSpawn);

                DBManager.getUserIDsToNotify(raidSpawn).forEach(id -> notifyUser(id, raidSpawn.buildMessage(),raidSpawn.raidLevel >= 3));
            }

            if(!config.isRaidChannelsEnabled()) continue;

//            notifyUser("107730875596169216",raidSpawn.buildMessage(),true);

            for (GeofenceIdentifier identifier : raidSpawn.getGeofences()) {
                String id = config.getGeofenceChannelId(identifier);

                if(id != null){
                    jda.getTextChannelById(id).sendMessage(raidSpawn.buildMessage()).queue(m -> {
                        if(config.isRaidOrganisationEnabled() && raidSpawn.raidLevel >= 3) {
                            System.out.println(String.format("adding reaction to raid with raidlevel %s", raidSpawn.raidLevel));
                            m.addReaction(WHITE_GREEN_CHECK).queue();
                        }
                    });
                }
            }

//            if(!testing) {
////                final ArrayList<String> userIDs = DBManager.getUserIDsToNotify(pokeSpawn);
////                if (userIDs.size() == 0) {
////                    notificationLog.log(INFO,"no-one wants this pokemon");
////                } else {
////                    final Message message = pokeSpawn.buildMessage();
////                    notificationLog.log(INFO,"Built message for pokespawn");
////                    userIDs.stream().filter(MessageListener::isSupporter).forEach(userID -> this.notifyUser(userID, message));
////                }
//            }else{
////                if(DBManager.shouldNotify("107730875596169216",pokeSpawn)){
////                    final Message message = pokeSpawn.buildMessage();
////                    notificationLog.log(INFO,"Built message for pokespawn");
////
////                    notifyUser("107730875596169216",message);
////                }
//            }
        }
    }
}
