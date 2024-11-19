package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.model.ManagerControllerModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for managing the main functionality for managers.
 *
 * This class handles the main UI and operations for managers after they have logged in.
 * It includes methods for navigating to different sections and performing various tasks.
 *
 * @author Poon Chin Hung
 * @version 1.0
 */
public class ManagerMainController implements Initializable {
    @Setter
    private ManagerControllerModel dataModel;

    @FXML
    private Button studentButton;

    /**
     * Initializes the controller class.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Handles the logout action.
     *
     * This method is called when the logout button is pressed. It navigates back to the login UI.
     *
     * @param event the action event triggered by the logout button
     */
    @FXML
    public void logout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage;
            if (event.getSource() instanceof javafx.scene.Node) {
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            } else {
                // Get the stage using another method if the event source is not a Node, e.g., from a menu item
                stage = (Stage) studentButton.getScene().getWindow();
            }

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            // Reset the close request handler if it was set previously
            stage.setOnCloseRequest(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Student Management UI.
     *
     * This method is called when the student management button is pressed.
     *
     */
    @FXML
    public void openStudentManageUI() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentManagementUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Student Management");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Teacher Management UI.
     *
     * This method is called when the teacher management button is pressed.
     *
     */
    @FXML
    public void openTeacherManageUI() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherManagementUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Teacher Management");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Course Management UI.
     *
     * This method is called when the course management button is pressed.
     *
     */
    @FXML
    public void openCourseManageUI() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("CourseManagementUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Course Management");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}