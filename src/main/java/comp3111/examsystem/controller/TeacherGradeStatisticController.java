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
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TeacherGradeStatisticController implements Initializable {
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
    private ChoiceBox<String> courseCombox;
    @FXML
    private ChoiceBox<String> examCombox;
    @FXML
    private ChoiceBox<String> studentCombox;
    @FXML
    private TableView<Grade> gradeTable;
    @FXML
    private TableColumn<Grade, String> studentColumn;
    @FXML
    private TableColumn<Grade, String> courseColumn;
    @FXML
    private TableColumn<Grade, String> examColumn;
    @FXML
    private TableColumn<Grade, Integer> scoreColumn;
    @FXML
    private TableColumn<Grade, Integer> fullScoreColumn;
    @FXML
    private TableColumn<Grade, Integer> timeSpendColumn;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis categoryAxisBar;
    @FXML
    private NumberAxis numberAxisBar;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis categoryAxisLine;
    @FXML
    private NumberAxis numberAxisLine;
    @FXML
    private PieChart pieChart;

    private final ObservableList<Grade> gradeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        barChart.setLegendVisible(false);
        categoryAxisBar.setLabel("Course");
        numberAxisBar.setLabel("Avg. Score");
        pieChart.setLegendVisible(false);
        pieChart.setTitle("Student Scores");
        lineChart.setLegendVisible(false);
        categoryAxisLine.setLabel("Exam");
        numberAxisLine.setLabel("Avg. Score");

        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseNum"));
        examColumn.setCellValueFactory(new PropertyValueFactory<>("examName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        fullScoreColumn.setCellValueFactory(new PropertyValueFactory<>("fullScore"));
        timeSpendColumn.setCellValueFactory(new PropertyValueFactory<>("timeSpend"));


        query();

        // Populate ChoiceBox elements with unique values from gradeList
        courseCombox.getItems().setAll(gradeList.stream().map(Grade::getCourseNum).distinct().collect(Collectors.toList()));
        examCombox.getItems().setAll(gradeList.stream().map(Grade::getExamName).distinct().collect(Collectors.toList()));
        studentCombox.getItems().setAll(gradeList.stream().map(Grade::getStudentName).distinct().collect(Collectors.toList()));

        loadChart();
    }


    @FXML
    public void reset() {
        courseCombox.setValue(null);
        examCombox.setValue(null);
        studentCombox.setValue(null);
        query();
    }

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
                Grade grade = new Grade(
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("exam"),
                        rs.getInt("score"),
                        rs.getInt("full_score"),
                        rs.getInt("time_spent")
                );
                gradeList.add(grade);
            }
            gradeTable.setItems(gradeList);
            loadChart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadChart() {
        // X axis: courseNum, Y axis: average score
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
        barChart.getData().clear();

        // Calculate average score per course
        Map<String, Double> courseAvgScores = gradeList.stream()
                .collect(Collectors.groupingBy(Grade::getCourseNum, Collectors.averagingInt(Grade::getScore)));

        for (Map.Entry<String, Double> entry : courseAvgScores.entrySet()) {
            seriesBar.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(seriesBar);

        pieChart.getData().clear();

        // Calculate total score per student
        Map<String, Integer> studentTotalScores = gradeList.stream()
                .collect(Collectors.groupingBy(Grade::getStudentName, Collectors.summingInt(Grade::getScore)));

        for (Map.Entry<String, Integer> entry : studentTotalScores.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // X axis: courseNum + "-" + examName, Y axis: average score (group by course + exam)
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