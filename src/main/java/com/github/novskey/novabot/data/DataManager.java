package com.github.novskey.novabot.data;

import com.github.novskey.novabot.Util.UtilityFunctions;
import com.github.novskey.novabot.core.*;
import com.github.novskey.novabot.maps.GeocodedLocation;
import com.github.novskey.novabot.pokemon.PokeSpawn;
import com.github.novskey.novabot.pokemon.Pokemon;
import com.github.novskey.novabot.raids.Raid;
import com.github.novskey.novabot.raids.RaidLobby;
import com.github.novskey.novabot.raids.RaidSpawn;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Created by Paris on 18/01/2018.
 */
public class DataManager implements IDataBase {

    private final NovaBot novaBot;
    public final DBManager dbManager;
    private final DBCache dbCache;

    public static void main(String[] args) {
        NovaBot novaBot = new NovaBot();
        novaBot.setup();

        System.out.println(novaBot.dataManager.getUserIDsToNotify(new PokeSpawn(
                1,
                -35.265134, 149.122796,
                ZonedDateTime.ofInstant(Instant.now().plusSeconds(60), UtilityFunctions.UTC),
                15,
                15,
                15,
                18,
                22,
                0,
                0,
                0,
                0,
                200,
                .1)));
    }

    public DataManager (NovaBot novaBot){
        this.novaBot = novaBot;

        dbManager = new DBManager(novaBot);

        dbManager.scanDbConnect();
        dbManager.novabotdbConnect();

        dbCache = new DBCache(novaBot);
        dbCache.users = dbManager.dumpUsers();
        dbCache.pokemons = dbManager.dumpPokemon();
        dbCache.raids = dbManager.dumpRaids();
        dbCache.presets = dbManager.dumpPresets();
        dbCache.raidLobbies = dbManager.dumpRaidLobbies();
        dbCache.spawnInfo = dbManager.dumpSpawnInfo();
    }

    @Override
    public void addPokemon(String userID, Pokemon pokemon) {
        dbCache.addPokemon(userID,pokemon);
        dbManager.addPokemon(userID,pokemon);
    }

    @Override
    public void addPreset(String userID, String preset, Location location) {
        dbCache.addPreset(userID,preset,location);
        dbManager.addPreset(userID, preset, location);
    }

    @Override
    public void addRaid(String userID, Raid raid) {
        dbCache.addRaid(userID,raid);
        dbManager.addRaid(userID,raid);
    }

    @Override
    public void addUser(String userID) {
        dbCache.addUser(userID);
        dbManager.addUser(userID);
    }

    @Override
    public void clearPreset(String id, String[] presets) {
        dbCache.clearPreset(id, presets);
        dbManager.clearPreset(id, presets);
    }

    @Override
    public void clearLocationsPresets(String id, Location[] locations) {
        dbCache.clearLocationsPresets(id, locations);
        dbManager.clearLocationsPresets(id, locations);
    }

    @Override
    public void clearLocationsPokemon(String id, Location[] locations) {
        dbCache.clearLocationsPokemon(id, locations);
        dbManager.clearLocationsPokemon(id, locations);
    }

    @Override
    public void clearLocationsRaids(String id, Location[] locations) {
        dbCache.clearLocationsRaids(id, locations);
        dbManager.clearLocationsRaids(id, locations);
    }

    @Override
    public void clearPokemon(String id, ArrayList<Pokemon> pokemons) {
        dbCache.clearPokemon(id, pokemons);
        dbManager.clearPokemon(id, pokemons);
    }

    @Override
    public void clearRaid(String id, ArrayList<Raid> raids) {
        dbCache.clearRaid(id, raids);
        dbManager.clearRaid(id, raids);
    }

    @Override
    public int countPokemon(String id, boolean countLocations) {
        return dbCache.countPokemon(id, countLocations);
    }

    @Override
    public int countPresets(String userID, boolean countLocations) {
        return dbCache.countPresets(userID, countLocations);
    }

    @Override
    public int countRaids(String id, boolean countLocations) {
        return dbCache.countRaids(id, countLocations);
    }

    @Override
    public void deletePokemon(String userID, Pokemon pokemon) {
        dbCache.deletePokemon(userID,pokemon);
        dbManager.deletePokemon(userID,pokemon);
    }

