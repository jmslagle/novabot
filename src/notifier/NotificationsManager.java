package notifier;

import core.Config;
import core.ScheduledExecutor;
import net.dv8tion.jda.core.JDA;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class NotificationsManager {

    ExecutorService pokeNotifSenderExecutor;

    ExecutorService raidNotifSenderExecutor;

    public NotificationsManager (Config config, JDA jda, boolean testing){
        if(config.useRmDb() && config.pokemonEnabled()) {

            final ScheduledExecutor executor = new ScheduledExecutor(1);
            executor.scheduleAtFixedRate(new PokeNotifier(this,jda, testing), 0L, config.getPokePollingRate(), TimeUnit.SECONDS);

            pokeNotifSenderExecutor = Executors.newFixedThreadPool(config.getPokemonThreads());
        }

        if(config.useRmDb() && config.raidsEnabled()){

            ScheduledExecutor executorService = new ScheduledExecutor(1);
            executorService.scheduleAtFixedRate(new RaidNotifier(this, jda, testing),0,config.getRaidPollingRate(), TimeUnit.SECONDS);

            raidNotifSenderExecutor = Executors.newFixedThreadPool(config.getRaidThreads());
        }
    }

}
