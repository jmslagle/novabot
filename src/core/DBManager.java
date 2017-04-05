package core;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by Paris on 18/03/2017.
 */
public class DBManager {

    public static void main(String[] args) {
        connect("root","mimi");

        Pokemon pokemon = new Pokemon("houndoom");

        ArrayList<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(pokemon);

//        logNewUser("123456");
        System.out.println(getJoinDate("123456"));

    }

    static MysqlDataSource dataSource = new MysqlDataSource();
    static java.sql.Connection conn;

    public static void connect(String user, String pass){
        dataSource.setUser(user);
        dataSource.setPassword(pass);
        dataSource.setUrl("jdbc:mysql://localhost:3306/pokealerts");
//        dataSource.setUrl("jdbc:mysql://192.168.200.210:3306/pokealerts");
        dataSource.setDatabaseName("pokealerts");

    }

    public static java.sql.Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Conn is null, something fukedup");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean containsUser(String userID){
        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeQuery(
                    String.format(
                            "SELECT * FROM users WHERE id = %s;",
                            "'" + userID + "'"));

            ResultSet rs = statement.getResultSet();

            if (!rs.next()) {
                conn.close();
                statement.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static Timestamp getJoinDate(String userID){

        Timestamp timestamp = null;

        conn = getConnection();

        Statement statement = null;
        try {

            statement = conn.createStatement();
            statement.executeQuery(
                    String.format(
                            "SELECT joindate FROM users WHERE id=%s;",
                            userID));

            ResultSet rs = statement.getResultSet();

            if(rs.next()){
                timestamp = (rs.getTimestamp(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return timestamp;
    }

    public static void logNewUser(String userID){

        conn = getConnection();

        Statement statement = null;
        try {

            java.util.Date date = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

            statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "INSERT INTO users VALUES ('%s','%s');",
                            userID,
                            timestamp));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean shouldNotify(String userID, PokeSpawn pokeSpawn){
        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeQuery(
                    String.format(
                            "SELECT * FROM pokemon WHERE (" +
                                    "(user_id=%s) AND " +
                                    "((channel=%s) OR (channel='All')) AND " +
                                    "(id=%s) AND " +
                                    "(min_iv <= %s) AND " +
                                    "(max_iv >= %s));",
                            "'" + userID + "'",
                            "'" +pokeSpawn.region + "'",
                            pokeSpawn.id,
                            pokeSpawn.iv,
                            pokeSpawn.iv));

            ResultSet rs = statement.getResultSet();

            if (!rs.next()) {
                conn.close();
                statement.close();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static void addUser(String userID){
        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "INSERT INTO users VALUES (%s);",
                            "'" + userID + "'"));

        } catch (MySQLIntegrityConstraintViolationException e){
            System.out.println("Cannot add duplicate");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deletePokemon(String userID, Pokemon pokemon){
        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "DELETE FROM pokemon WHERE (" +
                                    "(user_id=%s) AND " +
                                    "(channel=%s) AND " +
                                    "(id=%s) AND " +
                                    "(min_iv=%s) AND " +
                                    "(max_iv=%s));",
                            "'" + userID + "'",
                            "'" +pokemon.region + "'",
                            pokemon.getID(),
                            pokemon.miniv,
                            pokemon.maxiv));

        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addPokemon(String userID, Pokemon pokemon){
        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "INSERT INTO pokemon VALUES (%s,%s,%s,%s,%s);",
                            "'" + userID + "'",
                            Pokemon.VALID_NAMES.indexOf(pokemon.name) + 1,
                            "'" + pokemon.region.toString() + "'",
                            pokemon.maxiv,
                            pokemon.miniv
                            ));

        } catch (MySQLIntegrityConstraintViolationException e) {
            System.out.println("Can't add duplicate entry");
        }
         catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void resetUser(String id) {
        conn = getConnection();

        Statement statement = null;
        try {
            assert conn != null;
            statement = conn.createStatement();
            statement.executeUpdate(String.format(
                    "DELETE FROM pokemon WHERE user_id = %s;",
                    id)
            );

            statement.close();

            conn.close();
        } catch (SQLException e) {
            try {
                statement.close();
            } catch (SQLException f) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException f) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getAllUserIDs() {
        conn = getConnection();

        ArrayList<String> ids = new ArrayList<>();

        Statement statement = null;
        try {
            assert conn != null;
            statement = conn.createStatement();
            statement.executeQuery("SELECT * FROM users;");

            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                ids.add(rs.getString(1));
            }

            statement.close();

            conn.close();
        } catch (SQLException e) {
            try {
                statement.close();
            } catch (SQLException f) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException f) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }

        return  ids ;
    }

    public static ArrayList<String> getUserIDsToNotify(PokeSpawn pokeSpawn) {
        conn = getConnection();

        ArrayList<String> ids = new ArrayList<>();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeQuery(
                    String.format(
                            "SELECT user_id FROM pokemon WHERE (" +
                                    "((channel=%s) OR (channel='All')) AND " +
                                    "(id=%s) AND " +
                                    "(min_iv <= %s) AND " +
                                    "(max_iv >= %s));",
                            "'" +pokeSpawn.region + "'",
                            pokeSpawn.id,
                            pokeSpawn.iv,
                            pokeSpawn.iv));

            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                ids.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Found " + ids.size() + " to notify");
        return ids;
    }

    public static UserPref getUserPref(String id) {
        conn = getConnection();

        UserPref userPref = new UserPref(id);

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeQuery(
                    String.format(
                            "SELECT * FROM pokemon WHERE user_id=%s",
                            "'" +id + "'"));

            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                int pokemon_id = rs.getInt(2);
                Region region = Region.valueOf(rs.getString(3));
                float max_iv = rs.getFloat(4);
                float min_iv = rs.getFloat(5);

                userPref.addPokemon(new Pokemon(pokemon_id,min_iv,max_iv),new Region[]{region});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userPref;
    }

    public static void clearPokemon(String id, ArrayList<Pokemon> pokemons) {

        String idsString = "(";

        for (int i = 0; i < pokemons.size(); i++) {
            if(i == pokemons.size() - 1)
                idsString += pokemons.get(i).getID();
            else
                idsString += pokemons.get(i).getID() + ",";
        }

        idsString += ")";

        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "DELETE FROM pokemon WHERE " +
                                    "user_id=%s AND " +
                                    "id IN %s;",
                            "'" +id + "'",
                            idsString));


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public static void clearRegions(String id, Region[] regions) {
        String regionsString = "(";

        for (int i = 0; i < regions.length; i++) {
            if(i == regions.length - 1)
                regionsString += "'" + regions[i] + "'";
            else
                regionsString += "'" + regions[i] + "'" + ",";
        }

        regionsString += ")";

        conn = getConnection();

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(
                    String.format(
                            "DELETE FROM pokemon WHERE " +
                                    "user_id=%s AND " +
                                    "channel IN %s;",
                            "'" +id + "'",
                            regionsString));


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int countPokemon(String id) {
        conn = getConnection();

        int pokemon = 0;

        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeQuery(
                    String.format(
                            "SELECT count(id) FROM pokemon WHERE " +
                                    "user_id=%s;",
                            "'" +id + "'"));

            ResultSet rs = statement.getResultSet();

            if(rs.next()){
                pokemon = rs.getInt(1);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return pokemon;
    }
}
