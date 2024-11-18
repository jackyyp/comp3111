package comp3111.examsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import comp3111.examsystem.Main;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import comp3111.examsystem.model.TeacherControllerModel;

/**
 * Controller class for handling teacher login.
 *
 * @author Wong Cheuk Yuen
 * @version 1.0
 */
public class TeacherLoginController implements Initializable {
    @FXML
    private TextField usernameTxt;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private Label errorMessageLbl;

    /**
     * Initializes the controller class.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Handles the login action.
     * Validates the user input and performs login if valid.
     *
     * @param e the action event triggered by the login button
     */
    @FXML
    public void login(ActionEvent e) {
        // after login, we should load the student's information
        TeacherControllerModel dataModel = new TeacherControllerModel();

        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        String sql = "SELECT * FROM teacher WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dataModel.setUsername(username);    // Set the username in the data model
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherMainUI.fxml"));
                fxmlLoader.setControllerFactory(param -> {
                    TeacherMainController controller = new TeacherMainController();
                    controller.setDataModel(dataModel);
                    return controller;
                });

                Stage stage = new Stage();
                stage.setTitle("Hi " + username + ", Welcome to HKUST Examination System");
                stage.setScene(new Scene(fxmlLoader.load()));
                stage.show();

                // Close the current stage
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
                errorMessageLbl.setVisible(false); // Hide the error message on successful login
            } else {
                errorMessageLbl.setVisible(true); // Show the error message on failed login
            }

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            errorMessageLbl.setVisible(true); // Show the error message on exception
        }

    }

    /**
     * Handles the register action.
     * Opens the registration page.
     */
    @FXML
    public void register() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherRegisterPageUI.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Register for Examination Management System");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}