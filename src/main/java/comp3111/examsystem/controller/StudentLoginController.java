package comp3111.examsystem.controller;

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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentLoginController implements Initializable {
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private Label errorMessageLbl;


    public void initialize(URL location, ResourceBundle resources) {
    }


    @FXML
    public void login(ActionEvent e) {
        // after login, we should load the student's information
        StudentControllerModel dataModel = new StudentControllerModel();

        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        String sql = "SELECT * FROM student WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dataModel.setUsername(username);    // Set the username in the data model
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentMainUI.fxml"));
                fxmlLoader.setControllerFactory(param -> {
                    StudentMainController controller = new StudentMainController();
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

    @FXML
    public void register(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentRegisterPageUI.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Register for Examination Management System");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
