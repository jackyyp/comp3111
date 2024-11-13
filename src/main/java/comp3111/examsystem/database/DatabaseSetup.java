package comp3111.examsystem.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void DBSetup() {
        String dropTableSQL = "DROP TABLE IF EXISTS students;";
        String createTableSQL =
        "CREATE TABLE IF NOT EXISTS students("
        +"id INTEGER PRIMARY KEY AUTOINCREMENT,"
        +"username VARCHAR(50) NOT NULL UNIQUE,"
        +"name VARCHAR(100) NOT NULL,"
        +"gender VARCHAR(10) NOT NULL,"
        +"age INTEGER UNSIGNED NOT NULL,"
        +"department VARCHAR(100) NOT NULL,"
        +"password VARCHAR(255) NOT NULL);";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropTableSQL);
            statement.executeUpdate(createTableSQL);
            System.out.println("Table 'Student' created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void DBTeacherSetup() {
        String dropTableSQL = "DROP TABLE IF EXISTS teachers;";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS teachers("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"username VARCHAR(50) NOT NULL UNIQUE,"
                +"name VARCHAR(100) NOT NULL,"
                +"gender VARCHAR(10) NOT NULL,"
                +"age INTEGER UNSIGNED NOT NULL,"
                +"department VARCHAR(100) NOT NULL,"
                +"password VARCHAR(255) NOT NULL);";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropTableSQL);
            statement.executeUpdate(createTableSQL);
            System.out.println("Table 'Teacher' created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DBTeacherSetup();
    }
}