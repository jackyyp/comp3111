package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
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
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller class for managing the teacher grade statistics functionality.
 * <p>
 * This class handles the UI and operations for managing teacher grade statistics.
 * It includes methods for navigating to different sections and performing various tasks.
 *
 * @author Poon Chin Hung, Wang Shao Fu
 * @version 2.0
 */
public class TeacherGradeStatisticController {

    /**
     * Represents the grade statistics for a student.
     */
    @Data
    @AllArgsConstructor
    public static class Grade {
        private String studentName;
        private String courseNum;
        private String examName;
        private int score;
        private int fullScore;
        private int timeSpend;
    }

    @FXML
    public ChoiceBox<String> courseCombox;
    @FXML
    public ChoiceBox<String> examCombox;
    @FXML
    public ChoiceBox<String> studentCombox;
    @FXML
    public TableView<Grade> gradeTable;
    @FXML
    public TableColumn<Grade, String> studentColumn;
    @FXML
    public TableColumn<Grade, String> courseColumn;
    @FXML
    public TableColumn<Grade, String> examColumn;
    @FXML
    public TableColumn<Grade, Integer> scoreColumn;
    @FXML
    public TableColumn<Grade, Integer> fullScoreColumn;
    @FXML
    public TableColumn<Grade, Integer> timeSpendColumn;
    @FXML
    public BarChart<String, Number> barChart;
    @FXML
    public CategoryAxis categoryAxisBar;
    @FXML
    public NumberAxis numberAxisBar;
    @FXML
    public LineChart<String, Number> lineChart;
    @FXML
    public CategoryAxis categoryAxisLine;
    @FXML
    public NumberAxis numberAxisLine;
    @FXML
    public PieChart pieChart;

    public final ObservableList<Grade> gradeList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     *
     */
    @FXML
    public void initialize() {
        barChart.setLegendVisible(false);
        barChart.setAnimated(false); // Disable animation
        categoryAxisBar.setLabel("Course");
        numberAxisBar.setLabel("Avg. Score");
        pieChart.setLegendVisible(false);
        pieChart.setTitle("Student Scores");
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false); // Disable animation
        categoryAxisLine.setLabel("Exam");
        numberAxisLine.setLabel("Avg. Score");

        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseNum"));
        examColumn.setCellValueFactory(new PropertyValueFactory<>("examName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        fullScoreColumn.setCellValueFactory(new PropertyValueFactory<>("fullScore"));
        timeSpendColumn.setCellValueFactory(new PropertyValueFactory<>("timeSpend"));

        query();

        courseCombox.getItems().setAll(gradeList.stream().map(Grade::getCourseNum).distinct().collect(Collectors.toList()));
        examCombox.getItems().setAll(gradeList.stream().map(Grade::getExamName).distinct().collect(Collectors.toList()));
        studentCombox.getItems().setAll(gradeList.stream().map(Grade::getStudentName).distinct().collect(Collectors.toList()));

        loadChart();
    }

    /**
     * Resets the filter fields and reloads the grade statistics from the database.
     */
    @FXML
    public void reset() {
        courseCombox.setValue(null);
        examCombox.setValue(null);
        studentCombox.setValue(null);
        query();
    }

    /**
     * Queries the database and updates the grade list based on the selected filters.
     */
    @FXML
    public void query() {
        String sql = "SELECT s.name, e.course, e.name AS exam, g.score, " +
                "(SELECT SUM(q.score) FROM question q JOIN exam_question_link eql ON q.id = eql.question_id WHERE eql.exam_id = e.id) AS full_score, " +
                "g.time_spent " +
                "FROM grade g " +
                "JOIN exam e ON g.exam_id = e.id " +
                "JOIN student s ON s.username = g.student_id " +
                "WHERE 1=1";

        if (courseCombox.getValue() != null && !courseCombox.getValue().isEmpty()) {
            sql += " AND e.course = ?";
        }
        if (examCombox.getValue() != null && !examCombox.getValue().isEmpty()) {
            sql += " AND e.name = ?";
        }
        if (studentCombox.getValue() != null && !studentCombox.getValue().isEmpty()) {
            sql += " AND s.name = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (courseCombox.getValue() != null && !courseCombox.getValue().isEmpty()) {
                pstmt.setString(paramIndex++, courseCombox.getValue());
            }
            if (examCombox.getValue() != null && !examCombox.getValue().isEmpty()) {
                pstmt.setString(paramIndex++, examCombox.getValue());
            }
            if (studentCombox.getValue() != null && !studentCombox.getValue().isEmpty()) {
                pstmt.setString(paramIndex++, studentCombox.getValue());
            }

            ResultSet rs = pstmt.executeQuery();
            gradeList.clear();

            while (rs.next()) {
                gradeList.add(new Grade(
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("exam"),
                        rs.getInt("score"),
                        rs.getInt("full_score"),
                        rs.getInt("time_spent")
                ));
            }
            gradeTable.setItems(gradeList);
            loadChart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the chart data based on the grade list.
     */
    public void loadChart() {
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
        barChart.getData().clear();

        Map<String, Double> courseAvgScores = gradeList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Grade::getCourseNum, Collectors.averagingInt(Grade::getScore)));

        for (Map.Entry<String, Double> entry : courseAvgScores.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.setStyle("-fx-bar-fill: blue;"); // Set bar color to blue
                }
            });
            seriesBar.getData().add(data);
        }
        barChart.getData().add(seriesBar);

        pieChart.getData().clear();

        Map<String, Integer> studentTotalScores = gradeList.stream()
                .collect(Collectors.groupingBy(Grade::getStudentName, Collectors.summingInt(Grade::getScore)));

        for (Map.Entry<String, Integer> entry : studentTotalScores.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        XYChart.Series<String, Number> seriesLine = new XYChart.Series<>();
        lineChart.getData().clear();

        Map<String, Double> courseExamAvgScores = gradeList.stream()
                .collect(Collectors.groupingBy(g -> g.getCourseNum() + "-" + g.getExamName(), Collectors.averagingInt(Grade::getScore)));

        for (Map.Entry<String, Double> entry : courseExamAvgScores.entrySet()) {
            seriesLine.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        lineChart.getData().add(seriesLine);
    }
}