package core;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import maps.GeocodedLocation;
import net.dv8tion.jda.core.utils.SimpleLog;
import pokemon.PokeMove;
import pokemon.PokeSpawn;
import pokemon.Pokemon;
import raids.Raid;
import raids.RaidLobby;
import raids.RaidSpawn;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static core.FeedChannels.loadChannels;
import static core.MessageListener.*;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.*;

public class DBManager {
    private static final MysqlDataSource rocketmapDataSource = new MysqlDataSource();
    private static final MysqlDataSource novabotDataSource = new MysqlDataSource();
    private static Timestamp lastChecked = getCurrentTime();
    private static Timestamp lastCheckedRaids = getCurrentTime();

    private static RotatingSet<Integer> hashCodes = new RotatingSet<>(2000);

    public static HashMap<String, RaidSpawn> knownRaids = new HashMap<>();

    static SimpleLog dbLog = SimpleLog.getLog("DB");
    private static String blacklistQMarks;

    public static void main(final String[] args) {
//        MessageListener.main(null);

        testing = true;

        dbLog.setLevel(DEBUG);

        loadConfig();
        loadSuburbs();
        loadChannels();

        novabotdbConnect();

        PokeSpawn pokeSpawn = new PokeSpawn(143,35,149,new Timestamp(DBManager.getCurrentTime().getTime() + 504000), 15,15,15,"","",0,0,0,0,200,.1);

        getUserIDsToNotify(pokeSpawn).forEach(System.out::println);
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

    public static void novabotdbConnect() {
        DBManager.novabotDataSource.setUser(config.getNbUser());
        DBManager.novabotDataSource.setPassword(config.getNbPass());
        DBManager.novabotDataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s", config.getNbIp(), config.getNbPort(), config.getNbDbName()));
        DBManager.novabotDataSource.setDatabaseName(config.getNbDbName());
    }

    public static void rocketmapdbConnect() {
        DBManager.rocketmapDataSource.setUser(config.getRmUser());
        DBManager.rocketmapDataSource.setPassword(config.getRmPass());
        DBManager.rocketmapDataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s", config.getRmIp(), config.getRmPort(), config.getRmDbName()));
        DBManager.rocketmapDataSource.setDatabaseName(config.getRmDbName());
    }

    private static Connection getConnection(final MysqlDataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            dbLog.log(SimpleLog.Level.WARNING, "Conn is null, something fukedup");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<RaidSpawn> getCurrentRaids() {
        dbLog.log(INFO, "Checking which known gyms need to be updated");

        checkKnownRaids();

        dbLog.log(INFO, "Done");
//        dbLog.log(DEBUG,knownRaids);

        dbLog.log(INFO, "Getting new raid eggs");

        ArrayList<RaidSpawn> raidEggs = new ArrayList<>();


        String knownIdQMarks = "";

        if (knownRaids.size() > 0) {
            knownIdQMarks += "gym.gym_id NOT IN (";
            for (int i = 0; i < knownRaids.size(); ++i) {
                knownIdQMarks += "?";
                if (i != knownRaids.size() - 1) {
                    knownIdQMarks += ",";
                }
            }
            knownIdQMarks += ") AND";
        }

        ArrayList<String> knownIds = new ArrayList<>();

        knownRaids.keySet().forEach(knownIds::add);

        if (knownRaids.containsKey(null)) {
            System.out.println("NULL, OH NO KNOWNRAIDS CONTAINS NULL? WHY???");
            System.out.println(knownRaids.get(null));
        }

        try (Connection connection = getConnection(rocketmapDataSource);
             PreparedStatement statement = connection.prepareStatement(config.standardRaidTable()
                     ?   "SELECT"+
                         "  `gymdetails`.name," +
                         "  `gymdetails`.gym_id," +
                         "  `gym`.latitude," +
                         "  `gym`.longitude," +
                         "  (CONVERT_TZ(`raid`.end, 'UTC', '" + config.getTimeZone() + "')) AS end," +
                         "  (CONVERT_TZ(`raid`.start, 'UTC', '" + config.getTimeZone() + "')) AS battle," +
                         "  `raid`.pokemon_id," +
                         "  `raid`.cp, " +
                         "  `raid`.level, " +
                         "  `raid`.move_1, " +
                         "  `raid`.move_2 " +
                         "FROM `gym`" +
                         "  INNER JOIN gymdetails ON gym.gym_id = gymdetails.gym_id" +
                         "  INNER JOIN raid ON gym.gym_id = raid.gym_id " +
                         "WHERE " + knownIdQMarks + " `raid`.end > DATE_ADD(CONVERT_TZ(NOW(), '" + config.getTimeZone() +"', 'UTC'),INTERVAL 1 MINUTE)"
                     :   "SELECT" +
                         "  `gymdetails`.name," +
                         "  `gymdetails`.gym_id," +
                         "  `gym`.latitude," +
                         "  `gym`.longitude," +
                         "  (CONVERT_TZ(`raidinfo`.raid_end_ms, 'UTC', '" + config.getTimeZone() + "')) AS end," +
                         "  (CONVERT_TZ(`raidinfo`.raid_battle_ms, 'UTC', '" + config.getTimeZone() + "')) AS battle," +
                         "  `raidinfo`.pokemon_id," +
                         "  `raidinfo`.cp, " +
                         "  `raidinfo`.raid_level, " +
                         "  `raidinfo`.move_1, " +
                         "  `raidinfo`.move_2 " +
                         "FROM `gym`" +
                         "  INNER JOIN gymdetails ON gym.gym_id = gymdetails.gym_id" +
                         "  INNER JOIN raidinfo ON gym.gym_id = raidinfo.gym_id " +
                         "WHERE " + knownIdQMarks + " raid_end_ms > DATE_ADD(CONVERT_TZ(NOW(), '" + config.getTimeZone() + "', 'UTC'),INTERVAL 1 MINUTE)"
//                     "WHERE gym.last_scanned > ?" +
//                     "      AND raid_end_ms > CONVERT_TZ(NOW(),'"+config.getTimeZone()+"','UTC')"
             )
        ) {
            for (int i = 0; i < knownRaids.size(); i++) {
                statement.setString(i + 1, knownIds.get(i));
            }

            dbLog.log(INFO, "Executing query:");
//            dbLog.log(DEBUG,statement);
            final ResultSet rs = statement.executeQuery();
            dbLog.log(INFO, "Query complete");

            while (rs.next()) {
                String name = rs.getString(1);
                String gymId = rs.getString(2);
                double lat = rs.getDouble(3);
                double lon = rs.getDouble(4);
                Timestamp raidEnd = rs.getTimestamp(5);
                Timestamp battleStart = rs.getTimestamp(6);
                int bossId = rs.getInt(7);
                int bossCp = rs.getInt(8);
                int raidLevel = rs.getInt(9);
                String move_1 = PokeMove.idToName(rs.getInt(10));
                String move_2 = PokeMove.idToName(rs.getInt(11));

                RaidSpawn raidSpawn = new RaidSpawn(name, gymId, lat, lon, raidEnd, battleStart, bossId, bossCp, move_1, move_2, raidLevel);

                dbLog.log(DEBUG, raidSpawn);

                knownRaids.put(gymId, raidSpawn);

                raidEggs.add(raidSpawn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBManager.lastCheckedRaids = getCurrentTime();
        dbLog.log(INFO, "Found: " + raidEggs.size());

        return raidEggs;
    }

    private static void checkKnownRaids() {

        ArrayList<String> toRemove = new ArrayList<>();

        knownRaids.forEach((gymId, raid) -> {
            if (lastCheckedRaids.after(raid.battleStart) && raid.bossId == 0) {
                dbLog.log(DEBUG, String.format("%s egg has hatched, removing from known raids so we can find out boss pokemon", gymId));
                toRemove.add(gymId);
                dbLog.log(DEBUG, "Queued for removal " + raid);
            }

            if (lastCheckedRaids.after(raid.raidEnd)) {
                dbLog.log(DEBUG, String.format("%s raid has ended, removing from known raids so we can find new raids on this gym", gymId));
                toRemove.add(gymId);
                dbLog.log(DEBUG, "Queued for removal " + raid);
            }
        });

        toRemove.forEach(knownRaids::remove);

        dbLog.log(DEBUG, "Removed all queued gyms");
    }

    public static ArrayList<PokeSpawn> getNewPokemon() {
        dbLog.log(INFO, "Getting new pokemon");

        final ArrayList<PokeSpawn> pokeSpawns = new ArrayList<>();

        if (blacklistQMarks == null) {
            blacklistQMarks = "(";
            for (int i = 0; i < config.getBlacklist().size(); ++i) {
                blacklistQMarks += "?";
                if (i != config.getBlacklist().size() - 1) {
                    blacklistQMarks += ",";
                }
            }
            blacklistQMarks += ")";
        }

        try (Connection connection = getConnection(rocketmapDataSource);
             PreparedStatement statement = connection.prepareStatement("" +
                     "SELECT pokemon_id," +
                     "       latitude," +
                     "       longitude," +
                     "       (CONVERT_TZ(disappear_time,'UTC','" + config.getTimeZone() + "'))," +
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
                     "WHERE pokemon_id NOT IN " + blacklistQMarks + " AND last_modified >= DATE_SUB(CONVERT_TZ(?,'" + config.getTimeZone() + "','UTC'),INTERVAL 1 SECOND)");) {
            for (int i = 1; i <= config.getBlacklist().size(); ++i) {
                statement.setString(i, String.valueOf(config.getBlacklist().get(i - 1)));
            }
            statement.setTimestamp(config.getBlacklist().size() + 1, DBManager.lastChecked);
            dbLog.log(INFO, "Executing query:");
            final ResultSet rs = statement.executeQuery();
            dbLog.log(DEBUG, statement);
            dbLog.log(INFO, "Query complete");
            while (rs.next()) {
                final int id = rs.getInt(1);
                final double lat = rs.getDouble(2);
                final double lon = rs.getDouble(3);
                final Timestamp remainingTime = rs.getTimestamp(4);
                final int attack = rs.getInt(5);
                final int defense = rs.getInt(6);
                final int stamina = rs.getInt(7);
                final String move1 = PokeMove.idToName(rs.getInt(8));
                final String move2 = PokeMove.idToName(rs.getInt(9));
                final float weight = rs.getFloat(10);
                final float height = rs.getFloat(11);
                final int gender = rs.getInt(12);
                final int form = rs.getInt(13);
                final int cp = rs.getInt(14);
                final double cpMod = rs.getDouble(15);
                try {
                    final PokeSpawn pokeSpawn = new PokeSpawn(id, lat, lon, remainingTime, attack, defense, stamina, move1, move2, weight, height, gender, form, cp, cpMod);
                    dbLog.log(INFO, pokeSpawn.toString());
                    dbLog.log(INFO, pokeSpawn.hashCode());

                    if (!hashCodes.contains(pokeSpawn.hashCode())) {
                        dbLog.log(DEBUG, "new pokemon, adding to list");
                        hashCodes.add(pokeSpawn.hashCode());
                        pokeSpawns.add(pokeSpawn);
                    } else {
                        dbLog.log(DEBUG, "pokemon already seen, ignoring");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBManager.lastChecked = getCurrentTime();
        dbLog.log(INFO, "Found: " + pokeSpawns.size());
        return pokeSpawns;
    }

    public static boolean containsUser(final String userID) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(String.format("SELECT * FROM users WHERE id = %s;", "'" + userID + "'"));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                connection.close();
                statement.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Timestamp getJoinDate(final String userID) {
        Timestamp timestamp = null;
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(String.format("SELECT joindate FROM users WHERE id=%s;", userID));
            final ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                timestamp = rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static void logNewUser(final String userID) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            final Date date = new Date();
            final Timestamp timestamp = new Timestamp(date.getTime());
            statement.executeUpdate(String.format("INSERT INTO users (id,joindate) VALUES ('%s','%s');", userID, timestamp));
        } catch (MySQLIntegrityConstraintViolationException e) {
            dbLog.log(DEBUG, "Tried to add a duplicate user");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldNotify(final String userID, final PokeSpawn pokeSpawn) {

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(String.format("SELECT * FROM pokemon WHERE ((user_id=%s) AND ((location=%s) OR (location='All')) AND (id=%s) AND (min_iv <= %s) AND (max_iv >= %s));", "'" + userID + "'", "'" + pokeSpawn.region + "'", pokeSpawn.id, pokeSpawn.iv, pokeSpawn.iv));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                connection.close();
                statement.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void addUser(final String userID) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("INSERT INTO users (id) VALUES (%s);", "'" + userID + "'"));
        } catch (MySQLIntegrityConstraintViolationException e3) {
            dbLog.log(WARNING, "Cannot add duplicate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRaid(final String userID, final Raid raid) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO raid VALUES (?,?,?)")) {
            statement.setString(1, userID);
            statement.setDouble(2, raid.bossId);
            statement.setString(3, raid.location.toDbString());

            dbLog.log(DEBUG, statement);
            statement.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException e) {
            dbLog.log(SimpleLog.Level.WARNING, e.getMessage());
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void deleteRaid(final String userID, final Raid raid) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
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

    public static void clearRaid(final String id, final ArrayList<Raid> raids) {
        String idsString = "(";
        for (int i = 0; i < raids.size(); ++i) {
            if (i == raids.size() - 1) {
                idsString += raids.get(i).bossId;
            } else {
                idsString = idsString + raids.get(i).bossId + ",";
            }
        }
        idsString += ")";

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM raid WHERE user_id=%s AND boss_id IN %s;", "'" + id + "'", idsString));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getUserIDsToNotify(final RaidSpawn raidSpawn) {
        final ArrayList<String> ids = new ArrayList<>();

        int geofences = raidSpawn.getGeofenceIds().size();

        String geofenceQMarks = "";
        for (int i = 0; i < geofences; ++i) {
            geofenceQMarks += "?";
            if (i != geofences - 1) {
                geofenceQMarks += ",";
            }
        }
        if (geofences > 0) geofenceQMarks += ",";

        String sql = String.format(
                "SELECT DISTINCT(user_id) " +
                        "FROM raid " +
                        "WHERE (SELECT paused FROM users WHERE users.id = raid.user_id) = FALSE " +
                        "AND LOWER(location) IN (%s?,'all') " +
                        "AND boss_id=?;", geofenceQMarks
        );

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < geofences; i++) {
                statement.setString(i + 1, raidSpawn.getGeofenceIds().get(i).name.toLowerCase());
            }

//            statement.setString(geofences + 1, raidSpawn.properties.get("sublocality").toLowerCase());
//            statement.setString(geofences + 1, raidSpawn.getSuburb().toLowerCase());
            statement.setString(geofences + 1, raidSpawn.properties.get(config.getGoogleSuburbField()).toLowerCase());
            statement.setInt(geofences + 2, raidSpawn.bossId);
            dbLog.log(DEBUG, statement);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbLog.log(DEBUG, "Found " + ids.size() + " to notify");
        return ids;
    }

    public static void addPokemon(final String userID, final Pokemon pokemon) {

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement("" +
                     "INSERT INTO pokemon (user_id, id, location, max_iv, min_iv) " +
                     "VALUES (?,?,?,?,?)")) {
            statement.setString(1, userID);
            statement.setInt(2, pokemon.getID());
            statement.setString(3, pokemon.getLocation().toDbString());
            statement.setDouble(4, pokemon.maxiv);
            statement.setDouble(5, pokemon.miniv);

            dbLog.log(DEBUG, statement);
            statement.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException e) {
            dbLog.log(SimpleLog.Level.WARNING, e.getMessage());
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void deletePokemon(final String userID, final Pokemon pokemon) {

        try (Connection connection = getConnection(DBManager.novabotDataSource);
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

    public static void clearPokemon(final String id, final ArrayList<Pokemon> pokemons) {
        String idsString = "(";
        for (int i = 0; i < pokemons.size(); ++i) {
            if (i == pokemons.size() - 1) {
                idsString += pokemons.get(i).getID();
            } else {
                idsString = idsString + pokemons.get(i).getID() + ",";
            }
        }
        idsString += ")";

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND id IN %s;", "'" + id + "'", idsString));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetUser(final String id) {
        resetRaids(id);
        resetPokemon(id);
    }

    public static void resetRaids(String id) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM raid WHERE user_id = %s;", id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetPokemon(String id) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id = %s;", id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getUserIDsToNotify(final PokeSpawn pokeSpawn) {
        final ArrayList<String> ids = new ArrayList<>();

        int geofences = pokeSpawn.getGeofenceIds().size();

        String geofenceQMarks = "";
        for (int i = 0; i < geofences; ++i) {
            geofenceQMarks += "?";
            if (i != geofences - 1) {
                geofenceQMarks += ",";
            }
        }
        if (geofences > 0) geofenceQMarks += ",";

        String sql;

        if (pokeSpawn.feedChannel == null) {
            sql = String.format(
                    "SELECT DISTINCT(user_id) " +
                            "FROM pokemon " +
                            "WHERE (SELECT paused FROM users WHERE users.id = pokemon.user_id) = FALSE " +
                            "AND ((LOWER(location) IN (%s?,'all')) " +
                            "AND (id=? OR id=?) " +
                            "AND (min_iv <= ?) " +
                            "AND (max_iv >= ?));", geofenceQMarks
            );
        } else {
            sql = String.format(
                    "SELECT DISTINCT(user_id) " +
                            "FROM pokemon " +
                            "WHERE (SELECT paused FROM users WHERE users.id = pokemon.user_id) = FALSE " +
                            "AND ((LOWER(location) IN (%s?,?,'all')) " +
                            "AND (id=? OR id=?) " +
                            "AND (min_iv <= ?) " +
                            "AND (max_iv >= ?));", geofenceQMarks
            );
        }

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < geofences; i++) {
                statement.setString(i + 1, pokeSpawn.getGeofenceIds().get(i).name.toLowerCase());
            }

            if (pokeSpawn.feedChannel != null) {
                statement.setString(geofences + 1, pokeSpawn.feedChannel.getName().toLowerCase());
                geofences++;
            }

//            statement.setString(geofences + 1, pokeSpawn.getSuburb().toLowerCase());
//            statement.setString(geofences + 1, pokeSpawn.pokeProperties.get("sublocality").toLowerCase());
            statement.setString(geofences + 1, pokeSpawn.pokeProperties.get(config.getGoogleSuburbField()).toLowerCase());
            statement.setInt(geofences + 2, pokeSpawn.id);
            statement.setInt(geofences + 3, (pokeSpawn.form != null) ? 201 : pokeSpawn.id);
            statement.setDouble(geofences + 4, pokeSpawn.iv);
            statement.setDouble(geofences + 5, pokeSpawn.iv);
            dbLog.log(DEBUG, statement);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbLog.log(DEBUG, "Found " + ids.size() + " to notify");
        return ids;
    }

    public static UserPref getUserPref(final String id) {
        final UserPref userPref = new UserPref(isSupporter(id));

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeQuery(String.format("SELECT id,location,max_iv,min_iv FROM pokemon WHERE user_id=%s", "'" + id + "'"));
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                final int pokemon_id = rs.getInt(1);
                final Location location = Location.fromDbString(rs.getString(2).toLowerCase(), isSupporter(id));
                final float max_iv = rs.getFloat(3);
                final float min_iv = rs.getFloat(4);
                userPref.addPokemon(new Pokemon(pokemon_id, location, min_iv, max_iv));
            }

            statement.executeQuery(String.format("SELECT boss_id,location FROM raid WHERE user_id='%s'", id));

            rs = statement.getResultSet();

            while (rs.next()) {
                final int bossId = rs.getInt(1);
                final Location location = Location.fromDbString(rs.getString(2).toLowerCase(), isSupporter(id));
                userPref.addRaid(new Raid(bossId, location));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userPref;
    }

    public static int countRaids(final String id, boolean countLocations) {
        int raids = 0;

        String sql;

        if(countLocations){
            sql = "SELECT count(boss_id) FROM raid WHERE user_id=?";
        }else{
            sql = "SELECT count(distinct(boss_id)) FROM raid WHERE user_id=?";
        }

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1,id);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                raids = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return raids;
    }

    public static int countPokemon(final String id, boolean countLocations) {
        int pokemon = 0;

        String sql;

        if(countLocations){
            sql = "SELECT count(id) FROM pokemon WHERE user_id=?";
        }else{
            sql = "SELECT count(distinct(id)) FROM pokemon WHERE user_id=?";
        }

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1,id);

            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                pokemon = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pokemon;
    }

    public static Timestamp getCurrentTime() {

        if (config == null) loadConfig();

        TimeZone.setDefault(TimeZone.getTimeZone(config.getTimeZone()));

        return new Timestamp(new Date().getTime());
    }

    public static void setGeocodedLocation(final double lat, final double lon, GeocodedLocation location) {

        dbLog.log(INFO, "inserting location");
        dbLog.log(INFO, location.getProperties());

        try (Connection connection = getConnection(DBManager.novabotDataSource);
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

    public static GeocodedLocation getGeocodedLocation(final double lat, final double lon) {
        GeocodedLocation geocodedLocation = null;

        try (Connection connection = getConnection(DBManager.novabotDataSource);
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

    public static String getSuburb(final double lat, final double lon) {
        String suburb = null;

        try (Connection connection = getConnection(DBManager.novabotDataSource);
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

    public static void setSuburb(final double lat, final double lon, final String suburb) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO geocoding VALUES (?,?,?)")) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setString(3, suburb);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void clearLocationsRaids(String id, Location[] locations) {
        String locationsString = "(";
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                locationsString = locationsString + "'" + locations[i].toString().replace("'", "\\'") + "'";
            } else {
                locationsString = locationsString + "'" + locations[i].toString().replace("'", "\\'") + "',";
            }
        }
        locationsString += ")";

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM raid WHERE user_id=%s AND location IN %s;", "'" + id + "'", locationsString));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearLocationsPokemon(final String id, final Location[] locations) {
        String locationsString = "(";
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                locationsString = locationsString + "'" + locations[i].toString().replace("'", "\\'") + "'";
            } else {
                locationsString = locationsString + "'" + locations[i].toString().replace("'", "\\'") + "',";
            }
        }
        locationsString += ")";

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND location IN %s;", "'" + id + "'", locationsString));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static int countSpawns(int id, TimeUnit intervalType, int intervalLength) {
        int numSpawns = 0;

        try (Connection connection = getConnection(DBManager.rocketmapDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) " +
                             "FROM pokemon " +
                             "WHERE pokemon_id = ? AND disappear_time > CONVERT_TZ(NOW() - INTERVAL ? " + intervalType.toDbString() + ",?,'UTC')")) {
            statement.setInt(1, id);
            statement.setDouble(2, intervalLength);
            statement.setString(3, config.getTimeZone());
            dbLog.log(DEBUG, statement);
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

    public static void pauseUser(String id) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET paused = TRUE WHERE id = ?")
        ) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unPauseUser(String id) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET paused = FALSE WHERE id = ?")
        ) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void newLobby(String lobbyCode, String gymId, int memberCount, String channelId, String roleId, long nextTimeLeftUpdate, String inviteCode) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO raidlobby (lobby_id, gym_id, members, channel_id, role_id, next_timeleft_update,invite_code) VALUES (?,?,?,?,?,?,?)")
        ) {
            statement.setInt(1, Integer.parseInt(lobbyCode));
            statement.setString(2, gymId);
            statement.setInt(3, memberCount);
            statement.setString(4, channelId);
            statement.setString(5, roleId);
            statement.setInt(6, (int) nextTimeLeftUpdate);
            statement.setString(7,inviteCode);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLobby(String lobbyCode, int memberCount, int nextTimeLeftUpdate, String inviteCode) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE raidlobby SET members = ?, next_timeleft_update = ?, invite_code = ? WHERE lobby_id = ?")
        ) {
            statement.setInt(1, memberCount);
            statement.setInt(2, nextTimeLeftUpdate);
            statement.setString(3,inviteCode);
            statement.setInt(4, Integer.parseInt(lobbyCode));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void endLobby(String lobbyCode) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM raidlobby WHERE lobby_id = ?")
        ) {
            statement.setInt(1, Integer.parseInt(lobbyCode));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<RaidLobby> getActiveLobbies() {
        ArrayList<RaidLobby> activeLobbies = new ArrayList<>();

        ArrayList<String> toDelete = null;


        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT lobby_id,gym_id,channel_id,role_id,next_timeleft_update,invite_code FROM raidlobby;")
        ) {

            ResultSet rs = statement.executeQuery();


            while (rs.next()) {
                String lobbyCode = String.format("%04d", rs.getInt(1));
                String gymId = rs.getString(2);
                String channelId = rs.getString(3);
                String roleId = rs.getString(4);
                int nextTimeLeftUpdate = rs.getInt(5);
                String inviteCode = rs.getString(6);

                dbLog.log(INFO,String.format("Found lobby with info %s,%s,%s,%s in the db, checking for known raid",lobbyCode,gymId,channelId,roleId));

                RaidSpawn spawn = knownRaids.get(gymId);

                if (spawn == null) {
                    dbLog.log(WARNING, String.format("Couldn't find a known raid for gym id %s which was found as an active raid lobby, queuing for deletion", gymId));
                    if(toDelete == null){
                        toDelete = new ArrayList<>();
                    }

                    toDelete.add(lobbyCode);
                } else {
                    dbLog.log(INFO, String.format("Found a raid for gym id %s, lobby code %s",gymId,lobbyCode));
                    RaidLobby lobby = new RaidLobby(spawn, lobbyCode, channelId, roleId,inviteCode);
                    lobby.nextTimeLeftUpdate = nextTimeLeftUpdate;
                    lobby.loadMembers();

                    activeLobbies.add(lobby);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(toDelete != null) {
            dbLog.log(INFO, "Deleting non-existent lobbies");
            DBManager.endLobbies(toDelete);
        }

        return activeLobbies;
    }

    private static void endLobbies(ArrayList<String> toDelete) {

        String qMarks = "";
        for (int i = 0; i < toDelete.size(); ++i) {
            qMarks += "?";
            if (i != toDelete.size() - 1) {
                qMarks += ",";
            }
        }

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM raidlobby WHERE lobby_id IN ("+ qMarks + ") ")
        ) {

            for (int i = 1; i <= toDelete.size(); i++) {
                statement.setString(i,toDelete.get(i-1));
            }

            statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int highestRaidLobbyId() {

        int highest = 0;

        try (Connection connection = getConnection(DBManager.novabotDataSource);
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
}
