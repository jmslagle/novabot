package notifier;

import core.DBManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.lang.management.ManagementFactory;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.DEBUG;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

public class PokeNotifier implements Runnable
{
    private final JDA jda;
    private final boolean testing;
    private final NotificationsManager manager;

    private static final SimpleLog notifierLog = SimpleLog.getLog("PokeNotifier");

    public PokeNotifier(NotificationsManager manager, final JDA jda, boolean testing) {
        this.manager = manager;
        this.jda = jda;
        this.testing = testing;
    }

    @Override
    public void run() {
        notifierLog.log(DEBUG,"Total threads: " + ManagementFactory.getThreadMXBean().getThreadCount());
        notifierLog.log(INFO,"checking for pokemon to notify");
        manager.pokeNotifSenderExecutor.submit(new PokeNotificationSender(this.jda, DBManager.getNewPokemon(),testing));
        notifierLog.log(DEBUG,"Done checking and adding to queue for processing");
    }
}
