package notifier;

import core.DBManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.utils.SimpleLog;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.DEBUG;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

/**
 * Created by Owner on 27/06/2017.
 */
class RaidNotifier implements Runnable
{
    private final JDA jda;
    private final boolean testing;
    private final NotificationsManager manager;

    private static final SimpleLog notifierLog = SimpleLog.getLog("RaidNotifier");

    public RaidNotifier(NotificationsManager manager, final JDA jda, boolean testing) {
        this.manager = manager;
        this.jda = jda;
        this.testing = testing;
    }

    @Override
    public void run() {
        notifierLog.log(INFO,"checking for raids to notify");
        manager.raidNotifSenderExecutor.submit(new RaidNotificationSender(this.jda, DBManager.getCurrentRaids(),testing));
        notifierLog.log(DEBUG,"Done checking and adding to queue for processing");
    }
}
