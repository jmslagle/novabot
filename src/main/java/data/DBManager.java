package data;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import core.*;
import Util.UtilityFunctions;
import maps.GeocodedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokemon.PokeSpawn;
import pokemon.Pokemon;
import raids.Raid;
import raids.RaidLobby;
import raids.RaidSpawn;

import java.sql.*;
import java.sql.Types;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Date;

public class DBManager {
    private final Logger dbLog = LoggerFactory.getLogger("DB");
    private ZonedDateTime lastChecked;

    private final RotatingSet<Integer> hashCodes = new RotatingSet<>(2000);

    Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(UtilityFunctions.UTC));

    public final HashMap<String, RaidSpawn> knownRaids = new HashMap<>();
    private ZonedDateTime lastCheckedRaids;
    private StringBuilder blacklistQMarks = null;
    private final NovaBot novaBot;
    private java.lang.String scanUrl;
    private String nbUrl;

    public DBManager(NovaBot novaBot) {
        this.novaBot = novaBot;
        lastChecked = ZonedDateTime.now(UtilityFunctions.UTC);
        lastCheckedRaids = ZonedDateTime.now(UtilityFunctions.UTC);
    }

    public void addPokemon(final String userID, final Pokemon pokemon) {

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                                    "INSERT INTO pokemon (user_id, id, max_iv, min_iv, location) " +
                                    "VALUES (?,?,?,?,?)")){
            statement.setString(1, userID);
            statement.setInt(2, pokemon.getID());
            statement.setDouble(3, pokemon.maxiv);
            statement.setDouble(4, pokemon.miniv);
            statement.setString(5, pokemon.getLocation().toDbString());

            dbLog.info(statement.toString());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            dbLog.warn(e.getMessage());
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }


    public void addPreset(final String userID, String preset, Location location) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("" +
                                                                       "INSERT INTO preset (user_id, preset_name, location) " +
                                                                       "VALUES (?,?,?)")) {
            statement.setString(1, userID);
            statement.setString(2, preset);
            statement.setString(3, location.toDbString());

            dbLog.info(statement.toString());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            dbLog.warn(e.getMessage());
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public void addRaid(final String userID, final Raid raid) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO raid VALUES (?,?,?)")) {
            statement.setString(1, userID);
            statement.setDouble(2, raid.bossId);
            statement.setString(3, raid.location.toDbString());

            dbLog.debug(statement.toString());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            dbLog.warn(e.getMessage());
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public void addUser(final String userID) {
        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("INSERT INTO users (id) VALUES (%s);", "'" + userID + "'"));
        } catch (SQLIntegrityConstraintViolationException e3) {
            dbLog.warn("Cannot add duplicate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void clearPreset(String id, String[] presets) {
        StringBuilder namesString = new StringBuilder("(");
        for (int i = 0; i < presets.length; ++i) {
            if (i == presets.length - 1) {
                namesString.append("'" + presets[i] + "'");
            } else {
                namesString.append("'" + presets[i] + "'").append(",");
            }
        }
        namesString.append(")");

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM preset WHERE user_id=%s AND preset_name IN %s;", "'" + id + "'", namesString.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearLocationsPresets(String id, Location[] locations) {
        StringBuilder locationsString = new StringBuilder("(");
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                locationsString.append("'").append(locations[i].toString().replace("'", "\\'")).append("'");
            } else {
                locationsString.append("'").append(locations[i].toString().replace("'", "\\'")).append("',");
            }
        }
        locationsString.append(")");

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM preset WHERE user_id=%s AND location IN %s;", "'" + id + "'", locationsString.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearLocationsPokemon(final String id, final Location[] locations) {
        StringBuilder locationsString = new StringBuilder("(");
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                locationsString.append("'").append(locations[i].toString().replace("'", "\\'")).append("'");
            } else {
                locationsString.append("'").append(locations[i].toString().replace("'", "\\'")).append("',");
            }
        }
        locationsString.append(")");

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND location IN %s;", "'" + id + "'", locationsString.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearLocationsRaids(String id, Location[] locations) {
        StringBuilder locationsString = new StringBuilder("(");
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                locationsString.append("'").append(locations[i].toString().replace("'", "\\'")).append("'");
            } else {
                locationsString.append("'").append(locations[i].toString().replace("'", "\\'")).append("',");
            }
        }
        locationsString.append(")");

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM raid WHERE user_id=%s AND location IN %s;", "'" + id + "'", locationsString.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearPokemon(final String id, final ArrayList<Pokemon> pokemons) {
        StringBuilder idsString = new StringBuilder("(");
        for (int i = 0; i < pokemons.size(); ++i) {
            if (i == pokemons.size() - 1) {
                idsString.append(pokemons.get(i).getID());
            } else {
                idsString.append(pokemons.get(i).getID()).append(",");
            }
        }
        idsString.append(")");

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND id IN %s;", "'" + id + "'", idsString.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearRaid(final String id, final ArrayList<Raid> raids) {
        StringBuilder idsString = new StringBuilder("(");
        for (int i = 0; i < raids.size(); ++i) {
            if (i == raids.size() - 1) {
                idsString.append(raids.get(i).bossId);
            } else {
                idsString.append(raids.get(i).bossId).append(",");
            }
        }
        idsString.append(")");

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM raid WHERE user_id=%s AND boss_id IN %s;", "'" + id + "'", idsString.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countPokemon(final String id, boolean countLocations) {
        int pokemon = 0;

        String sql;

        if (countLocations) {
            sql = "SELECT count(id) FROM pokemon WHERE user_id=?";
        } else {
            sql = "SELECT count(distinct(id)) FROM pokemon WHERE user_id=?";
        }

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                pokemon = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pokemon;
    }

