package notifier;

import core.DBManager;
import core.MessageListener;
import core.PokeSpawn;
import core.Pokemon;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;

class NotificationSender implements Runnable
{
    private final JDA jda;
    private final ArrayList<PokeSpawn> newPokemon;

    public NotificationSender(final JDA jda, final ArrayList<PokeSpawn> newPokemon) {
        this.jda = jda;
        this.newPokemon = newPokemon;
    }

    private void notifyUser(final String userID, final Message message) {
        final User user = this.jda.getUserById(userID);
        final Thread thread = new Thread(new UserNotifier(user, message));
        thread.start();
    }

    @Override
    public void run() {
        for (final PokeSpawn pokeSpawn : this.newPokemon) {
            System.out.println("Checking if anyone wants: " + Pokemon.idToName(pokeSpawn.id));
            final ArrayList<String> userIDs = DBManager.getUserIDsToNotify(pokeSpawn);
            if (userIDs.size() == 0) {
                System.out.println("noone wants this pokemon");
            }
            else {
                final Message message = pokeSpawn.buildMessage();
                System.out.println("Built message for pokespawn");
                userIDs.stream().filter(MessageListener::isSupporter).forEach(userID -> this.notifyUser(userID, message));
            }
        }
    }
}
