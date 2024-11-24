package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
/**
 * Controller class for handling teacher registration.
 * This class is responsible for managing the UI interactions for teacher registration.
 *
 * @author Wong Cheuk Yuen
 * @version 1.0
 */
public class TeacherRegisterController {

    @FXML
    public TextField usernameTxt;
    @FXML
    public TextField nameTxt;
    @FXML
    public ComboBox<String> genderComboBox;
    @FXML
    public ComboBox<String> positionComboBox;
    @FXML
    public TextField ageTxt;
    @FXML
    public TextField departmentTxt;
    @FXML
    public PasswordField passwordTxt;
    @FXML
    public PasswordField confirmPasswordTxt;
    @FXML
    public Label errorMessageLbl;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        positionComboBox.setItems(FXCollections.observableArrayList("Junior", "Senior", "Parttime"));
    }
    /**
     * Closes the registration window.
     *
     * @param e the action event triggered by the close button
     */
    @FXML
    public void close(ActionEvent e) {
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }
    /**
     * Handles the register action.
     * Validates the user input and performs registration if valid.
     *
     */
    @FXML
    public void register() {
        String username = usernameTxt.getText();
        String name = nameTxt.getText();
        String gender = genderComboBox.getValue();
        String position = positionComboBox.getValue();
        String age = ageTxt.getText();
        String department = departmentTxt.getText();
        String password = passwordTxt.getText();
        String confirmPassword = confirmPasswordTxt.getText();

        if (username.isEmpty() || name.isEmpty() || gender == null || age.isEmpty() || department.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            setMessage(false, "Error: Please check your inputs.");
            return;
        }
        try {
            if (Integer.parseInt(age) < 0) {
                setMessage(false, "Error: Please check your inputs.");
                return;
            }
        } catch (NumberFormatException e) {
            setMessage(false, "Error: Please check your inputs.");
            return;
        }

        String checkUsernameSql = "SELECT COUNT(*) FROM teacher WHERE username = ?";
        String insertSql = "INSERT INTO teacher (username, name, gender, age, position, department, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        boolean isDup = executePreparedStatement(checkUsernameSql, checkPstmt -> {
            checkPstmt.setString(1, username);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.getInt(1) > 0) {
                setMessage(false, "Error: Username already exists.");
                return true;
            }
            return false;
        });
        if (isDup) {
            return;
        }

        executePreparedStatement(insertSql, pstmt -> {
            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, position);
            pstmt.setString(6, department);
            pstmt.setString(7, password);

            pstmt.executeUpdate();
            setMessage(true, "Add Successful!");

            return true;
        });
    }
    /**
     * Executes a database operation using a connection from the database connection pool.
     *
     * @param operation the database operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executeDatabaseOperation(DatabaseOperation operation) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return operation.execute(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            setMessage(false, "Error connecting to the database.");
            return false;
        }
    }
    /**
     * Executes a prepared statement operation using a connection from the database connection pool.
     *
     * @param sql the SQL query to be executed
     * @param operation the prepared statement operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executePreparedStatement(String sql, PreparedStatementOperation operation) {
        return executeDatabaseOperation(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                return operation.execute(pstmt);
            } catch (SQLException e) {
                e.printStackTrace();
                setMessage(false, "Error connecting to the database.");
                return false;
            }
        });
    }
    /**
     * Functional interface for database operations.
     */
    @FunctionalInterface
    private interface DatabaseOperation {
        /**
         * Executes a database operation.
         *
         * @param conn the database connection
         * @return true if the operation was successful, false otherwise
         * @throws SQLException if a database access error occurs
         */
        boolean execute(Connection conn) throws SQLException;
    }
    /**
     * Functional interface for prepared statement operations.
     */
    @FunctionalInterface
    private interface PreparedStatementOperation {
        /**
         * Executes a prepared statement operation.
         *
         * @param pstmt the prepared statement
         * @return true if the operation was successful, false otherwise
         * @throws SQLException if a database access error occurs
         */
        boolean execute(PreparedStatement pstmt) throws SQLException;
    }
    /**
     * Sets the error message label with the specified message and style.
     *
     * @param success true if the operation was successful, false otherwise
     * @param message the message to be displayed
     */
    private void setMessage(boolean success, String message) {
        errorMessageLbl.setText(message);
        if (success) {
            errorMessageLbl.setStyle("-fx-text-fill: green;");
        } else {
            errorMessageLbl.setStyle("-fx-text-fill: red;");
        }
        errorMessageLbl.setVisible(true);
    }
}