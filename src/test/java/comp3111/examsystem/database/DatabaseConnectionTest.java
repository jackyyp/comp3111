package comp3111.examsystem.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testGetConnection() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            assertNotNull(connection);
            connection.close();
        } catch (SQLException e) {
            fail("Failed to connect to the database.");
        }
    }
}