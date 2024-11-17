package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javafx.scene.control.Alert;
/**
 * The controller for the student main page.
 *
 * This controller is responsible for displaying the student's exam options and statistics.
 *
 * @author WANG Shao Fu
 */
public class StudentMainController implements Initializable {

    /**
     * The combo box for selecting the exam.
     */
    @FXML
    private ComboBox<String> examCombox;

    /**
     * The label for displaying the error message.
     */
    @FXML
    private Label errorLabel;

    /**
     * The data model for the student.
     */
    private StudentControllerModel dataModel;

    /**
     * Sets the data model for the student.
     *
     * @param dataModel the data model for the student
     */
    public void setDataModel(StudentControllerModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * A map to store exam names and their corresponding IDs.
     */
    private Map<String, Integer> examMap = new HashMap<>();

    /**
     * Initializes the controller.
     *
     * @param location the location of the controller
     * @param resources the resources of the controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadExams();
    }

    /**
     * Loads the exams from the database.
     */
    private void loadExams() {
        String sql = "SELECT e.id, e.name, e.course " +
                "FROM exam e " +
                "JOIN exam_question_link eql ON e.id = eql.exam_id " +
                "LEFT JOIN grade g ON e.id = g.exam_id AND g.student_id = ? " +
                "WHERE g.exam_id IS NULL AND e.is_published = 1 " +
                "ORDER BY e.name ASC";

        System.out.println("Executing SQL: " + sql);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dataModel.getUsername()); // Set the student ID for filtering
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Query Results:");
            Set<String> addedExamNames = new HashSet<>();
            while (rs.next()) {
                String courseName = rs.getString("course");
                String examName = rs.getString("name");
                int examId = rs.getInt("id");
                String displayName = courseName + " | " + examName;

                System.out.println("Exam ID: " + examId + ", Course: " + courseName + ", Exam: " + examName);

                if (!addedExamNames.contains(displayName)) {
                    examCombox.getItems().add(displayName);
                    examMap.put(displayName, examId); // Store the exam ID for each exam name
                    addedExamNames.add(displayName); // Mark this exam name as added
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

    /**
     * Opens the exam UI for the selected exam.
     *
     * @param event the event that triggered the open exam UI
     */
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

    /**
     * Opens the grade statistic UI.
     */
    @FXML
    public void openGradeStatistic() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentGradeStatisticUI.fxml"));
            fxmlLoader.setControllerFactory(param -> {
                StudentGradeStatisticController controller = new StudentGradeStatisticController();
                controller.setDataModel(dataModel); // Pass the dataModel to the new controller
                return controller;
            });
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Grade Statistics");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Set the stage as modal
            stage.showAndWait(); // Show the stage and wait for it to be closed before returning to the main page

            // Optionally close the current stage
            // Stage currentStage = (Stage) errorLabel.getScene().getWindow();
            // currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application.
     */
    @FXML
    public void exit() {
        System.exit(0);
    }

    /**
     * Logs out the user.
     *
     * @param event the event that triggered the logout
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
                stage = (Stage) examCombox.getScene().getWindow();
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