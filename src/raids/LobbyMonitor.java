package raids;

import core.Util;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import static core.MessageListener.config;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.DEBUG;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;


/**
 * Created by miriam on 5/7/17.
 */
public class LobbyMonitor implements Runnable {

    private final LobbyManager lobbyManager;

    private static final SimpleLog lobbyMonitorLog = SimpleLog.getLog("Lobby-Monitor");

    public LobbyMonitor(LobbyManager lobbyManager){
        this.lobbyManager = lobbyManager;
        lobbyMonitorLog.setLevel(DEBUG);
        try {
            SimpleLog.addFileLogs(new File("std.log"),new File("err.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        lobbyMonitorLog.log(INFO,"Checking all active lobbies");

        if(lobbyManager.activeLobbies.size() == 0){
            lobbyMonitorLog.log(INFO,"No currently active lobbies");
        }

        for (RaidLobby lobby : lobbyManager.activeLobbies.values()) {
            lobbyMonitorLog.log(INFO, String.format("Commencing check for lobby %s", lobby.lobbyCode));

            if(lobby.shutDownService == null && lobby.spawn.raidEnd.before(Util.getCurrentTime(config.getTimeZone()))){
                lobbyMonitorLog.log(INFO,String.format("Lobby %s's raid has ended and is not already shutting down, ending raid.",lobby.lobbyCode));
                lobby.end(15);
                continue;
            }

            double timeLeft = lobby.spawn.raidEnd.getTime() - Util.getCurrentTime(config.getTimeZone()).getTime();

            if(timeLeft < 0 && lobby.nextTimeLeftUpdate <= 0){
                continue;
            }

            double minutes = timeLeft / 1000 / 60;

            lobbyMonitorLog.log(DEBUG,String.format("%s minutes left until lobby %s ends",minutes,lobby.lobbyCode));
            if(lobby.channelId != null && minutes <= lobby.nextTimeLeftUpdate){
                lobbyMonitorLog.log(INFO,String.format("Lobby %s is going to end in %s minutes or less. Alerting the lobby.",
                        lobby.lobbyCode,
                        lobby.nextTimeLeftUpdate));
                lobby.nextTimeLeftUpdate -= 5;
                lobby.alertRaidNearlyOver();
            }
        }

        lobbyMonitorLog.log(INFO,"Done checking");
    }

    public static void main(String[] args) {

        Timestamp time1 = new Timestamp(Util.getCurrentTime(config.getTimeZone()).getTime() + 504000);
        Timestamp time2 = new Timestamp(Util.getCurrentTime(config.getTimeZone()).getTime() + 6000000);

        System.out.println(time1);
        System.out.println(time2);

        long timeLeft = time2.getTime() - time1.getTime();

        System.out.println(timeLeft);

        long minutes = MILLISECONDS.toMinutes(timeLeft);

        System.out.println(minutes);


    }
}
