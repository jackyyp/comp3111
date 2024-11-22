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

public class StudentRegisterController {

    @FXML
    public TextField usernameTxt;
    @FXML
    public TextField nameTxt;
    @FXML
    public ComboBox<String> genderComboBox;
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

    @FXML
    public void initialize() {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
    }

    @FXML
    public void close(ActionEvent e) {
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void register() {
        String username = usernameTxt.getText();
        String name = nameTxt.getText();
        String gender = genderComboBox.getValue();
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
                setMessage(false,"Error: Please check your inputs.");
                return;
            }
        } catch (NumberFormatException e) {
            setMessage(false, "Error: Please check your inputs.");
            return;
        }

        String checkUsernameSql = "SELECT COUNT(*) FROM student WHERE username = ?";
        String insertSql = "INSERT INTO student (username, name, gender, age, department, password) VALUES (?, ?, ?, ?, ?, ?)";

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
            pstmt.setString(5, department);
            pstmt.setString(6, password);

            pstmt.executeUpdate();
            setMessage(true, "Registration successful!");

            return true;
        });
    }

    private boolean executeDatabaseOperation(DatabaseOperation operation) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return operation.execute(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            setMessage(false, "Error connecting to the database.");
            return false;
        }
    }

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

    @FunctionalInterface
    interface DatabaseOperation {
        boolean execute(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    interface PreparedStatementOperation {
        boolean execute(PreparedStatement pstmt) throws SQLException;
    }

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