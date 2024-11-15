package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TeacherRegisterController implements Initializable {
    @FXML
    private TextField usernameTxt;
    @FXML
    private TextField nameTxt;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private ComboBox<String> positionComboBox;
    @FXML
    private TextField ageTxt;
    @FXML
    private TextField departmentTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private PasswordField confirmPasswordTxt;
    @FXML
    private Label errorMessageLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        positionComboBox.setItems(FXCollections.observableArrayList("Junior", "Senior", "Parttime"));
    }

    @FXML
    public void close(ActionEvent e) {
        // Close the registration window
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    @FXML
    /*public void register(ActionEvent e) {
        String username = usernameTxt.getText();
        String name = nameTxt.getText();
        String gender = genderComboBox.getValue();
        String position = positionComboBox.getValue();
        String age = ageTxt.getText();
        String department = departmentTxt.getText();
        String password = passwordTxt.getText();
        String confirmPassword = confirmPasswordTxt.getText();

        try {
            if (username.isEmpty() || name.isEmpty() || gender == null || position==null || age.isEmpty() || Integer.parseInt(age) < 0 || department.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
                errorMessageLbl.setText("Error: Please check your inputs.");
                errorMessageLbl.setVisible(true);
                return;
            }
        } catch (NumberFormatException exception) {
            errorMessageLbl.setText("Error: Please check your inputs.");
            errorMessageLbl.setVisible(true);
            return;
        }


        String sql = "INSERT INTO teacher (username, name, gender, age, position, department, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5,position);
            pstmt.setString(6, department);
            pstmt.setString(7, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                errorMessageLbl.setText("Registration successful!");
                errorMessageLbl.setStyle("-fx-text-fill: green;");
                errorMessageLbl.setVisible(true);

                // Pause for 1 second then close the window
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> ((Stage) ((Button) e.getSource()).getScene().getWindow()).close());
                pause.play();
            } else {
                errorMessageLbl.setText("Failed to register user.");
                errorMessageLbl.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setVisible(true);
        }
    }*/

    private void register() {
        String username = usernameTxt.getText();
        String name = nameTxt.getText();
        String gender = genderComboBox.getValue();
        String position = positionComboBox.getValue();
        String age = ageTxt.getText();
        String department = departmentTxt.getText();
        String password = passwordTxt.getText();
        String confirmPassword = confirmPasswordTxt.getText();

        try {
            if (username.isEmpty() || name.isEmpty() || gender == null || age.isEmpty() || Integer.parseInt(age) < 0 || position == null || department.isEmpty() || password.isEmpty()|| !password.equals(confirmPassword)) {
                errorMessageLbl.setText("Error: Please check your inputs.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }
        } catch (NumberFormatException exception) {
            errorMessageLbl.setText("Error: Please check your inputs.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM teacher WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                errorMessageLbl.setText("Error: Username already exists.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
            return;
        }

        String sql = "INSERT INTO teacher (username, name, gender, age, position, department, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, position);
            pstmt.setString(6, department);
            pstmt.setString(7, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                errorMessageLbl.setText("Add Successful!");
                errorMessageLbl.setStyle("-fx-text-fill: green;");
                errorMessageLbl.setVisible(true);
            } else {
                errorMessageLbl.setText("Failed to add teacher.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

}

