package core;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import maps.GeocodedLocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static core.MessageListener.config;
import static core.MessageListener.loadConfig;

public class DBManager
{
    private static final MysqlDataSource rocketmapDataSource = new MysqlDataSource();
    private static final MysqlDataSource novabotDataSource = new MysqlDataSource();
    private static Timestamp lastChecked = getCurrentTime();

    private static RotatingSet<Integer> hashCodes = new RotatingSet<>(2000);



    public static void main(final String[] args) {
//        MessageListener.main(null);

        MessageListener.testing = true;

        loadConfig();

        novabotdbConnect();

        System.out.println(getGeocodedLocation(-35.405055,149.1270075).getProperties().toString());

//        System.out.println(getCurrentTime());
//        MessageListener.main(null);

//        System.out.println("Connecting to db");
////        rocketmapdbConnect();
//        novabotdbConnect();
//        System.out.println("Getting db connection");
//        final PokeSpawn pokeSpawn = new PokeSpawn(149, -35.264327, 149.116087, new Time(123L), 15, 15, 15, "", "", 1.0f, 2.0f, 1, 0, 2314);
//        System.out.println(pokeSpawn.buildMessage().getEmbeds().get(0).getTitle());
//        System.out.println(pokeSpawn.buildMessage().getEmbeds().get(0).getDescription());
//
//        getUserIDsToNotify(pokeSpawn).forEach((id) -> {if(id.equals("107730875596169216")) System.out.println("true");});

//        getUserIDsToNotify(pokeSpawn).forEach(System.out::println);
    }


    public static void novabotdbConnect() {
        DBManager.novabotDataSource.setUser(config.getNbUser());
        DBManager.novabotDataSource.setPassword(config.getNbPass());
        DBManager.novabotDataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s",   config.getNbIp(),config.getNbPort(),config.getNbDbName()));
        DBManager.novabotDataSource.setDatabaseName(config.getNbDbName());
    }

    public static void rocketmapdbConnect() {
        DBManager.rocketmapDataSource.setUser(config.getRmUser());
        DBManager.rocketmapDataSource.setPassword(config.getRmPass());
        DBManager.rocketmapDataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s", config.getRmIp(),config.getRmPort(),config.getRmDbName()));
        DBManager.rocketmapDataSource.setDatabaseName(config.getRmDbName());
    }

