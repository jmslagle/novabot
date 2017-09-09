package raids;

import core.DBManager;
import core.RotatingSet;
import maps.GeofenceIdentifier;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

/**
 * Created by Owner on 2/07/2017.
 */
public class LobbyManager {

    HashMap<String, RaidLobby> activeLobbies = new HashMap<>();
    private static SimpleLog lobbyManagerLog = SimpleLog.getLog("Lobby-Manager");

    RotatingSet<String> oldLobbyRoleIds = new RotatingSet<>(200);

    public LobbyManager(){

    }

    public RaidLobby getLobby(String lobbyCode) {
        return activeLobbies.get(lobbyCode);
    }

    public void newRaid(String lobbyCode, RaidSpawn raidSpawn){
        activeLobbies.put(lobbyCode,new RaidLobby(raidSpawn,lobbyCode));
    }

    public boolean isLobbyChannel(String id) {
        for (RaidLobby raidLobby : activeLobbies.values()) {
            if(raidLobby.channelId == null) continue;
            if(raidLobby.channelId.equals(id))
                return true;
        }
        return false;
    }

    public RaidLobby getLobbyByChannelId(String id) {
        for (RaidLobby raidLobby : activeLobbies.values()) {
            if(raidLobby.channelId == null) continue;
            if(raidLobby.channelId.equals(id))
                return raidLobby;
        }
        return null;
    }

    public void endLobby(String lobbyCode) {
        RaidLobby lobby = getLobby(lobbyCode);

        if(lobby != null) {
            getLobby(lobbyCode).end(15);
        }
    }

    public RaidLobby getLobbyByGymId(String gymId) {
        for (Map.Entry<String, RaidLobby> raidLobbyEntry : activeLobbies.entrySet()) {
            if(raidLobbyEntry.getValue().spawn.gymId.equals(gymId)){
                return raidLobbyEntry.getValue();
            }
        }
        return null;
    }

    public boolean isLobbyRoleId(String id) {
        for (RaidLobby lobby : activeLobbies.values()) {
            if(lobby.roleId == null) continue;
            if(lobby.roleId.equals(id)) return true;
        }

        return oldLobbyRoleIds.contains(id);
    }

    public ArrayList<RaidLobby> getLobbiesByGeofence(GeofenceIdentifier geofence) {
        ArrayList<RaidLobby> lobbies = new ArrayList<>();

        for (RaidLobby lobby : activeLobbies.values()) {
            if(lobby.spawn.getGeofences().contains(geofence)){
                lobbies.add(lobby);
            }
        }

        return lobbies;
    }

    public void getLobbiesFromDb(HashMap<String,String> activeLobbyCodes) {

        lobbyManagerLog.log(INFO,"Loading active lobbies based on lobby codes found in the DB");

        for (Map.Entry<String, String> entry : activeLobbyCodes.entrySet()) {
            String lobbyCode = entry.getKey();
            String gymId = entry.getValue();

            RaidSpawn raidSpawn = DBManager.knownRaids.get(gymId);

            if(raidSpawn != null){
                lobbyManagerLog.log(INFO,"Found an active raid/egg for gymId: %s, previous lobby code %s. Restoring the lobby");
                raidSpawn.setLobbyCode(lobbyCode);
            }
        }

    }

    public void addLobbies(ArrayList<RaidLobby> lobbies) {
        for (RaidLobby activeLobby : lobbies) {
            if(activeLobby.inviteCode == null){
                activeLobby.createInvite();
            }
            addLobby(activeLobby);
        }
    }

    public void addLobby(RaidLobby raidLobby) {
        activeLobbies.put(raidLobby.lobbyCode,raidLobby);
    }
}
