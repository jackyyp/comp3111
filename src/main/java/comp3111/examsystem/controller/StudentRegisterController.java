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
import java.sql.SQLException;
import java.util.ResourceBundle;
/**
 * The controller for the student registration page.
 *
 * This controller is responsible for handling the student's registration.
 *
 * @author WANG Shao Fu
 */
public class StudentRegisterController implements Initializable {

    /**
     * The text field for the username.
     */
    @FXML
    private TextField usernameTxt;

    /**
     * The text field for the name.
     */
    @FXML
    private TextField nameTxt;

    /**
     * The combo box for selecting the gender.
     */
    @FXML
    private ComboBox<String> genderComboBox;

    /**
     * The text field for the age.
     */
    @FXML
    private TextField ageTxt;

    /**
     * The text field for the department.
     */
    @FXML
    private TextField departmentTxt;

    /**
     * The password field for the password.
     */
    @FXML
    private PasswordField passwordTxt;

    /**
     * The password field for the confirm password.
     */
    @FXML
    private PasswordField confirmPasswordTxt;

    /**
     * The label for displaying the error message.
     */
    @FXML
    private Label errorMessageLbl;

    /**
     * Initializes the controller.
     *
     * @param location the location of the controller
     * @param resources the resources of the controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
    }

    /**
     * Closes the registration window.
     *
     * @param e the event that triggered the close
     */
    @FXML
    public void close(ActionEvent e) {
        // Close the registration window
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    /**
     * Registers the student.
     *
     * @param e the event that triggered the register
     */
    @FXML
    public void register(ActionEvent e) {
        String username = usernameTxt.getText();
        String name = nameTxt.getText();
        String gender = genderComboBox.getValue();
        String age = ageTxt.getText();
        String department = departmentTxt.getText();
        String password = passwordTxt.getText();
        String confirmPassword = confirmPasswordTxt.getText();

        try {
            if (username.isEmpty() || name.isEmpty() || gender == null || age.isEmpty() || Integer.parseInt(age) < 0 || department.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
                errorMessageLbl.setText("Error: Please check your inputs.");
                errorMessageLbl.setVisible(true);
                return;
            }
        } catch (NumberFormatException exception) {
            errorMessageLbl.setText("Error: Please check your inputs.");
            errorMessageLbl.setVisible(true);
            return;
        }

        String sql = "INSERT INTO student (username, name, gender, age, department, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, department);
            pstmt.setString(6, password);
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
    }
}