    @Override
    public void deletePreset(String userId, String preset, Location location) {
        dbCache.deletePreset(userId, preset, location);
        dbManager.deletePreset(userId, preset, location);
    }

    @Override
    public void deleteRaid(String userID, Raid raid) {
        dbCache.deleteRaid(userID, raid);
        dbManager.deleteRaid(userID, raid);
    }

    @Override
    public void endLobby(String lobbyCode) {
        dbCache.endLobby(lobbyCode);
        dbManager.endLobby(lobbyCode);
    }

    @Override
    public GeocodedLocation getGeocodedLocation(double lat, double lon) {
        return dbCache.getGeocodedLocation(lat, lon);
    }

    @Override
    public ArrayList<String> getUserIDsToNotify(RaidSpawn raidSpawn) {
        return dbCache.getUserIDsToNotify(raidSpawn);
    }

    @Override
    public ArrayList<String> getUserIDsToNotify(String preset, Spawn spawn) {
        return dbCache.getUserIDsToNotify(preset, spawn);
    }

    @Override
    public ArrayList<String> getUserIDsToNotify(PokeSpawn pokeSpawn) {
        return dbCache.getUserIDsToNotify(pokeSpawn);
    }

    @Override
    public UserPref getUserPref(String id) {
        return dbCache.getUserPref(id);
    }

    @Override
    public int highestRaidLobbyId() {
        return dbCache.highestRaidLobbyId();
    }

    @Override
    public void logNewUser(String userID) {
        dbCache.logNewUser(userID);
        dbManager.logNewUser(userID);
    }

    @Override
    public void newLobby(String lobbyCode, String gymId, int memberCount, String channelId, String roleId, long nextTimeLeftUpdate, String inviteCode) {
        dbCache.newLobby(lobbyCode, gymId, memberCount, channelId, roleId, nextTimeLeftUpdate, inviteCode);
        dbManager.newLobby(lobbyCode, gymId, memberCount, channelId, roleId, nextTimeLeftUpdate, inviteCode);
    }

    @Override
    public boolean notContainsUser(String userID) {
        return dbCache.notContainsUser(userID);
    }

    @Override
    public void pauseUser(String id) {
        dbCache.pauseUser(id);
        dbManager.pauseUser(id);
    }

    @Override
    public void resetPokemon(String id) {
        dbCache.resetPokemon(id);
        dbManager.resetPokemon(id);
    }

    @Override
    public void resetPresets(String id) {
        dbCache.resetPresets(id);
        dbManager.resetPresets(id);
    }

    @Override
    public void resetRaids(String id) {
        dbCache.resetRaids(id);
        dbManager.resetRaids(id);
    }

    @Override
    public void resetUser(String id) {
        dbCache.resetUser(id);
        dbManager.resetUser(id);
    }

    @Override
    public void setGeocodedLocation(double lat, double lon, GeocodedLocation location) {
        dbCache.setGeocodedLocation(lat, lon, location);
        dbManager.setGeocodedLocation(lat, lon, location);
    }

    @Override
    public void unPauseUser(String id) {
        dbCache.unPauseUser(id);
        dbManager.unPauseUser(id);
    }

    @Override
    public void updateLobby(String lobbyCode, int memberCount, int nextTimeLeftUpdate, String inviteCode) {
        dbCache.updateLobby(lobbyCode, memberCount, nextTimeLeftUpdate, inviteCode);
        dbManager.updateLobby(lobbyCode, memberCount, nextTimeLeftUpdate, inviteCode);
    }

    @Override
    public int purgeUnknownSpawnpoints() {
        return dbCache.purgeUnknownSpawnpoints();
    }

    @Override
    public ZoneId getZoneId(double lat, double lon) {
        return dbCache.getZoneId(lat, lon);
    }

    @Override
    public void setZoneId(double lat, double lon, ZoneId zoneId) {
        dbCache.setZoneId(lat, lon, zoneId);
        dbManager.setZoneId(lat, lon, zoneId);
    }

    public int countSpawns(int id, TimeUnit timeUnit, int intervalLength) {
        return dbManager.countSpawns(id,timeUnit,intervalLength);
    }

    public ArrayList<RaidLobby> getActiveLobbies() {
        return dbManager.getActiveLobbies();
    }

    public void getNewPokemon() {
        dbManager.getNewPokemon();
    }

    public void getCurrentRaids() {
        dbManager.getCurrentRaids();
    }
}
