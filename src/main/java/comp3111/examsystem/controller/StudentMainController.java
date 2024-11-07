package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javafx.scene.control.Alert;

public class StudentMainController implements Initializable {
    @FXML
    private ComboBox<String> examCombox;
    @FXML
    private Label errorLabel;
    private StudentControllerModel dataModel;

    public void setDataModel(StudentControllerModel dataModel) {
        this.dataModel = dataModel;
    }

    // Map to store exam names and their corresponding IDs
    private Map<String, Integer> examMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadExams();
    }

    private void loadExams() {
        String sql = "SELECT e.id, e.name " +
                "FROM exam e " +
                "JOIN exam_question_link eql ON e.id = eql.exam_id " +
                "LEFT JOIN grade g ON e.id = g.exam_id AND g.student_id = ? " +
                "WHERE g.exam_id IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dataModel.getUsername()); // Set the student ID for filtering
            ResultSet rs = pstmt.executeQuery();

            Set<String> addedExamNames = new HashSet<>();
            while (rs.next()) {
                String examName = rs.getString("name");
                int examId = rs.getInt("id");
                if (!addedExamNames.contains(examName)) {
                    examCombox.getItems().add(examName);
                    examMap.put(examName, examId); // Store the exam ID for each exam name
                    addedExamNames.add(examName); // Mark this exam name as added
                }
            }

            // Check if there are no exams left to take
            if (examMap.isEmpty()) {
                errorLabel.setText("Hooray! You have finished all exams!");
                errorLabel.setStyle("-fx-text-fill: green;"); // Set the text color to green
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
            errorLabel.setStyle("-fx-text-fill: red;"); // Set the text color to red for error
            return;  // Exit the method if no exam is selected
        }

        // Get the exam ID for the selected exam name
        int examId = examMap.get(selectedExam);
        dataModel.setExamId(examId); // Set the exam ID in the data model
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentExamPageUI.fxml"));
            fxmlLoader.setControllerFactory(param -> {
                StudentExamPageController controller = new StudentExamPageController();
                controller.setDataModel(dataModel); // Pass the dataModel to the new controller
                return controller;
            });
            Scene scene = new Scene(fxmlLoader.load());

            StudentExamPageController controller = fxmlLoader.getController();
            controller.setExamName(selectedExam);
            controller.loadQuestions(examId); // Load questions for the specific exam

            // Print exam ID for debugging
            System.out.println("Selected Exam ID: " + examId);

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
