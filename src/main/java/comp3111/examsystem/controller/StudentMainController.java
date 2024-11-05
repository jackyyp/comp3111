package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class StudentMainController implements Initializable {
    @FXML
    private ComboBox<String> examCombox;
    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadExams();
    }

    private void loadExams() {
        String sql = "SELECT DISTINCT e.name " +
                     "FROM exam e " +
                     "JOIN exam_question_link eql ON e.id = eql.exam_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                examCombox.getItems().add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openExamUI(ActionEvent event) {
        String selectedExam = examCombox.getSelectionModel().getSelectedItem();

        // Check if no item is selected in the ComboBox
        if (selectedExam == null || selectedExam.isEmpty()) {
            // Set error message
            errorLabel.setText("Please select an exam before starting.");
            return;  // Exit the method if no exam is selected
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentExamPageUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            StudentExamPageController controller = fxmlLoader.getController();
            controller.setExamName(selectedExam);

            // Get the current stage
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Open new stage
            Stage stage = new Stage();
            stage.setTitle("Exams - " + selectedExam);
            stage.setScene(scene);
            stage.show();

            // Close the current stage
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openGradeStatistic() {
        // Implement the method to open the Grade Statistic page
    }

    @FXML
    public void exit() {
        System.exit(0);
    }
}
