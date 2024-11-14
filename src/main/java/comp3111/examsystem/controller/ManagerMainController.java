package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.model.ManagerControllerModel;
import javafx.event.ActionEvent;
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


public class ManagerMainController implements Initializable {
    @Setter
    private ManagerControllerModel dataModel;

    @FXML
    private Button studentButton;

    public void initialize(URL location, ResourceBundle resources) {
    }

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

    @FXML
    public void openStudentManageUI(ActionEvent event) {
        closeCurrentStage(event);
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

    @FXML
    public void openTeacherManageUI(ActionEvent event) {
        closeCurrentStage(event);
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

    @FXML
    public void openCourseManageUI(ActionEvent event) {
        closeCurrentStage(event);
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

    @FXML
    public void exit() {
        System.exit(0);
    }

    private void closeCurrentStage(ActionEvent event) {
        // Get the current stage from the event
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        // Close the current stage
        currentStage.close();
    }
}
