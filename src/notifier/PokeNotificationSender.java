package notifier;

import core.DBManager;
import core.MessageListener;
import pokemon.PokeSpawn;
import pokemon.Pokemon;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.ArrayList;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

class PokeNotificationSender implements Runnable
{
    private final JDA jda;
    private final ArrayList<PokeSpawn> newPokemon;
    private final boolean testing;

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
            notificationLog.log(INFO,"Checking if anyone wants: " + Pokemon.idToName(pokeSpawn.id));

            if(pokeSpawn.disappearTime.before(DBManager.getCurrentTime())){
                notificationLog.log(INFO,"Already despawned, skipping");
                continue;
            }

            if(!testing) {
                final ArrayList<String> userIDs = DBManager.getUserIDsToNotify(pokeSpawn);
                if (userIDs.size() == 0) {
                    notificationLog.log(INFO,"no-one wants this pokemon");
                } else {
                    final Message message = pokeSpawn.buildMessage();
                    notificationLog.log(INFO,"Built message for pokespawn");
                    userIDs.stream().filter(MessageListener::isSupporter).forEach(userID -> this.notifyUser(userID, message));
                }
            }else{
                if(DBManager.shouldNotify("107730875596169216",pokeSpawn)){
                    final Message message = pokeSpawn.buildMessage();
                    notificationLog.log(INFO,"Built message for pokespawn");

                    notifyUser("107730875596169216",message);
                }
            }
        }
    }
}
