package notifier;

import core.NovaBot;
import core.ScheduledExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class NotificationsManager {

    ExecutorService pokeNotifSenderExecutor;

    ExecutorService raidNotifSenderExecutor;

    public NotificationsManager(NovaBot novaBot, boolean testing) {
        if (novaBot.config.useScanDb() && novaBot.config.pokemonEnabled()) {

            final ScheduledExecutor executor = new ScheduledExecutor(1);
            executor.scheduleAtFixedRate(new PokeNotifier(this, novaBot, testing), 0L, novaBot.config.getPokePollingDelay(), TimeUnit.SECONDS);

            pokeNotifSenderExecutor = Executors.newFixedThreadPool(novaBot.config.getPokemonThreads());
        }

        if (novaBot.config.useScanDb() && novaBot.config.raidsEnabled()) {

            ScheduledExecutor executorService = new ScheduledExecutor(1);
            executorService.scheduleAtFixedRate(new RaidNotifier(this, novaBot), 0, novaBot.config.getRaidPollingDelay(), TimeUnit.SECONDS);

            raidNotifSenderExecutor = Executors.newFixedThreadPool(novaBot.config.getRaidThreads());
        }
    }

}
