package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.model.ManagerControllerModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ManagerMainController implements Initializable {
    @FXML
    private VBox mainbox;
    @FXML
    private Label errorLabel;
    @Setter
    private ManagerControllerModel dataModel;

    public void initialize(URL location, ResourceBundle resources) {
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
