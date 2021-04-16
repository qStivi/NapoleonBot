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
     */
    public static void createNewTable(String tbl) {
        // SQLite connection string
        String url = "jdbc:sqlite:bot.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS " + tbl + " (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	money integer default 100,\n"
                + "	xp integer default 0\n"
                + ");";

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
     * @param id
     */
    public void insert(long id) {
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
     * Update data of a warehouse specified by the id
     *
     * @param id
     * @param money money of the warehouse
     * @param xp    xp of the warehouse
     */
    public void update(int id, String money, double xp) {
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
     * Delete a warehouse specified by the id
     *
     * @param id
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
     * @param xp
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
        String sql = "select id from users order by money DESC LIMIT 10";
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
}