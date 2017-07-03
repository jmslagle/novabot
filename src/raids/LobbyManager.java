package raids;

import java.util.HashMap;

/**
 * Created by Owner on 2/07/2017.
 */
public class LobbyManager {

    HashMap<String, RaidLobby> activeLobbies = new HashMap<>();

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
        getLobby(lobbyCode).end();
    }
}
