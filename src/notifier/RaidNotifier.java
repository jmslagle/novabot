package notifier;

import core.DBManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.utils.SimpleLog;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.DEBUG;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

/**
 * Created by Owner on 27/06/2017.
 */
public class RaidNotifier implements Runnable
{
    private final JDA jda;
    private final boolean testing;

    private static SimpleLog notifierLog = SimpleLog.getLog("RaidNotifier");

    public RaidNotifier(final JDA jda, boolean testing) {
        this.jda = jda;
        this.testing = testing;
    }

    @Override
    public void run() {
        notifierLog.log(INFO,"checking for raids to notify");
        final Thread thread = new Thread(new RaidNotificationSender(this.jda, DBManager.getCurrentRaids(),testing));
        thread.start();
//        PokeNotificationSender sender = new PokeNotificationSender(this.jda, DBManager.getNewPokemon(),testing);
//        sender.run();
        notifierLog.log(DEBUG,"Done checking");
    }
}
