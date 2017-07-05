package raids;

import core.DBManager;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.Time;
import java.sql.Timestamp;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.DEBUG;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;


/**
 * Created by miriam on 5/7/17.
 */
public class LobbyMonitor implements Runnable {

    private final LobbyManager lobbyManager;

    static SimpleLog lobbyMonitorLog = SimpleLog.getLog("Lobby-Monitor");

    public LobbyMonitor(LobbyManager lobbyManager){
        this.lobbyManager = lobbyManager;
        lobbyMonitorLog.setLevel(DEBUG);
    }

    @Override
    public void run() {
        lobbyMonitorLog.log(INFO,"Checking all active lobbies");

        if(lobbyManager.activeLobbies.size() == 0){
            lobbyMonitorLog.log(INFO,"No currently active lobbies");
        }

        for (RaidLobby lobby : lobbyManager.activeLobbies.values()) {
            lobbyMonitorLog.log(INFO, String.format("Commencing check for lobby %s", lobby.lobbyCode));

            if(lobby.shutDownService == null && lobby.spawn.raidEnd.before(DBManager.getCurrentTime())){
                lobbyMonitorLog.log(INFO,String.format("Lobby %s's raid has ended and is not already shutting down, ending raid."));
                lobby.end(15);
                continue;
            }

            long timeLeft = lobby.spawn.raidEnd.getTime() - DBManager.getCurrentTime().getTime();

            long minutes = MILLISECONDS.toMinutes(timeLeft);

            lobbyMonitorLog.log(DEBUG,String.format("%s minutes left until lobby %s ends",minutes,lobby.lobbyCode));
            if(lobby.channelId != null && minutes <= lobby.nextTimeLeftUpdate){
                lobbyMonitorLog.log(INFO,String.format("Lobby %s is going to end in %s minutes or less. Alerting the lobby.",
                        lobby.lobbyCode,
                        lobby.nextTimeLeftUpdate));
                lobby.alertRaidNearlyOver();

                while(minutes < lobby.nextTimeLeftUpdate){
                    lobby.nextTimeLeftUpdate -= 5;
                }
            }
        }

        lobbyMonitorLog.log(INFO,"Done checking");
    }

    public static void main(String[] args) {

        Timestamp time1 = new Timestamp(DBManager.getCurrentTime().getTime() + 504000);
        Timestamp time2 = new Timestamp(DBManager.getCurrentTime().getTime() + 6000000);

        System.out.println(time1);
        System.out.println(time2);

        long timeLeft = time2.getTime() - time1.getTime();

        System.out.println(timeLeft);

        long minutes = MILLISECONDS.toMinutes(timeLeft);

        System.out.println(minutes);


    }
}
