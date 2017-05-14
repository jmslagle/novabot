package notifier;

import net.dv8tion.jda.core.*;
import core.*;

public class Notifier implements Runnable
{
    private final JDA jda;
    private final boolean testing   ;

    public Notifier(final JDA jda, boolean testing) {
        this.jda = jda;
        this.testing = testing;
    }

    @Override
    public void run() {
        System.out.println("checking for pokemon to notify");
        final Thread thread = new Thread(new NotificationSender(this.jda, DBManager.getNewPokemon(),testing));
        thread.start();
        System.out.println("Done checking");
    }
}
