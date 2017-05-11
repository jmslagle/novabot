package core;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class DBManager
{
    private static final MysqlDataSource rocketmapDataSource;
    private static final MysqlDataSource novabotDataSource;
    private static Connection novabotConnection;
    private static Timestamp lastChecked;

    public static void main(final String[] args) {
        System.out.println("Connecting to db");
        rocketmapdbConnect("root", "mimi");
        novabotdbConnect("root", "mimi");
        System.out.println("Getting db connection");
        final PokeSpawn pokeSpawn = new PokeSpawn(143, -35.0, 149.0, new Time(123L), 15, 15, 15, "", "", 1.0f, 2.0f, 1, 0);
        System.out.println(pokeSpawn);
        getUserIDsToNotify(pokeSpawn).forEach(System.out::println);
    }

    public static ArrayList<PokeSpawn> getNewPokemon() {
        System.out.println("Getting new pokemon");
        final Connection connection = getConnection(DBManager.rocketmapDataSource);
        final ArrayList<PokeSpawn> pokeSpawns = new ArrayList<PokeSpawn>();
        PreparedStatement statement = null;
        try {
            String blacklistQMarks = "(";
            for (int i = 0; i < MessageListener.blacklist.size(); ++i) {
                blacklistQMarks += "?";
                if (i != MessageListener.blacklist.size() - 1) {
                    blacklistQMarks += ",";
                }
            }
            blacklistQMarks += ")";
            assert connection != null;
            statement = connection.prepareStatement("SELECT pokemon_id,latitude, longitude, TIME((CONVERT_TZ(disappear_time,'UTC','Australia/Canberra'))),individual_attack, individual_defense,individual_stamina,move_1,move_2,weight,height,gender,form FROM pokemon WHERE pokemon_id NOT IN " + blacklistQMarks + " AND last_modified > CONVERT_TZ(?,'Australia/Canberra','UTC')");
            for (int i = 1; i <= MessageListener.blacklist.size(); ++i) {
                statement.setString(i, String.valueOf(MessageListener.blacklist.get(i - 1)));
            }
            statement.setTimestamp(MessageListener.blacklist.size() + 1, DBManager.lastChecked);
            System.out.println("Executing query");
            final ResultSet rs = statement.executeQuery();
            System.out.println("Query complete");
            while (rs.next()) {
                final int id = rs.getInt(1);
                final double lat = rs.getDouble(2);
                final double lon = rs.getDouble(3);
                final Time remainingTime = rs.getTime(4);
                final int attack = rs.getInt(5);
                final int defense = rs.getInt(6);
                final int stamina = rs.getInt(7);
                final String move1 = PokeMove.idToName(rs.getInt(8));
                final String move2 = PokeMove.idToName(rs.getInt(9));
                final float weight = rs.getFloat(10);
                final float height = rs.getFloat(11);
                final int gender = rs.getInt(12);
                final int form = rs.getInt(13);
                try {
                    final PokeSpawn pokeSpawn = new PokeSpawn(id, lat, lon, remainingTime, attack, defense, stamina, move1, move2, weight, height, gender, form);
                    System.out.println(pokeSpawn.toString());
                    pokeSpawns.add(pokeSpawn);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (SQLException e2) {
            e2.printStackTrace();
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (SQLException e3) {
                e2.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e3) {
                e2.printStackTrace();
            }
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (SQLException e3) {
                e3.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e3) {
                e3.printStackTrace();
            }
        }
        DBManager.lastChecked = getCurrentTime();
        System.out.println("Found: " + pokeSpawns.size());
        return pokeSpawns;
    }

    public static void novabotdbConnect(final String user, final String pass) {
        DBManager.novabotDataSource.setUser(user);
        DBManager.novabotDataSource.setPassword(pass);
        if (user.equals("novabot")) {
            DBManager.novabotDataSource.setUrl("jdbc:mysql://192.168.200.210:3306/pokealerts");
        }
        else {
            DBManager.novabotDataSource.setUrl("jdbc:mysql://localhost:3306/pokealerts");
        }
        DBManager.novabotDataSource.setDatabaseName("pokealerts");
    }

    public static void rocketmapdbConnect(final String user, final String pass) {
        DBManager.rocketmapDataSource.setUser(user);
        DBManager.rocketmapDataSource.setPassword(pass);
        if (user.equals("novabot")) {
            DBManager.rocketmapDataSource.setUrl("jdbc:mysql://192.168.200.210:3306/pokemongomapdb");
        }
        else {
            DBManager.rocketmapDataSource.setUrl("jdbc:mysql://localhost:3306/pokemongomapdb");
        }
        DBManager.rocketmapDataSource.setDatabaseName("pokemongomapdb");
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

    public static boolean containsUser(final String userID) {
        DBManager.novabotConnection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert DBManager.novabotConnection != null;
            statement = DBManager.novabotConnection.createStatement();
            statement.executeQuery(String.format("SELECT * FROM users WHERE id = %s;", "'" + userID + "'"));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                DBManager.novabotConnection.close();
                statement.close();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return true;
    }

    public static Timestamp getJoinDate(final String userID) {
        Timestamp timestamp = null;
        DBManager.novabotConnection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert DBManager.novabotConnection != null;
            statement = DBManager.novabotConnection.createStatement();
            statement.executeQuery(String.format("SELECT joindate FROM users WHERE id=%s;", userID));
            final ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                timestamp = rs.getTimestamp(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return timestamp;
    }

    public static void logNewUser(final String userID) {
        DBManager.novabotConnection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            final Date date = new Date();
            final Timestamp timestamp = new Timestamp(date.getTime());
            statement = DBManager.novabotConnection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO users VALUES ('%s','%s');", userID, timestamp));
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static boolean shouldNotify(final String userID, final PokeSpawn pokeSpawn) {
        DBManager.novabotConnection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert DBManager.novabotConnection != null;
            assert DBManager.novabotConnection != null;
            statement = DBManager.novabotConnection.createStatement();
            statement.executeQuery(String.format("SELECT * FROM pokemon WHERE ((user_id=%s) AND ((channel=%s) OR (channel='All')) AND (id=%s) AND (min_iv <= %s) AND (max_iv >= %s));", "'" + userID + "'", "'" + pokeSpawn.region + "'", pokeSpawn.id, pokeSpawn.iv, pokeSpawn.iv));
            final ResultSet rs = statement.getResultSet();
            if (!rs.next()) {
                DBManager.novabotConnection.close();
                statement.close();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return true;
    }

    public static void addUser(final String userID) {
        DBManager.novabotConnection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert DBManager.novabotConnection != null;
            statement = DBManager.novabotConnection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO users VALUES (%s);", "'" + userID + "'"));
        }
        catch (MySQLIntegrityConstraintViolationException e3) {
            System.out.println("Cannot add duplicate");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void deletePokemon(final String userID, final Pokemon pokemon) {
        DBManager.novabotConnection = getConnection(DBManager.novabotDataSource);
        PreparedStatement statement = null;
        try {
            assert DBManager.novabotConnection != null;
            statement = DBManager.novabotConnection.prepareStatement("DELETE FROM pokemon WHERE ((user_id=?) AND (LOWER(channel)=LOWER(?)) AND (id=?) AND (min_iv=?) AND (max_iv=?))");
            statement.setString(1, userID);
            statement.setString(2, (pokemon.getLocation().toString() == null) ? "All" : pokemon.getLocation().toString());
            statement.setDouble(3, pokemon.getID());
            statement.setDouble(4, pokemon.miniv);
            statement.setDouble(5, pokemon.maxiv);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                DBManager.novabotConnection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void addPokemon(final String userID, final Pokemon pokemon) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        PreparedStatement statement = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement("INSERT INTO pokemon VALUES (?,?,?,?,?)");
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
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e3) {
                e3.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e3) {
                e3.printStackTrace();
            }
        }
    }

    public static void resetUser(final String id) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert connection != null;
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id = %s;", id));
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException f) {
                e.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException f) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getUserIDsToNotify(final PokeSpawn pokeSpawn) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        final ArrayList<String> ids = new ArrayList<String>();
        PreparedStatement statement = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement("SELECT DISTINCT(user_id) FROM pokemon WHERE (((LOWER(channel)=LOWER(?)) OR (LOWER(channel)=LOWER(?)) OR (LOWER(channel)=LOWER('All'))) AND (id=? OR id=?) AND (min_iv <= ?) AND (max_iv >= ?));");
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
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        System.out.println("Found " + ids.size() + " to notify");
        return ids;
    }

    public static UserPref getUserPref(final String id) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        final UserPref userPref = new UserPref(id);
        Statement statement = null;
        try {
            assert connection != null;
            statement = connection.createStatement();
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
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return userPref;
    }

    public static void clearPokemon(final String id, final ArrayList<Pokemon> pokemons) {
        String idsString = "(";
        for (int i = 0; i < pokemons.size(); ++i) {
            if (i == pokemons.size() - 1) {
                idsString += pokemons.get(i).getID();
            }
            else {
                idsString = idsString + pokemons.get(i).getID() + ",";
            }
        }
        idsString += ")";
        final Connection connection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert connection != null;
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND id IN %s;", "'" + id + "'", idsString));
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static int countPokemon(final String id) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        int pokemon = 0;
        Statement statement = null;
        try {
            assert connection != null;
            statement = connection.createStatement();
            statement.executeQuery(String.format("SELECT count(id) FROM pokemon WHERE user_id=%s;", "'" + id + "'"));
            final ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                pokemon = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return pokemon;
    }

    public static Timestamp getCurrentTime() {
        final Date date = new Date();
        return new Timestamp(date.getTime());
    }

    public static String getSuburb(final double lat, final double lon) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        String suburb = null;
        PreparedStatement statement = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement("SELECT suburb FROM geocoding WHERE lat = ? AND lon = ?");
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                suburb = rs.getString(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return suburb;
    }

    public static void setSuburb(final double lat, final double lon, final String suburb) {
        final Connection connection = getConnection(DBManager.novabotDataSource);
        PreparedStatement statement = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement("INSERT INTO geocoding VALUES (?,?,?)");
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setString(3, suburb);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                assert connection != null;
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
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
        final Connection connection = getConnection(DBManager.novabotDataSource);
        Statement statement = null;
        try {
            assert connection != null;
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM pokemon WHERE user_id=%s AND channel IN %s;", "'" + id + "'", locationsString));
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e1) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                assert statement != null;
                statement.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
            try {
                connection.close();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    static {
        rocketmapDataSource = new MysqlDataSource();
        novabotDataSource = new MysqlDataSource();
        DBManager.lastChecked = getCurrentTime();
    }
}
