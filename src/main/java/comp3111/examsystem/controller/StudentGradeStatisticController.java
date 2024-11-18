package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * The controller for the student grade statistic page.
 *
 * This controller is responsible for displaying the student's grades and statistics.
 *
 * @author WANG Shao Fu
 */
public class StudentGradeStatisticController implements Initializable {

    /**
     * The combo box for selecting the course.
     */
    @FXML
    ComboBox<String> courseComboBox;

    /**
     * The table view for displaying the grades.
     */
    @FXML
    TableView<Grade> gradeTable;

    /**
     * The table column for the course.
     */
    @FXML
    TableColumn<Grade, String> courseColumn;

    /**
     * The table column for the exam.
     */
    @FXML
    TableColumn<Grade, String> examColumn;

    /**
     * The table column for the score.
     */
    @FXML
    TableColumn<Grade, Integer> scoreColumn;

    /**
     * The table column for the full score.
     */
    @FXML
    TableColumn<Grade, Integer> fullScoreColumn;

    /**
     * The table column for the time spent.
     */
    @FXML
    TableColumn<Grade, Integer> timeColumn;

    /**
     * The bar chart for displaying the grades.
     */
    @FXML
    BarChart<String, Number> gradeChart;

    /**
     * The x-axis for the bar chart.
     */
    @FXML
    CategoryAxis xAxis;

    /**
     * The y-axis for the bar chart.
     */
    @FXML
    NumberAxis yAxis;

    /**
     * The data model for the student.
     */
    StudentControllerModel dataModel;

    /**
     * Sets the data model for the student.
     *
     * @param dataModel the data model for the student
     */
    public void setDataModel(StudentControllerModel dataModel) {
        this.dataModel = dataModel;
        System.out.println("Current Username: " + dataModel.getUsername());
    }

    /**
     * Initializes the controller.
     *
     * @param location the location of the controller
     * @param resources the resources of the controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCourses();
        courseComboBox.setOnAction(this::filterGrades);

        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        examColumn.setCellValueFactory(new PropertyValueFactory<>("exam"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        fullScoreColumn.setCellValueFactory(new PropertyValueFactory<>("fullScore"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        loadGradesFromDatabase(null);
    }

    /**
     * Loads the courses from the database.
     */
    private void loadCourses() {
        String sql = "SELECT DISTINCT course FROM exam e " +
                "JOIN grade g ON e.id = g.exam_id " +
                "WHERE g.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dataModel.getUsername()); // Set the student ID for filtering
            ResultSet rs = pstmt.executeQuery();

            courseComboBox.getItems().add("All Courses");
            while (rs.next()) {
                String course = rs.getString("course");
                courseComboBox.getItems().add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the grades from the database.
     *
     * @param courseFilter the course filter
     */
    private void loadGradesFromDatabase(String courseFilter) {
        String sql = "SELECT e.course, e.name AS exam, g.score, " +
                "(SELECT SUM(q.score) FROM question q JOIN exam_question_link eql ON q.id = eql.question_id WHERE eql.exam_id = e.id) AS full_score, " +
                "g.time_spent " +
                "FROM grade g " +
                "JOIN exam e ON g.exam_id = e.id " +
                "WHERE g.student_id = ? ";

        if (courseFilter != null && !courseFilter.equals("All Courses")) {
            sql += " AND e.course = ? ORDER BY e.course ASC, e.name ASC";
        } else {
            sql += " ORDER BY e.course ASC, e.name ASC";
        }

        System.out.println("Executing SQL: " + sql);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dataModel.getUsername()); // Set the student ID for filtering
            if (courseFilter != null && !courseFilter.equals("All Courses")) {
                pstmt.setString(2, courseFilter); // Set the course filter
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.println("Query Results:");
            gradeTable.getItems().clear();
            gradeChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Grades");

            while (rs.next()) {
                String course = rs.getString("course");
                String exam = rs.getString("exam");
                int score = rs.getInt("score");
                int fullScore = rs.getInt("full_score");
                int time = rs.getInt("time_spent");

                System.out.println("Course: " + course + ", Exam: " + exam + ", Score: " + score + ", Full Score: " + fullScore + ", Time Spent: " + time);

                gradeTable.getItems().add(new Grade(course, exam, score, fullScore, time));

                // Use a combined label for course and exam for proper alignment
                XYChart.Data<String, Number> data = new XYChart.Data<>(course + " | " + exam, score);
                System.out.println("Adding to BarChart: Category = " + course + " | " + exam + ", Score = " + score);
                series.getData().add(data);
            }

            gradeChart.getData().add(series);
            System.out.println("BarChart Data: " + gradeChart.getData());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters the grades based on the selected course.
     *
     * @param event the event that triggered the filter
     */
    @FXML
    public void filterGrades(ActionEvent event) {
        String selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        loadGradesFromDatabase(selectedCourse);
    }

    /**
     * A grade.
     *
     * @author WANG Shao Fu
     */
    @Data
    @AllArgsConstructor
    public static class Grade {
        /**
         * The course of the grade.
         */
        private String course;

        /**
         * The exam of the grade.
         */
        private String exam;

        /**
         * The score of the grade.
         */
        private int score;

        /**
         * The full score of the grade.
         */
        private int fullScore;

        /**
         * The time spent on the grade.
         */
        private int time;
    }
}