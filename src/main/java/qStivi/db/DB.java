package qStivi.db;

import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DB {
    //TODO make every number a long
    private static final Logger logger = getLogger(DB.class);

    public DB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to a sample database
     *
     * @param fileName the database file name
     */
    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:" + fileName + ".db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.info("The driver name is " + meta.getDriverName());
                logger.info("A new database has been created.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new table in the test database
     *
     * @param tblName Name of table
     * @param cols    columns of table in format "type name"
     */
    public static void createNewTable(String tblName, String... cols) {
        // SQLite connection string
        String url = "jdbc:sqlite:bot.db";

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tblName).append("(id INTEGER PRIMARY KEY");

        for (int i = 0, colsLength = cols.length; i < colsLength; i++) {
            String col = cols[i];
            var type = col.split(" ")[0];
            var name = col.split(" ")[1];

            sb.append(",").append(name).append(" ").append(type);
            if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("text")) {
                sb.append(" ").append("DEFAULT 0");
            }
            if (i < colsLength - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new table in the test database
     */
    @Deprecated
    public static void createNewUsersTable(String tbl) {
        // SQLite connection string
        String url = "jdbc:sqlite:bot.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id integer PRIMARY KEY," +
                "money integer default 100," +
                "xp integer default 0," +
                "last_worked int default 0," +
                "last_chat_message int default 0," +
                "last_command int default 0," +
                "last_reaction int default 0" +
                "times_played_blackjack int default 0" +
                "times_xp_from_chat int default 0" +
                "times_xp_from_voice int default 0" +
                "times_xp_from_reaction int default 0" +
                "amount_xp_from_reaction int default 0" +
                "amount_xp_from_voice int default 0" +
                "amount_xp_from_chat int default 0" +
                ")";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new table in the test database
     */
    @Deprecated
    public static void createNewQuotasTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:bot.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS quotas\n" +
                "(\n" +
                "    name  text PRIMARY KEY,\n" +
                "    wins  integer default 0,\n" +
                "    loses integer default 0,\n" +
                "    draws integer default 0,\n" +
                "    plays integer default 0\n" +
                ")";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a new row into the warehouses table
     *
     * @param table   name of table to insert to
     * @param colName name of column to insert to
     * @param value   value to insert to
     */
    public void insert(String table, String colName, Object value) {
        String sql = "INSERT INTO " + table + "(" + colName + ") VALUES(?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param tblName    name of table to update
     * @param colName    name of column to update
     * @param whereName  name of column to use as identifier
     * @param whereValue value of identifier
     * @param value      new value
     */
    public void update(String tblName, String colName, String whereName, Object whereValue, Object value) {
        String sql = "UPDATE %s SET %s = ? , WHERE %s = ?".formatted(tblName, colName, whereName);

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, value);
            pstmt.setObject(2, whereValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param name name of game
     */
    public void incrementValue(String tblName, String colName, String whereName, Object whereValue, int value) {
        String sql = "UPDATE %s SET %s = %s + ? WHERE %s = ?".formatted(tblName, colName, colName, whereName);

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setObject(1, whereValue);
            pstmt.setString(1, name);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a new row into the warehouses table
     *
     * @param id user id
     */
    @Deprecated
    public void insertOld(long id) {
        String sql = "INSERT INTO users(id) VALUES(?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a new row into the warehouses table
     *
     * @param name name of game
     */
    @Deprecated
    public void insertGame(String name) {
        String sql = "INSERT INTO quotas(name) VALUES(?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param id    user id
     * @param money money of the warehouse
     * @param xp    xp of the warehouse
     */
    @Deprecated
    public void updateOld(int id, String money, double xp) {
        String sql = "UPDATE users SET money = ? , "
                + "xp = ? "
                + "WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, money);
            pstmt.setDouble(2, xp);
            pstmt.setInt(3, id);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param name name of game
     */
    public void incrementWins(String name) {
        String sql = "UPDATE quotas SET wins = wins + 1 WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, name);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param name name of game
     */
    public void incrementLoses(String name) {
        String sql = "UPDATE quotas SET loses = loses + 1 WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, name);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param name name of game
     */
    public void incrementDraws(String name) {
        String sql = "UPDATE quotas SET draws = draws + 1 WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, name);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param name name of game
     */
    public void incrementPlays(String name) {
        String sql = "UPDATE quotas SET plays = plays + 1 WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, name);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a warehouse specified by the id
     *
     * @param id user id
     */
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:bot.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Get the warehouse whose capacity greater than a specified capacity
     *
     * @param xp xp to compare to
     */
    public void getXpGreaterThan(double xp) {
        String sql = "SELECT id, money, xp "
                + "FROM users WHERE xp > ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setDouble(1, xp);
            //
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                logger.info(rs.getLong("id") + "\t" +
                        rs.getString("money") + "\t" +
                        rs.getDouble("xp"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * select all rows in the warehouses table
     */
    public void selectAll() {
        String sql = "SELECT id, money, xp FROM users";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                logger.info(rs.getLong("id") + "\t" +
                        rs.getString("money") + "\t" +
                        rs.getDouble("xp"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(long id) {
        String sql = "select id from users where id = ? LIMIT 1";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setLong(1, id);
            //
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean gameExists(String id) {
        String sql = "select name from quotas where name = ? LIMIT 1";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, id);
            //
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getMoney(long id) {
        String sql = "select money from users where id = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("money");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getLastWorked(long id) {
        String sql = """
                select last_worked
                                 from users
                                 where id = ?""";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("last_worked");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getLastChatMessage(long id) {
        String sql = """
                select last_chat_message
                                 from users
                                 where id = ?""";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("last_chat_message");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getLastReaction(long id) {
        String sql = """
                select last_reaction
                                 from users
                                 where id = ?""";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("last_reaction");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getXp(long id) {
        String sql = "select xp from users where id = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("xp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    public List<Long> getTop10() {
        String sql = "select id from users order by money DESC, xp desc limit 10";
        List<Long> list = new ArrayList<>();

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getLong("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setXp(long id, int xp) {
        String sql = "update users SET xp = ? where id = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, xp);
            pstmt.setLong(2, id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateLastWorked(long id, long now) {
        String sql = "UPDATE users SET last_worked = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setLong(1, now);
            pstmt.setLong(2, id);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMoney(long id, long now) {
        String sql = "UPDATE users SET money = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setLong(1, now);
            pstmt.setLong(2, id);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLastChatMessage(long id, long now) {
        String sql = "UPDATE users SET last_chat_message = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setLong(1, now);
            pstmt.setLong(2, id);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLastReaction(long id, long now) {
        String sql = "UPDATE users SET last_reaction = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setLong(1, now);
            pstmt.setLong(2, id);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}