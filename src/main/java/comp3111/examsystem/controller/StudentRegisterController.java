package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentRegisterController implements Initializable {
    @FXML
    private TextField usernameTxt;
    @FXML
    private TextField nameTxt;
    @FXML
    private ComboBox<String> genderComboBox;
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
    }

    @FXML
    public void register(ActionEvent e) {
        String username = usernameTxt.getText();
        String name = nameTxt.getText();
        String gender = genderComboBox.getValue();
        String age = ageTxt.getText();
        String department = departmentTxt.getText();
        String password = passwordTxt.getText();
        String confirmPassword = confirmPasswordTxt.getText();

        if (username.isEmpty() || name.isEmpty() || gender == null || Integer.parseInt(age)<0 ||age.isEmpty() || department.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            errorMessageLbl.setText("Error: Please check your inputs."); // Update error message
            errorMessageLbl.setVisible(true);
            return;
        }

        String sql = "INSERT INTO students (username, name, gender, age, department, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setString(4, age);
            pstmt.setString(5, department);
            pstmt.setString(6, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User registered successfully");
                // Close the registration window
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
            } else {
                errorMessageLbl.setText("Failed to register user.");
                errorMessageLbl.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setVisible(true);
        }
    }

    @FXML
    public void close(ActionEvent e) {
        // Close the registration window
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }
}
