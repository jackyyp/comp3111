package comp3111.examsystem.controller;

import java.io.IOException;

import comp3111.examsystem.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Controller for the login selection screen.
 *
 * This class handles the login selection functionality, directing the user to either the
 * student, teacher, or manager login screens based on the selected option.
 *
 */
public class SelectLoginController {

    /**
     * Closes the current stage when a login option is selected.
     * 
     * @param event The action event that triggered this method.
     */
    @FXML
    public void closeCurrentStage(ActionEvent event) {
        // Get the current stage from the event
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        // Close the current stage
        currentStage.close();
    }

    /**
     * Opens the student login screen when the student login button is clicked.
     * 
     * @param event The action event that triggered this method.
     */
    @FXML
    public void studentLogin(ActionEvent event) {
        closeCurrentStage(event);
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentLoginUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Student Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the teacher login screen when the teacher login button is clicked.
     * 
     * @param event The action event that triggered this method.
     */
    @FXML
    public void teacherLogin(ActionEvent event) {
        closeCurrentStage(event);
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherLoginUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Teacher Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the manager login screen when the manager login button is clicked.
     * 
     * @param event The action event that triggered this method.
     */
    @FXML
    public void managerLogin(ActionEvent event) {
        closeCurrentStage(event);
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ManagerLoginUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Manager Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}