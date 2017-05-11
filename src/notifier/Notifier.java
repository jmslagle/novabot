package notifier;

import net.dv8tion.jda.core.*;
import core.*;

public class Notifier implements Runnable
{
    private final JDA jda;

    public Notifier(final JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        System.out.println("checking for pokemon to notify");
        final Thread thread = new Thread(new NotificationSender(this.jda, DBManager.getNewPokemon()));
        thread.start();
        System.out.println("Done checking");
    }
}
