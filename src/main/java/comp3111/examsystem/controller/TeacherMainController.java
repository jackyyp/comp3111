package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import comp3111.examsystem.model.TeacherControllerModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for handling the main interface for teachers.
 * This class is responsible for managing the main UI interactions for teachers after they log in.
 *
 * @author Wong Cheuk Yuen
 * @version 1.0
 */
public class TeacherMainController implements Initializable {
    @FXML
    private VBox mainbox;
    private TeacherControllerModel dataModel;

    /**
     * Sets the data model for this controller.
     *
     * @param dataModel the data model to set
     */

    public void setDataModel(TeacherControllerModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Initializes the controller class.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Opens the Question Management UI.
     */
    @FXML
    public void openQuestionManageUI() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherQuestion.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Question Management");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Exam Management UI.
     *
     * @throws IOException if an I/O error occurs
     */
    @FXML
    public void openExamManageUI() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ExamManagement.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Exam Management");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Grade Statistics UI.
     */
    @FXML
    public void openGradeStatistic() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherGradeStatistic.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Grade Statistics");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application.
     */
    /**
     * Logs out the current user and opens the login UI.
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
                stage = (Stage) mainbox.getScene().getWindow();
            }

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

            // Reset the close request handler if it was set previously
            stage.setOnCloseRequest(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}