    private static Connection getConnection(final MysqlDataSource dataSource) {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            System.out.println("Conn is null, something fukedup");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<PokeSpawn> getNewPokemon() {
        System.out.println("Getting new pokemon");

        final ArrayList<PokeSpawn> pokeSpawns = new ArrayList<>();

        String blacklistQMarks = "(";
        for (int i = 0; i < config.getBlacklist().size(); ++i) {
            blacklistQMarks += "?";
            if (i != config.getBlacklist().size() - 1) {
                blacklistQMarks += ",";
            }
        }
        blacklistQMarks += ")";

        try (Connection connection = getConnection(rocketmapDataSource);
            PreparedStatement statement = connection.prepareStatement("" +
                    "SELECT pokemon_id," +
                    "       latitude," +
                    "       longitude," +
                    "       (CONVERT_TZ(disappear_time,'UTC','"+config.getTimeZone()+"'))," +
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
                    "WHERE pokemon_id NOT IN " + blacklistQMarks + " AND last_modified >= DATE_SUB(CONVERT_TZ(?,'"+config.getTimeZone()+"','UTC'),INTERVAL 1 SECOND)");)
        {
            for (int i = 1; i <= config.getBlacklist().size(); ++i) {
                statement.setString(i, String.valueOf(config.getBlacklist().get(i - 1)));
            }
            statement.setTimestamp(config.getBlacklist().size() + 1, DBManager.lastChecked);
            System.out.println("Executing query:");
            final ResultSet rs = statement.executeQuery();
            System.out.println(statement);
            System.out.println("Query complete");
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
                    final PokeSpawn pokeSpawn = new PokeSpawn(id, lat, lon, remainingTime, attack, defense, stamina, move1, move2, weight, height, gender, form, cp,cpMod);
                    System.out.println(pokeSpawn.toString());
                    System.out.println(pokeSpawn.hashCode());

                    if(!hashCodes.contains(pokeSpawn.hashCode())) {
                        System.out.println("new pokemon, adding to list");
                        hashCodes.add(pokeSpawn.hashCode());
                        pokeSpawns.add(pokeSpawn);
                    }else{
                        System.out.println("pokemon already seen, ignoring");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        DBManager.lastChecked = getCurrentTime();
        System.out.println("Found: " + pokeSpawns.size());
        return pokeSpawns;
    }

    public static boolean containsUser(final String userID) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeQuery(String.format("SELECT * FROM users WHERE id = %s;", "'" + userID + "'"));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                connection.close();
                statement.close();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Timestamp getJoinDate(final String userID) {
        Timestamp timestamp = null;
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeQuery(String.format("SELECT joindate FROM users WHERE id=%s;", userID));
            final ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                timestamp = rs.getTimestamp(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static void logNewUser(final String userID) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            final Date date = new Date();
            final Timestamp timestamp = new Timestamp(date.getTime());
            statement.executeUpdate(String.format("INSERT INTO users VALUES ('%s','%s');", userID, timestamp));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldNotify(final String userID, final PokeSpawn pokeSpawn) {

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeQuery(String.format("SELECT * FROM pokemon WHERE ((user_id=%s) AND ((channel=%s) OR (channel='All')) AND (id=%s) AND (min_iv <= %s) AND (max_iv >= %s));", "'" + userID + "'", "'" + pokeSpawn.region + "'", pokeSpawn.id, pokeSpawn.iv, pokeSpawn.iv));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                connection.close();
                statement.close();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void addUser(final String userID) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate(String.format("INSERT INTO users (id) VALUES (%s);", "'" + userID + "'"));
        }
        catch (MySQLIntegrityConstraintViolationException e3) {
            System.out.println("Cannot add duplicate");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deletePokemon(final String userID, final Pokemon pokemon) {

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement("" +
                     "DELETE FROM pokemon " +
                     "WHERE ((user_id=?) " +
                     "AND (LOWER(channel)=LOWER(?)) " +
                     "AND (id=?) " +
                     "AND (min_iv=?) " +
                     "AND (max_iv=?))"))
        {
            statement.setString(1, userID);
            statement.setString(2, (pokemon.getLocation().toString() == null) ? "All" : pokemon.getLocation().toString());
            statement.setDouble(3, pokemon.getID());
            statement.setDouble(4, pokemon.miniv);
            statement.setDouble(5, pokemon.maxiv);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPokemon(final String userID, final Pokemon pokemon) {

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement =connection.prepareStatement("INSERT INTO pokemon VALUES (?,?,?,?,?)"))
        {
            statement.setString(1, userID);
            statement.setInt(2, pokemon.getID());
            statement.setString(3, pokemon.getLocation().toString());
            statement.setDouble(4, pokemon.maxiv);
            statement.setDouble(5, pokemon.miniv);
            statement.executeUpdate();
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            System.out.println(e.getMessage());
        }
        catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    public static void resetUser(final String id) {
        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id = %s;", id));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getUserIDsToNotify(final PokeSpawn pokeSpawn) {
        final ArrayList<String> ids = new ArrayList<>();

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DISTINCT(user_id) " +
                         "FROM pokemon " +
                         "WHERE (((LOWER(channel)=LOWER(?)) " +
                         "OR (LOWER(channel)=LOWER(?)) " +
                         "OR (LOWER(channel)=LOWER('All'))) " +
                         "AND (id=? OR id=?) " +
                         "AND (min_iv <= ?) " +
                         "AND (max_iv >= ?));"))
        {
            statement.setString(1, (pokeSpawn.getRegion() == null) ? "All" : pokeSpawn.getRegion().toString());
            statement.setString(2, pokeSpawn.getSuburb());
            statement.setInt(3, pokeSpawn.id);
            statement.setInt(4, (pokeSpawn.form != null) ? 201 : pokeSpawn.id);
            statement.setDouble(5, pokeSpawn.iv);
            statement.setDouble(6, pokeSpawn.iv);
            System.out.println(statement);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Found " + ids.size() + " to notify");
        return ids;
    }

    public static UserPref getUserPref(final String id) {
        final UserPref userPref = new UserPref(id);

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeQuery(String.format("SELECT * FROM pokemon WHERE user_id=%s", "'" + id + "'"));
            final ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                final int pokemon_id = rs.getInt(2);
                final Location location = Location.fromDbString(rs.getString(3).toLowerCase());
                final float max_iv = rs.getFloat(4);
                final float min_iv = rs.getFloat(5);
                userPref.addPokemon(new Pokemon(pokemon_id, location, min_iv, max_iv), new Location[] { location });
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return userPref;
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
             Statement   statement = connection.createStatement())
        {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND id IN %s;", "'" + id + "'", idsString));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int countPokemon(final String id) {
        int pokemon = 0;

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement =  connection.createStatement())
        {
            statement.executeQuery(String.format("SELECT count(id) FROM pokemon WHERE user_id=%s;", "'" + id + "'"));
            final ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                pokemon = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return pokemon;
    }

    public static Timestamp getCurrentTime() {

        if(MessageListener.config == null) MessageListener.loadConfig();

        TimeZone.setDefault(TimeZone.getTimeZone(config.getTimeZone()));

        return new Timestamp(new Date().getTime());
    }

    public static void setGeocodedLocation(final double lat, final double lon, GeocodedLocation location) {

        System.out.println("inserting location");
        System.out.println(location.getProperties());

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement("" +
                     "INSERT INTO geocoding " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)"))
        {
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
                     "WHERE lat = ? AND lon = ?"))
        {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                geocodedLocation = new GeocodedLocation();

                String city = rs.getString(1);
                geocodedLocation.set("city",city);

                String streetNum = rs.getString(2);
                geocodedLocation.set("street_num",streetNum);

                String street = rs.getString(3);
                geocodedLocation.set("street",street);

                String state = rs.getString(4);
                geocodedLocation.set("state",state);

                String postal = rs.getString(5);
                geocodedLocation.set("postal",postal);

                String neighbourhood = rs.getString(6);
                geocodedLocation.set("neighbourhood",neighbourhood);

                String sublocality = rs.getString(7);
                geocodedLocation.set("sublocality",sublocality);

                String country = rs.getString(8);
                geocodedLocation.set("country",country);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return geocodedLocation;
    }

    public static String getSuburb(final double lat, final double lon) {
        String suburb = null;

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             PreparedStatement statement = connection.prepareStatement("SELECT suburb FROM geocoding WHERE lat = ? AND lon = ?"))
        {
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
             PreparedStatement statement = connection.prepareStatement("INSERT INTO geocoding VALUES (?,?,?)"))
        {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setString(3, suburb);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearLocations(final String id, final Location[] locations) {
        String locationsString = "(";
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                locationsString = locationsString + "'" + locations[i].toString().replace("'", "\\'") + "'";
            }
            else {
                locationsString = locationsString + "'" + locations[i].toString().replace("'", "\\'") + "',";
            }
        }
        locationsString += ")";

        try (Connection connection = getConnection(DBManager.novabotDataSource);
             Statement statement = connection.createStatement())
        {
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND channel IN %s;", "'" + id + "'", locationsString));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static int countSpawns(int id, TimeUnit intervalType, int intervalLength){
        int numSpawns = 0;

        try (Connection connection = getConnection(DBManager.rocketmapDataSource);
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT COUNT(*) " +
                     "FROM pokemon " +
                     "WHERE pokemon_id = ? AND disappear_time > CONVERT_TZ(NOW() - INTERVAL ? "+intervalType.toDbString() + ",?,'UTC')"))
        {
            statement.setInt(1, id);
            statement.setDouble(2, intervalLength);
            statement.setString(3,config.getTimeZone());
            System.out.println(statement);
            statement.executeQuery();

            ResultSet rs = statement.getResultSet();

            if(rs.next()){
                numSpawns = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return numSpawns;
    }
}
