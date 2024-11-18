package comp3111.examsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:examsystem.db";
    private static Connection mockConnection;

    public static void setMockConnection(Connection connection) {
        mockConnection = connection;
    }

    public static Connection getConnection() throws SQLException {
        if (mockConnection != null) {
            return mockConnection;
        }
        return DriverManager.getConnection(URL);
    }
}