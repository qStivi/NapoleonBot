package qStivi.db;

import org.slf4j.Logger;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
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
    public void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:" + fileName + ".db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.info("The driver name is " + meta.getDriverName());
                logger.info("Database connection ok.");
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
    public void createNewTable(String tblName, String cols) {
        String query = "CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY, %s)".formatted(tblName, cols);

        try (Connection conn = this.connect()) {
            Statement stmt;
            if (conn != null) {
                stmt = conn.createStatement();
                stmt.execute(query);
            }
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
        String sql = "INSERT INTO %s(%s) VALUES(?)".formatted(table, colName);

        executeUpdate(value, sql);
    }

    private void executeUpdate(Object value, String sql) {
        try (Connection conn = this.connect()) {
            PreparedStatement pstmt;
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setObject(1, value);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a warehouse specified by the id
     *
     * @param table   name of table to insert to
     * @param colName name of column to insert to
     * @param value   value to insert to
     */
    public void delete(String table, String colName, Object value) {
        String sql = "DELETE FROM %s WHERE %s = ?".formatted(table, colName);

        executeUpdate(value, sql);
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
        String sql = "UPDATE %s SET %s = ? WHERE %s = ?".formatted(tblName, colName, whereName);

        try (Connection conn = this.connect()) {
            PreparedStatement pstmt;
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setObject(1, value);
                pstmt.setObject(2, whereValue);
                pstmt.executeUpdate();
            }
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
    public void increment(String tblName, String colName, String whereName, Object whereValue, long value) {
        String sql = "UPDATE %s SET %s = %s + ? WHERE %s = ?".formatted(tblName, colName, colName, whereName);

        inDeCrement(whereValue, value, sql);
    }

    private void inDeCrement(Object whereValue, long value, String sql) {
        try (Connection conn = this.connect()) {
            PreparedStatement pstmt;
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, value);
                pstmt.setObject(2, whereValue);
                pstmt.executeUpdate();
            }
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
    public void decrement(String tblName, String colName, String whereName, Object whereValue, long value) {
        String sql = "UPDATE %s SET %s = %s - ? WHERE %s = ?".formatted(tblName, colName, colName, whereName);

        inDeCrement(whereValue, value, sql);
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param tblName    name of table to update
     * @param colName    name of column to update
     * @param whereName  name of column to use as identifier
     * @param whereValue value of identifier
     * @return value
     */
    @Nullable
    @CheckForNull
    public Long selectLong(String tblName, String colName, String whereName, Object whereValue) {
        String sql = "select %s from %s where %s = ?".formatted(colName, tblName, whereName);

        try (Connection conn = this.connect()) {
            PreparedStatement pstmt;
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setObject(1, whereValue);
                var rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getLong(colName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        String url = "jdbc:sqlite:bot.db";
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userDoesNotExists(long id) {
        String sql = "select id from users where id = ? LIMIT 1";

        try (Connection conn = this.connect()) {
            PreparedStatement pstmt;
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<Long> getRanking() {
        String sql = "select id from users order by money DESC, xp desc";
        List<Long> list = new ArrayList<>();

        try (Connection conn = this.connect()) {
            PreparedStatement pstmt;
            if (conn != null) {
                pstmt = conn.prepareStatement(sql);
                var rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(rs.getLong("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}