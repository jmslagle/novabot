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

    private static SimpleLog notifierLog = SimpleLog.getLog("PokeNotifier");

    public PokeNotifier(final JDA jda, boolean testing) {
        this.jda = jda;
        this.testing = testing;
    }

    @Override
    public void run() {
        notifierLog.log(DEBUG,"Total threads: " + ManagementFactory.getThreadMXBean().getThreadCount());
        notifierLog.log(INFO,"checking for pokemon to notify");
        final Thread thread = new Thread(new PokeNotificationSender(this.jda, DBManager.getNewPokemon(),testing));
        thread.start();
//        PokeNotificationSender sender = new PokeNotificationSender(this.jda, DBManager.getNewPokemon(),testing);
//        sender.run();
        notifierLog.log(DEBUG,"Done checking");
    }
}
