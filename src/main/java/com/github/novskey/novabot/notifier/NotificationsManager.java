package com.github.novskey.novabot.notifier;

import com.github.novskey.novabot.core.NovaBot;
import com.github.novskey.novabot.core.ScheduledExecutor;
import com.github.novskey.novabot.pokemon.PokeSpawn;
import com.github.novskey.novabot.raids.RaidSpawn;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class NotificationsManager {

    private final NovaBot novaBot;
    private final boolean testing;
    public final LinkedBlockingQueue<PokeSpawn> pokeQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<RaidSpawn> raidQueue = new LinkedBlockingQueue<>();

    public NotificationsManager(NovaBot novaBot, boolean testing) {
        this.novaBot = novaBot;
        this.testing = testing;
    }

    public void start(){
        final ScheduledExecutor executor = new ScheduledExecutor(novaBot.config.getDbThreads());

        if (novaBot.config.useScanDb() && novaBot.config.pokemonEnabled()) {

            executor.scheduleAtFixedRate(new PokeNotifier(this, novaBot, testing), 0L, novaBot.config.getPokePollingDelay(), TimeUnit.SECONDS);

            for (int i = 1; i <= novaBot.config.getPokemonThreads(); i++) {
                new Thread(new PokeNotificationSender(novaBot,i)).start();
            }
        }

        if (novaBot.config.useScanDb() && novaBot.config.raidsEnabled()) {

            executor.scheduleAtFixedRate(new RaidNotifier(this, novaBot), 0, novaBot.config.getRaidPollingDelay(), TimeUnit.SECONDS);

            if (novaBot.config.isRaidOrganisationEnabled()) {
                novaBot.lobbyManager.addLobbies(novaBot.dataManager.getActiveLobbies());
            }
            for (int i = 1; i <= novaBot.config.getRaidThreads(); i++) {
                new Thread(new RaidNotificationSender(novaBot,i)).start();
            }
        }
    }

}
