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

public class TeacherMainController implements Initializable {
    @FXML
    private VBox mainbox;
    private TeacherControllerModel dataModel;

    public void setDataModel(TeacherControllerModel dataModel) {
        this.dataModel = dataModel;
    }

    public void initialize(URL location, ResourceBundle resources) {
    }

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

    @FXML
    public void openExamManageUI() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherExamManageUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Exam Management");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    public void exit() {
        System.exit(0);
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
