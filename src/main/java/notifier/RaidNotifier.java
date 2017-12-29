package notifier;

import core.NovaBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Owner on 27/06/2017.
 */
class RaidNotifier implements Runnable
{
    private static final Logger notifierLog = LoggerFactory.getLogger("RaidNotifier");
    private final NotificationsManager manager;
    private final NovaBot novaBot;

    public RaidNotifier(NotificationsManager manager, NovaBot novaBot) {
        this.manager = manager;
        this.novaBot = novaBot;
    }

    @Override
    public void run() {
        try {
            notifierLog.info("checking for raids to notify");
            manager.raidNotifSenderExecutor.submit(new RaidNotificationSender(novaBot, novaBot.dbManager.getCurrentRaids()));
            notifierLog.debug("Done checking and adding to queue for processing");
        } catch (Exception e){
            notifierLog.error("An error ocurred in RaidNotifier",e);
        }
    }
}