//    public boolean shouldNotify(final String userID, final PokeSpawn pokeSpawn) {
//
//        try (Connection connection = getNbConnection();
//             Statement statement = connection.createStatement()) {
//            statement.executeQuery(String.format("SELECT * FROM pokemon WHERE ((user_id=%s) AND ((location=%s) OR (location='All')) AND (id=%s) AND (min_iv <= %s) AND (max_iv >= %s));", "'" + userID + "'", "'" + pokeSpawn.region + "'", pokeSpawn.id, pokeSpawn.iv, pokeSpawn.iv));
//            final ResultSet rs = statement.getResultSet();
//            if (!rs.next()) {
//                connection.close();
//                statement.close();
//                return false;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }

    public int countPresets(String userID, boolean countLocations) {
        int presets = 0;

        String sql;

        if (countLocations) {
            sql = "SELECT count(preset_name) FROM preset WHERE user_id=?";
        } else {
            sql = "SELECT count(distinct(preset_name)) FROM preset WHERE user_id=?";
        }

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, userID);

            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                presets = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presets;
    }

    public int countRaids(final String id, boolean countLocations) {
        int raids = 0;

        String sql;

        if (countLocations) {
            sql = "SELECT count(boss_id) FROM raid WHERE user_id=?";
        } else {
            sql = "SELECT count(distinct(boss_id)) FROM raid WHERE user_id=?";
        }

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                raids = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return raids;
    }

    public int countSpawns(int id, TimeUnit intervalType, int intervalLength) {
        int numSpawns = 0;

        String sql = null;
        switch(novaBot.config.getScannerType()){
            case RocketMap:
            case PhilMap:
                sql = "  SELECT COUNT(*) " +
                        "FROM pokemon " +
                        "WHERE pokemon_id = ? AND disappear_time > (UTC_TIMESTAMP() - INTERVAL ? " + intervalType.toDbString() + ")";
                break;
            case Hydro74000Monocle:
            case Monocle:
                sql = "  SELECT COUNT(*) " +
                        "FROM sightings " +
                        "WHERE pokemon_id = ? AND expire_timestamp > UNIX_TIMESTAMP(UTC_TIMESTAMP() - INTERVAL ? " + intervalType.toDbString() + ")";
                break;
        }

        try (Connection connection = getScanConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.setDouble(2, intervalLength);

            dbLog.debug(statement.toString());
            statement.executeQuery();

            ResultSet rs = statement.getResultSet();

            if (rs.next()) {
                numSpawns = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numSpawns;
    }

    public void deletePokemon(final String userID, final Pokemon pokemon) {

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("" +
                                                                       "DELETE FROM pokemon " +
                                                                       "WHERE ((user_id=?) " +
                                                                       "AND (LOWER(location)=LOWER(?)) " +
                                                                       "AND (id=?) " +
                                                                       "AND (min_iv=?) " +
                                                                       "AND (max_iv=?))")) {
            statement.setString(1, userID);
            statement.setString(2, (pokemon.getLocation().toString() == null) ? "All" : pokemon.getLocation().toString());
            statement.setDouble(3, pokemon.getID());
            statement.setDouble(4, pokemon.miniv);
            statement.setDouble(5, pokemon.maxiv);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePreset(String userId, String preset, Location location) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("" +
                                                                       "DELETE FROM preset " +
                                                                       "WHERE ((user_id=?) " +
                                                                       "AND (LOWER(location)=LOWER(?)) " +
                                                                       "AND (preset_name=?))")) {
            statement.setString(1, userId);
            statement.setString(2, location.toDbString());
            statement.setString(3, preset);

            dbLog.info(statement.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRaid(final String userID, final Raid raid) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("" +
                                                                       "DELETE FROM raid " +
                                                                       "WHERE ((user_id=?) " +
                                                                       "AND (boss_id=?) " +
                                                                       "AND (LOWER(location)=LOWER(?)))")
        ) {
            statement.setString(1, userID);
            statement.setDouble(2, raid.bossId);
            statement.setString(3, raid.location.toDbString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void endLobby(String lobbyCode) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM raidlobby WHERE lobby_id = ?")
        ) {
            statement.setInt(1, Integer.parseInt(lobbyCode));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RaidLobby> getActiveLobbies() {
        ArrayList<RaidLobby> activeLobbies = new ArrayList<>();

        ArrayList<String> toDelete = null;


        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT lobby_id,gym_id,channel_id,role_id,next_timeleft_update,invite_code FROM raidlobby;")
        ) {

            ResultSet rs = statement.executeQuery();


            while (rs.next()) {
                String lobbyCode          = String.format("%04d", rs.getInt(1));
                String gymId              = rs.getString(2);
                String channelId          = rs.getString(3);
                String roleId             = rs.getString(4);
                int    nextTimeLeftUpdate = rs.getInt(5);
                String inviteCode         = rs.getString(6);

                dbLog.info(String.format("Found lobby with info %s,%s,%s,%s in the db, checking for known raid", lobbyCode, gymId, channelId, roleId));

                RaidSpawn spawn = knownRaids.get(gymId);

                if (spawn == null) {
                    dbLog.warn(String.format("Couldn't find a known raid for gym id %s which was found as an active raid lobby, queuing for deletion", gymId));
                    if (toDelete == null) {
                        toDelete = new ArrayList<>();
                    }

                    toDelete.add(lobbyCode);
                } else {
                    dbLog.info(String.format("Found a raid for gym id %s, lobby code %s", gymId, lobbyCode));
                    RaidLobby lobby = new RaidLobby(spawn, lobbyCode, novaBot, channelId, roleId, inviteCode);
                    lobby.nextTimeLeftUpdate = nextTimeLeftUpdate;
                    lobby.loadMembers();

                    activeLobbies.add(lobby);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (toDelete != null) {
            dbLog.info("Deleting non-existent lobbies");
            endLobbies(toDelete);
        }

        return activeLobbies;
    }

    public ArrayList<RaidSpawn> getCurrentRaids() {
        dbLog.info("Checking which known gyms need to be updated");

        checkKnownRaids();

        dbLog.info("Done");
//        dbLog.log(DEBUG,knownRaids);

        dbLog.info("Getting new raid eggs");

        ArrayList<RaidSpawn> raidEggs = new ArrayList<>();

        StringBuilder knownIdQMarks = new StringBuilder();

        if (knownRaids.size() > 0) {
            if (novaBot.config.getScannerType() == ScannerType.PhilMap || novaBot.config.getScannerType() == ScannerType.RocketMap) {
                knownIdQMarks.append("gym.gym_id NOT IN (");
            }else{
                knownIdQMarks.append("forts.id NOT in (");
            }
            for (int i = 0; i < knownRaids.size(); ++i) {
                knownIdQMarks.append("?");
                if (i != knownRaids.size() - 1) {
                    knownIdQMarks.append(",");
                }
            }
            knownIdQMarks.append(") AND");
        }

        ArrayList<String> knownIds = new ArrayList<>();

        knownIds.addAll(knownRaids.keySet());

        if (knownRaids.containsKey(null)) {
            System.out.println("NULL, OH NO KNOWNRAIDS CONTAINS NULL? WHY???");
            System.out.println(knownRaids.get(null));
        }

        String sql = null;

        switch (novaBot.config.getScannerType()){
            case RocketMap:
                sql =   "SELECT" +
                        "  `gymdetails`.name," +
                        "  `gymdetails`.gym_id," +
                        "  `gym`.latitude," +
                        "  `gym`.longitude," +
                        "  `gym`.team_id," +
                        "  `raid`.end AS end," +
                        "  `raid`.start AS battle," +
                        "  `raid`.pokemon_id," +
                        "  `raid`.cp, " +
                        "  `raid`.level, " +
                        "  `raid`.move_1, " +
                        "  `raid`.move_2 " +
                        "FROM `gym`" +
                        "  INNER JOIN gymdetails ON gym.gym_id = gymdetails.gym_id" +
                        "  INNER JOIN raid ON gym.gym_id = raid.gym_id " +
                        "WHERE " + knownIdQMarks + " `raid`.end > DATE_ADD(?,INTERVAL 1 MINUTE)";
                break;
            case Monocle:
                sql =   "SELECT" +
                        "  `forts`.id," +
                        "  `forts`.lat," +
                        "  `forts`.lon," +
                        "  `fort_sightings`.team," +
                        "  `raids`.time_end AS end," +
                        "  `raids`.time_battle AS battle," +
                        "  `raids`.pokemon_id," +
                        "  `raids`.level, " +
                        "  `raids`.move_1, " +
                        "  `raids`.move_2 " +
                        "FROM `raids`" +
                        "  INNER JOIN fort_sightings ON forts.id = fort_sightings.fort_id" +
                        "  INNER JOIN raids ON forts.id = raids.fort_id " +
                        "WHERE " + knownIdQMarks + " `raids`.time_end > UNIX_TIMESTAMP(DATE_ADD(?,INTERVAL 1 MINUTE))";
                break;
            case Hydro74000Monocle:
                sql =   "SELECT `forts`.name," +
                        " `forts`.id," +
                        " `forts`.lat," +
                        " `forts`.lon," +
                        " `fort_sightings`.team," +
                        " `raids`.time_end AS END," +
                        " `raids`.time_battle AS battle," +
                        " `raids`.pokemon_id," +
                        " `raids`.cp," +
                        " `raids`.level," +
                        " `raids`.move_1," +
                        " `raids`.move_2 " +
                        "FROM `forts` " +
                        "INNER JOIN fort_sightings ON forts.id = fort_sightings.fort_id " +
                        "INNER JOIN `raids` ON forts.id = `raids`.fort_id " +
                        "WHERE " + knownIdQMarks + " `raids`.time_end > UNIX_TIMESTAMP(DATE_ADD(?, INTERVAL 1 MINUTE))";
                break;
            case PhilMap:
                sql = "SELECT" +
                        "  `gymdetails`.name," +
                        "  `gymdetails`.gym_id," +
                        "  `gym`.latitude," +
                        "  `gym`.longitude," +
                        "  `gym`.team_id," +
                        "  `raidinfo`.raid_end_ms AS end," +
                        "  `raidinfo`.raid_battle_ms AS battle," +
                        "  `raidinfo`.pokemon_id," +
                        "  `raidinfo`.cp, " +
                        "  `raidinfo`.raid_level, " +
                        "  `raidinfo`.move_1, " +
                        "  `raidinfo`.move_2 " +
                        "FROM `gym`" +
                        "  INNER JOIN gymdetails ON gym.gym_id = gymdetails.gym_id" +
                        "  INNER JOIN raidinfo ON gym.gym_id = raidinfo.gym_id " +
                        "WHERE " + knownIdQMarks + " raid_end_ms > DATE_ADD(?,INTERVAL 1 MINUTE)";
                break;
        }

        try (Connection connection = getScanConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < knownRaids.size(); i++) {
                statement.setString(i + 1, knownIds.get(i));
            }
            switch (novaBot.config.getScannerType()){
                case RocketMap:
                case PhilMap:
                    statement.setObject(knownRaids.size() + 1, lastCheckedRaids.toLocalDateTime(), Types.TIMESTAMP);
                    break;
                case Monocle:
                case Hydro74000Monocle:
                    statement.setObject(knownRaids.size() + 1, lastCheckedRaids.withZoneSameInstant(novaBot.config.getTimeZone()).toLocalDateTime(), Types.TIMESTAMP);
                    break;
            }

//            statement.setTimestamp(knownRaids.size() + 1, Timestamp.from(lastCheckedRaids.toInstant()), utcCalendar);


            dbLog.info("Executing query: " + statement);
            lastCheckedRaids = ZonedDateTime.now(UtilityFunctions.UTC);
//            dbLog.log(DEBUG,statement);
            final ResultSet rs = statement.executeQuery();
            dbLog.info("Query complete");

            while (rs.next()) {
                RaidSpawn raidSpawn = null;
                String gymId = null;

//                System.out.println("Current UTC time: " + ZonedDateTime.now(UtilityFunctions.UTC));
//                System.out.println("Current time based on config timezone : " + UtilityFunctions.getCurrentTime(novaBot.config.getTimeZone()));
                switch (novaBot.config.getScannerType()) {
                    case RocketMap:
                    case PhilMap:
                        String name = rs.getString(1);
                        gymId = rs.getString(2);
                        double lat = rs.getDouble(3);
                        double lon = rs.getDouble(4);
                        Team team = Team.fromId(rs.getInt(5));
                        ZonedDateTime raidEnd = ZonedDateTime.ofLocal(rs.getTimestamp(6).toLocalDateTime(), UtilityFunctions.UTC, null);
                        ZonedDateTime battleStart = ZonedDateTime.ofLocal(rs.getTimestamp(7).toLocalDateTime(), UtilityFunctions.UTC, null);
                        int bossId = rs.getInt(8);
                        int bossCp = rs.getInt(9);
                        int raidLevel = rs.getInt(10);
                        int move_1 = rs.getInt(11);
                        int move_2 = rs.getInt(12);

                        raidSpawn = new RaidSpawn(name, gymId, lat, lon, team, raidEnd, battleStart, bossId, bossCp, move_1, move_2, raidLevel);
                        break;
                    case Hydro74000Monocle:
                        name = rs.getString(1);
                        gymId = String.valueOf(rs.getInt(2));
                        lat = rs.getDouble(3);
                        lon = rs.getDouble(4);
                        team = Team.fromId(rs.getInt(5));
                        raidEnd = ZonedDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(6)), UtilityFunctions.UTC);
                        battleStart = ZonedDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(7)), UtilityFunctions.UTC);
                        bossId = rs.getInt(8);
                        bossCp = rs.getInt(9);
                        raidLevel = rs.getInt(10);
                        move_1 = rs.getInt(11);
                        move_2 = rs.getInt(12);

                        raidSpawn = new RaidSpawn(name, gymId, lat, lon, team, raidEnd, battleStart, bossId, bossCp, move_1, move_2, raidLevel);
                        break;
                    case Monocle:
                        gymId = String.valueOf(rs.getInt(1));
                        lat = rs.getDouble(2);
                        lon = rs.getDouble(3);
                        team = Team.fromId(rs.getInt(4));
                        raidEnd = ZonedDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(5)), UtilityFunctions.UTC);
                        battleStart = ZonedDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(6)), UtilityFunctions.UTC);
                        bossId = rs.getInt(7);
                        raidLevel = rs.getInt(8);
                        move_1 = rs.getInt(9);
                        move_2 = rs.getInt(10);

                        raidSpawn = new RaidSpawn("unkn", gymId, lat, lon, team, raidEnd, battleStart, bossId, Pokemon.getRaidBossCp(bossId,raidLevel), move_1, move_2, raidLevel);
                        break;
                }
                dbLog.debug(raidSpawn.toString());

                knownRaids.put(gymId, raidSpawn);

                raidEggs.add(raidSpawn);
            }
        } catch (SQLException e) {
            lastCheckedRaids = ZonedDateTime.now(UtilityFunctions.UTC);
            e.printStackTrace();
        }
        dbLog.info("Found: " + raidEggs.size());

        return raidEggs;
    }

    public GeocodedLocation getGeocodedLocation(final double lat, final double lon) {
        GeocodedLocation geocodedLocation = null;

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("" +
                                                                       "SELECT suburb,street_num,street,state,postal,neighbourhood,sublocality,country " +
                                                                       "FROM geocoding " +
                                                                       "WHERE lat = ? AND lon = ?")) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                geocodedLocation = new GeocodedLocation();

                String city = rs.getString(1);
                geocodedLocation.set("city", city);

                String streetNum = rs.getString(2);
                geocodedLocation.set("street_num", streetNum);

                String street = rs.getString(3);
                geocodedLocation.set("street", street);

                String state = rs.getString(4);
                geocodedLocation.set("state", state);

                String postal = rs.getString(5);
                geocodedLocation.set("postal", postal);

                String neighbourhood = rs.getString(6);
                geocodedLocation.set("neighbourhood", neighbourhood);

                String sublocality = rs.getString(7);
                geocodedLocation.set("sublocality", sublocality);

                String country = rs.getString(8);
                geocodedLocation.set("country", country);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return geocodedLocation;
    }

    public ArrayList<PokeSpawn> getNewPokemon() {
        dbLog.info("Getting new pokemon");

        final ArrayList<PokeSpawn> pokeSpawns = new ArrayList<>();

        if (blacklistQMarks == null) {
            blacklistQMarks = new StringBuilder("(");
            for (int i = 0; i < novaBot.config.getBlacklist().size(); ++i) {
                blacklistQMarks.append("?");
                if (i != novaBot.config.getBlacklist().size() - 1) {
                    blacklistQMarks.append(",");
                }
            }
            blacklistQMarks.append(")");
        }

        String sql = null;
        switch (novaBot.config.getScannerType()){
            case Monocle:
            case Hydro74000Monocle:
                sql = "" +
                        "SELECT pokemon_id," +
                        "       lat," +
                        "       lon," +
                        "       expire_timestamp," +
                        "       atk_iv," +
                        "       def_iv," +
                        "       sta_iv," +
                        "       move_1," +
                        "       move_2," +
                        "       gender," +
                        "       form," +
                        "       cp," +
                        "       level " +
                        "FROM sightings " +
                        "WHERE pokemon_id NOT IN " + blacklistQMarks + " AND updated >= UNIX_TIMESTAMP(DATE_SUB(?, INTERVAL 1 SECOND))";
                break;
            case RocketMap:
            case PhilMap:
                sql = "" +
                        "SELECT pokemon_id," +
                        "       latitude," +
                        "       longitude," +
                        "       disappear_time," +
                        "       individual_attack, " +
                        "       individual_defense," +
                        "       individual_stamina," +
                        "       move_1," +
                        "       move_2," +
                        "       weight," +
                        "       height," +
                        "       gender," +
                        "       form," +
                        "       cp, " +
                        "       cp_multiplier " +
                        "FROM pokemon " +
                        "WHERE pokemon_id NOT IN " + blacklistQMarks + " AND last_modified >= DATE_SUB(?,INTERVAL 1 SECOND)";
                break;
        }

        try (Connection connection = getScanConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= novaBot.config.getBlacklist().size(); ++i) {
                statement.setString(i, String.valueOf(novaBot.config.getBlacklist().get(i - 1)));
            }

            switch (novaBot.config.getScannerType()){
                case RocketMap:
                case PhilMap:
                    statement.setObject(novaBot.config.getBlacklist().size() + 1, lastChecked.toLocalDateTime(), Types.TIMESTAMP);
                    break;
                case Monocle:
                case Hydro74000Monocle:
                    statement.setObject(novaBot.config.getBlacklist().size() + 1, lastChecked.withZoneSameInstant(novaBot.config.getTimeZone()).toLocalDateTime(), Types.TIMESTAMP);
                    break;
            }
            dbLog.info("Executing query:" + statement);
            lastChecked = ZonedDateTime.now(UtilityFunctions.UTC);
            final ResultSet rs = statement.executeQuery();
            dbLog.info("Query complete");

            while (rs.next()) {
//                System.out.println("Current UTC time: " + ZonedDateTime.now(UtilityFunctions.UTC));
//                System.out.println("Current time based on config timezone : " + UtilityFunctions.getCurrentTime(novaBot.config.getTimeZone()));

                PokeSpawn pokeSpawn = null;

                switch(novaBot.config.getScannerType()){
                    case RocketMap:
                    case PhilMap:
                        int    id  = rs.getInt(1);
                        double lat = rs.getDouble(2);
                        double lon = rs.getDouble(3);
                        ZonedDateTime disappearTime = ZonedDateTime.ofLocal(rs.getTimestamp(4).toLocalDateTime(), UtilityFunctions.UTC, null);
                        int attack = rs.getInt(5);
                        int defense = rs.getInt(6);
                        int stamina = rs.getInt(7);
                        int move1 = rs.getInt(8);
                        int move2 = rs.getInt(9);
                        final float weight = rs.getFloat(10);
                        final float height = rs.getFloat(11);
                        int gender = rs.getInt(12);
                        int form = rs.getInt(13);
                        int cp = rs.getInt(14);
                        final double cpMod = rs.getDouble(15);
                        pokeSpawn = new PokeSpawn(id, lat, lon, disappearTime, attack, defense, stamina, move1, move2, weight, height, gender, form, cp, cpMod);
                        break;
                    case Monocle:
                    case Hydro74000Monocle:
                        id = rs.getInt(1);
                        lat = rs.getDouble(2);
                        lon = rs.getDouble(3);
                        disappearTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(rs.getLong(4)), UtilityFunctions.UTC);
                        attack = rs.getInt(5);
                        defense = rs.getInt(6);
                        stamina = rs.getInt(7);
                        move1 = rs.getInt(8);
                        move2 = rs.getInt(9);
                        gender = rs.getInt(10);
                        form = rs.getInt(11);
                        cp = rs.getInt(12);
                        final int level = rs.getInt(13);
                        pokeSpawn = new PokeSpawn(id,lat,lon,disappearTime,attack,defense,stamina,move1,move2,0,0,gender,form,cp,Integer.valueOf(level));
                        break;
                }

                try {
                    dbLog.info(pokeSpawn.toString());
                    dbLog.info(Integer.toString(pokeSpawn.hashCode()));

                    if (!hashCodes.contains(pokeSpawn.hashCode())) {
                        dbLog.debug("new pokemon, adding to list");
                        hashCodes.add(pokeSpawn.hashCode());
                        pokeSpawns.add(pokeSpawn);
                    } else {
                        dbLog.debug("pokemon already seen, ignoring");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            lastChecked = ZonedDateTime.now(UtilityFunctions.UTC);
            e.printStackTrace();
        }
        dbLog.info("Found: " + pokeSpawns.size());
        return pokeSpawns;
    }

    public String getSuburb(final double lat, final double lon) {
        String suburb = null;

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT suburb FROM geocoding WHERE lat = ? AND lon = ?")) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                suburb = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suburb;
    }

    public ArrayList<String> getUserIDsToNotify(final RaidSpawn raidSpawn) {
        final ArrayList<String> ids = new ArrayList<>();

        int geofences = raidSpawn.getGeofences().size();

        StringBuilder geofenceQMarks = new StringBuilder();
        for (int i = 0; i < geofences; ++i) {
            geofenceQMarks.append("?");
            if (i != geofences - 1) {
                geofenceQMarks.append(",");
            }
        }
        if (geofences > 0) geofenceQMarks.append(",");

        String sql = String.format(
                "SELECT DISTINCT(user_id) " +
                "FROM raid " +
                "WHERE (SELECT paused FROM users WHERE users.id = raid.user_id) = FALSE " +
                "AND LOWER(location) IN (%s%s'all') " +
                "AND boss_id=?;", geofenceQMarks.toString(), (novaBot.config.suburbsEnabled() ? "?, " : "")
                                  );

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < geofences; i++) {
                statement.setString(i + 1, raidSpawn.getGeofences().get(i).name.toLowerCase());
            }
            int offset = 1;
            if (novaBot.config.suburbsEnabled()) {
                statement.setString(geofences + offset, raidSpawn.properties.get(novaBot.config.getGoogleSuburbField()).toLowerCase());
                offset++;
            }
            statement.setInt(geofences + offset, raidSpawn.bossId);
            dbLog.debug(statement.toString());
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbLog.debug("Found " + ids.size() + " to notify");
        return ids;
    }

    public ArrayList<String> getUserIDsToNotify(String preset, Spawn spawn) {
        ArrayList<String> ids = new ArrayList<>();

        int geofences = spawn.getGeofences().size();

        StringBuilder geofenceQMarks = new StringBuilder();
        for (int i = 0; i < geofences; ++i) {
            geofenceQMarks.append("?");
            if (i != geofences - 1) {
                geofenceQMarks.append(",");
            }
        }
        if (geofences > 0) geofenceQMarks.append(",");

        String sql = String.format(
                "SELECT user_id " +
                "FROM preset " +
                "WHERE (SELECT paused FROM users WHERE users.id = preset.user_id) = FALSE " +
                "AND (LOWER(location) IN (%s%s'all'))" +
                "AND (preset_name = ?)", geofenceQMarks.toString(),novaBot.config.suburbsEnabled() ? "?," : ""
                                  );

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql)
        ) {
            for (int i = 0; i < geofences; i++) {
                statement.setString(i + 1, spawn.getGeofences().get(i).name.toLowerCase());
            }
            int offset = 1;
            if (novaBot.config.suburbsEnabled()) {
                statement.setString(geofences + offset, spawn.properties.get(novaBot.config.getGoogleSuburbField()).toLowerCase());
                offset++;
            }
            statement.setString(geofences + offset, preset);

            dbLog.info(statement.toString());
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }

    public ArrayList<String> getUserIDsToNotify(final PokeSpawn pokeSpawn) {
        final ArrayList<String> ids = new ArrayList<>();

        int geofences = pokeSpawn.getGeofences().size();

        StringBuilder geofenceQMarks = new StringBuilder();
        for (int i = 0; i < geofences; ++i) {
            geofenceQMarks.append("?");
            if (i != geofences - 1) {
                geofenceQMarks.append(",");
            }
        }
        if (geofences > 0) geofenceQMarks.append(",");

        String sql = String.format(
                "SELECT DISTINCT(user_id) " +
                "FROM pokemon " +
                "WHERE (SELECT paused FROM users WHERE users.id = pokemon.user_id) = FALSE " +
                "AND ((LOWER(location) IN (%s" + (novaBot.config.suburbsEnabled() ? "?," : "") + "'all')) " +
                "AND (id=? OR id=?) " +
                "AND (min_iv <= ?) " +
                "AND (max_iv >= ?));", geofenceQMarks.toString()
                                  );

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < geofences; i++) {
                statement.setString(i + 1, pokeSpawn.getGeofences().get(i).name.toLowerCase());
            }

            int offset = 1;

            if (novaBot.config.suburbsEnabled()) {
                statement.setString(geofences + offset, pokeSpawn.properties.get(novaBot.config.getGoogleSuburbField()).toLowerCase());
                offset++;
            }
            statement.setInt(geofences + offset, pokeSpawn.id);
            offset++;
            statement.setInt(geofences + offset, (pokeSpawn.form != null) ? 201 : pokeSpawn.id);
            offset++;
            statement.setDouble(geofences + offset, pokeSpawn.iv);
            offset++;
            statement.setDouble(geofences + offset, pokeSpawn.iv);
            dbLog.debug(statement.toString());
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbLog.debug("Found " + ids.size() + " to notify");
        return ids;
    }

    public UserPref getUserPref(final String id) {
        final UserPref userPref = new UserPref(novaBot);

        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery(String.format("SELECT id,location,max_iv,min_iv FROM pokemon WHERE user_id=%s", "'" + id + "'"));
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                final int      pokemon_id = rs.getInt(1);
                final Location location   = Location.fromDbString(rs.getString(2).toLowerCase(), novaBot);
                final float    max_iv     = rs.getFloat(3);
                final float    min_iv     = rs.getFloat(4);
                if (location == null) {
                    novaBot.novabotLog.warn("Location null fromDbString " + rs.getString(2).toLowerCase());
                } else {
                    userPref.addPokemon(new Pokemon(pokemon_id, location, min_iv, max_iv));
                }
            }

            statement.executeQuery(String.format("SELECT boss_id,location FROM raid WHERE user_id='%s'", id));

            rs = statement.getResultSet();

            while (rs.next()) {
                final int      bossId   = rs.getInt(1);
                final Location location = Location.fromDbString(rs.getString(2).toLowerCase(), novaBot);
                userPref.addRaid(new Raid(bossId, location));
            }

            statement.executeQuery(String.format("SELECT preset_name, location FROM preset WHERE user_id = '%s'", id));

            rs = statement.getResultSet();

            while (rs.next()) {
                String   presetName = rs.getString(1);
                Location location   = Location.fromDbString(rs.getString(2).toLowerCase(), novaBot);
                userPref.addPreset(presetName, location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userPref;
    }

    public int highestRaidLobbyId() {

        int highest = 0;

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT MAX(lobby_id) FROM raidlobby;")
        ) {

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                highest = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return highest;
    }

    public void logNewUser(final String userID) {
        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            final Date      date      = new Date();
            final Timestamp timestamp = new Timestamp(date.getTime());
            statement.executeUpdate(String.format("INSERT INTO users (id,joindate) VALUES ('%s','%s');", userID, timestamp));
        } catch (SQLIntegrityConstraintViolationException e) {
            dbLog.debug("Tried to add a duplicate user");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {

        NovaBot novaBot = new NovaBot();
        novaBot.setup();
        novaBot.start();
        novaBot.dbManager.novabotdbConnect();
//        novaBot.dbManager.scanDbConnect();

        System.out.println(new PokeSpawn(
                143,
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
                .1).buildMessage("formatting.ini").getContentRaw());

        System.out.println(new RaidSpawn(
                "gym",
                "id",
                -35.265134, 149.122796,
                Team.Valor,
                ZonedDateTime.ofInstant(Instant.now().plusSeconds(120), UtilityFunctions.UTC),
                ZonedDateTime.ofInstant(Instant.now().plusSeconds(60), UtilityFunctions.UTC),
                383,
                155555,
                18,
                22,
                5).buildMessage("formatting.ini").getEmbeds().get(0).getDescription());

        for (String gym : novaBot.dbManager.getUserIDsToNotify(new RaidSpawn("gym", "", -35.265134, 149.122796, Team.Uncontested, ZonedDateTime.now(), ZonedDateTime.now(), 383, 51000, 0, 0, 5))) {
            System.out.println(gym);
        }
//        novaBot.dbManager.addPokemon("246738412315475969",new Pokemon(2,Location.ALL,0,100));
//        for (PokeSpawn pokeSpawn : novaBot.dbManager.getNewPokemon()) {
//            System.out.println(pokeSpawn);
//        }
//        for (String s : novaBot.dbManager.getUserIDsToNotify(new PokeSpawn(
//                143,
//                35,
//                149,
//                ZonedDateTime.ofInstant(Instant.now().plusSeconds(60),UtilityFunctions.UTC),
//                15,
//                15,
//                15,
//                "",
//                "",
//                0,
//                0,
//                0,
//                0,
//                200,
//                .1))) {
//            System.out.println(s);
//        }

        //        MessageListener.main(null);
//
//        testing = true;
//
////        dbLog
//
//        loadConfig();
//        loadSuburbs();
//
//        novabotdbConnect();
//
//        System.out.println(getUserPref("107730875596169216").allPresetsToString());
//        System.out.println(config.findMatchingPresets(new RaidSpawn("raid","123",145,35,Team.Uncontested,DBManager.getCurrentTime(),DBManager.getCurrentTime(),3,1,1,1,3)));
//
//        System.out.println(getUserIDsToNotify("test",new RaidSpawn("raid","123",145,35,Team.Uncontested,DBManager.getCurrentTime(),DBManager.getCurrentTime(),1,1,1,1,2)));
//
//        PokeSpawn pokeSpawn = new PokeSpawn(143,35,149,new Timestamp(DBManager.getCurrentTime().getTime() + 504000), 15,15,15,"","",0,0,0,0,200,.1);
//
//        getUserIDsToNotify(pokeSpawn).forEach(System.out::println);
//        RaidSpawn spawn = new RaidSpawn("gym",
//                "12345", -35.265134, 149.122796,
//                new Timestamp(DBManager.getCurrentTime().getTime() + 504000),
//                new Timestamp(DBManager.getCurrentTime().getTime() + 6000000),
//                248,
//                11003,
//                "fire",
//                "fire blast",
//                4);
//
//        knownRaids.put(spawn.gymId, spawn);

//        getActiveLobbies();


//        System.out.println(getUserIDsToNotify(spawn));
//        System.out.println(pokeSpawn);

//        System.out.println(getUserIDsToNotify(pokeSpawn));

//        RaidSpawn raidSpawn = new RaidSpawn("gym","123",-35,-149,)
    }

    public void newLobby(String lobbyCode, String gymId, int memberCount, String channelId, String roleId, long nextTimeLeftUpdate, String inviteCode) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO raidlobby (lobby_id, gym_id, members, channel_id, role_id, next_timeleft_update,invite_code) VALUES (?,?,?,?,?,?,?)")
        ) {
            statement.setInt(1, Integer.parseInt(lobbyCode));
            statement.setString(2, gymId);
            statement.setInt(3, memberCount);
            statement.setString(4, channelId);
            statement.setString(5, roleId);
            statement.setInt(6, (int) nextTimeLeftUpdate);
            statement.setString(7, inviteCode);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean notContainsUser(final String userID) {
        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery(String.format("SELECT * FROM users WHERE id = %s;", "'" + userID + "'"));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                connection.close();
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void novabotdbConnect() {
//        novabotDataSource.setUser(novaBot.config.getNbUser());
//        novabotDataSource.setPassword(novaBot.config.getNbPass());
        nbUrl = String.format("jdbc:%s://%s:%s/%s?useSSL=%s", novaBot.config.getNbProtocol(),novaBot.config.getNbIp(), novaBot.config.getNbPort(), novaBot.config.getNbDbName(), novaBot.config.getNbUseSSL());
//        novabotDataSource.setDatabaseName(novaBot.config.getNbDbName());
    }

    public void pauseUser(String id) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET paused = TRUE WHERE id = ?")
        ) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetPokemon(String id) {
        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id = %s;", id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetPresets(String id) {
        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM preset WHERE user_id = %s;", id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetRaids(String id) {
        try (Connection connection = getNbConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM raid WHERE user_id = %s;", id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetUser(final String id) {
        resetRaids(id);
        resetPokemon(id);
        resetPresets(id);
    }

    public void scanDbConnect() {
//        scanDataSource.setUser(novaBot.config.getScanUser());
//        scanDataSource.setPassword(novaBot.config.getScanPass());
        scanUrl = String.format("jdbc:%s://%s:%s/%s?useSSL=%s", novaBot.config.getScanProtocol(),novaBot.config.getScanIp(), novaBot.config.getScanPort(), novaBot.config.getScanDbName(),novaBot.config.getScanUseSSL());
//        scanDataSource.setDatabaseName(novaBot.config.getScanDbName());
    }

    public void setGeocodedLocation(final double lat, final double lon, GeocodedLocation location) {

        dbLog.info("inserting location");
        dbLog.info(location.getProperties().toString());

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("" +
                                                                       "INSERT INTO geocoding " +
                                                                       "VALUES (?,?,?,?,?,?,?,?,?,?)")) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setString(3, location.getProperties().get("city"));
            statement.setString(4, location.getProperties().get("street_num"));
            statement.setString(5, location.getProperties().get("street"));
            statement.setString(6, location.getProperties().get("state"));
            statement.setString(7, location.getProperties().get("postal"));
            statement.setString(8, location.getProperties().get("neighbourhood"));
            statement.setString(9, location.getProperties().get("sublocality"));
            statement.setString(10, location.getProperties().get("country"));

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSuburb(final double lat, final double lon, final String suburb) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO geocoding VALUES (?,?,?)")) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setString(3, suburb);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unPauseUser(String id) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET paused = FALSE WHERE id = ?")
        ) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLobby(String lobbyCode, int memberCount, int nextTimeLeftUpdate, String inviteCode) {
        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE raidlobby SET members = ?, next_timeleft_update = ?, invite_code = ? WHERE lobby_id = ?")
        ) {
            statement.setInt(1, memberCount);
            statement.setInt(2, nextTimeLeftUpdate);
            statement.setString(3, inviteCode);
            statement.setInt(4, Integer.parseInt(lobbyCode));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkKnownRaids() {

        ArrayList<String> toRemove = new ArrayList<>();

        knownRaids.forEach((gymId, raid) -> {
            if (lastCheckedRaids.isAfter(raid.battleStart) && raid.bossId == 0) {
                dbLog.debug(String.format("%s egg has hatched, removing from known raids so we can find out boss pokemon", gymId));
                toRemove.add(gymId);
                dbLog.debug("Queued for removal " + raid);
            }

            if (lastCheckedRaids.isAfter(raid.raidEnd)) {
                dbLog.debug(String.format("%s raid has ended, removing from known raids so we can find new raids on this gym", gymId));
                toRemove.add(gymId);
                dbLog.debug("Queued for removal " + raid);
            }
        });

        toRemove.forEach(knownRaids::remove);

        dbLog.debug("Removed all queued gyms");
    }

    private void endLobbies(ArrayList<String> toDelete) {

        StringBuilder qMarks = new StringBuilder();
        for (int i = 0; i < toDelete.size(); ++i) {
            qMarks.append("?");
            if (i != toDelete.size() - 1) {
                qMarks.append(",");
            }
        }

        try (Connection connection = getNbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM raidlobby WHERE lobby_id IN (" + qMarks + ") ")
        ) {

            for (int i = 1; i <= toDelete.size(); i++) {
                statement.setString(i, toDelete.get(i - 1));
            }

            statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getScanConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user",novaBot.config.getScanUser());
            props.setProperty("password",novaBot.config.getScanPass());
            props.setProperty("ssl","false");
            return DriverManager.getConnection(scanUrl,props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Connection getNbConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user",novaBot.config.getNbUser());
            props.setProperty("password",novaBot.config.getNbPass());
            props.setProperty("ssl","false");
            return DriverManager.getConnection(nbUrl,props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
