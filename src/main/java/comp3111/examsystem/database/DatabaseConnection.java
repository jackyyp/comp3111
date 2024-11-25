package comp3111.examsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class that provides a connection to the database.
 * The connection is established using the SQLite JDBC driver.
 * The connection is established to the database file examsystem.db.
 * The class also provides a method to set a mock connection for testing.
 * @author WANG Shao Fu
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:examsystem.db";
    private static Connection mockConnection;

    /**
     * Sets a mock connection for testing.
     * @param connection
     */
    public static void setMockConnection(Connection connection) {
        mockConnection = connection;
    }

    /**
     * Establishes a connection to the database.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (mockConnection != null) {
            return mockConnection;
        }
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys=ON");
        }
        return conn;
    }
}