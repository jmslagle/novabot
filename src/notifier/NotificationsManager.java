package notifier;

import core.Config;
import core.ScheduledExecutor;
import net.dv8tion.jda.core.JDA;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class NotificationsManager {

    LinkedBlockingQueue<Runnable> pokemonQueue;

    LinkedBlockingQueue<Runnable> raidQueue;

    ExecutorService pokeNotifSenderExecutor;

    ExecutorService raidNotifSenderExecutor;

    private Config config;

    public NotificationsManager (Config config, JDA jda, boolean testing){
        this.config = config;
        if(config.useRmDb() && config.pokemonEnabled()) {
            pokemonQueue = new LinkedBlockingQueue<>();

            final ScheduledExecutor executor = new ScheduledExecutor(1);
            executor.scheduleAtFixedRate(new PokeNotifier(this,jda, testing), 0L, config.getPokePollingRate(), TimeUnit.SECONDS);

            pokeNotifSenderExecutor = Executors.newFixedThreadPool(config.getPokemonThreads());
        }

        if(config.useRmDb() && config.raidsEnabled()){
            raidQueue = new LinkedBlockingQueue<>();

            ScheduledExecutor executorService = new ScheduledExecutor(1);
            executorService.scheduleAtFixedRate(new RaidNotifier(this, jda, testing),0,config.getRaidPollingRate(), TimeUnit.SECONDS);

            raidNotifSenderExecutor = Executors.newFixedThreadPool(config.getRaidThreads());
        }
    }

}
