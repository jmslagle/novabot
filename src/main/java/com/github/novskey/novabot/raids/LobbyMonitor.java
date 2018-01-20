package com.github.novskey.novabot.raids;

import com.github.novskey.novabot.Util.UtilityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZonedDateTime;


/**
 * Created by miriam on 5/7/17.
 */
public class LobbyMonitor implements Runnable {

    private final LobbyManager lobbyManager;

    private static final Logger lobbyMonitorLog = LoggerFactory.getLogger("Lobby-Monitor");

    public LobbyMonitor(LobbyManager lobbyManager){
        this.lobbyManager = lobbyManager;
    }

    @Override
    public void run() {
        try {
            lobbyMonitorLog.info("Checking all active lobbies");

            if (lobbyManager.activeLobbies.size() == 0) {
                lobbyMonitorLog.info("No currently active lobbies");
            }

            for (RaidLobby lobby : lobbyManager.activeLobbies.values()) {
                lobbyMonitorLog.info(String.format("Commencing check for lobby %s", lobby.lobbyCode));

                if (lobby.shutDownService == null && lobby.spawn.raidEnd.isBefore(ZonedDateTime.now(UtilityFunctions.UTC))) {
                    lobbyMonitorLog.info(String.format("Lobby %s's raid has ended and is not already shutting down, ending raid.", lobby.lobbyCode));
                    lobby.end(15);
                    continue;
                }

                long timeLeft = Duration.between(ZonedDateTime.now(UtilityFunctions.UTC), lobby.spawn.raidEnd).toMillis();

                if (timeLeft < 0 && lobby.nextTimeLeftUpdate <= 0) {
                    continue;
                }

                double minutes = timeLeft / 1000 / 60;

                lobbyMonitorLog.debug(String.format("%s minutes left until lobby %s ends", minutes, lobby.lobbyCode));
                if (lobby.channelId != null && minutes <= lobby.nextTimeLeftUpdate) {
                    lobbyMonitorLog.info(String.format("Lobby %s is going to end in %s minutes or less. Alerting the lobby.",
                            lobby.lobbyCode,
                            lobby.nextTimeLeftUpdate));
                    lobby.nextTimeLeftUpdate -= 5;
                    lobby.alertRaidNearlyOver();
                }
            }

            lobbyMonitorLog.info("Done checking");
        }catch (Exception e){
            lobbyMonitorLog.error("An error occurred in lobby monitor",e);
        }
    }
}
