package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentGradeStatisticControllerTest extends ApplicationTest {

    private StudentGradeStatisticController controller;
    private StudentControllerModel dataModel;
    private ComboBox<String> courseComboBox;
    private TableView<StudentGradeStatisticController.Grade> gradeTable;
    private BarChart<String, Number> gradeChart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private TableColumn<StudentGradeStatisticController.Grade, String> courseColumn;
    private TableColumn<StudentGradeStatisticController.Grade, String> examColumn;
    private TableColumn<StudentGradeStatisticController.Grade, Integer> scoreColumn;
    private TableColumn<StudentGradeStatisticController.Grade, Integer> fullScoreColumn;
    private TableColumn<StudentGradeStatisticController.Grade, Integer> timeColumn;

    @Override
    public void start(Stage stage) {
        controller = new StudentGradeStatisticController();
        dataModel = mock(StudentControllerModel.class);
        when(dataModel.getUsername()).thenReturn("testUser");

        courseComboBox = new ComboBox<>();
        gradeTable = new TableView<>();
        gradeChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        courseColumn = new TableColumn<>();
        examColumn = new TableColumn<>();
        scoreColumn = new TableColumn<>();
        fullScoreColumn = new TableColumn<>();
        timeColumn = new TableColumn<>();

        controller.courseComboBox = courseComboBox;
        controller.gradeTable = gradeTable;
        controller.gradeChart = gradeChart;
        controller.xAxis = xAxis;
        controller.yAxis = yAxis;
        controller.courseColumn = courseColumn;
        controller.examColumn = examColumn;
        controller.scoreColumn = scoreColumn;
        controller.fullScoreColumn = fullScoreColumn;
        controller.timeColumn = timeColumn;
        controller.setDataModel(dataModel);

        Scene scene = new Scene(gradeTable);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        // No need to initialize JFXPanel
    }

    @Test
    public void testInitialize() {
        ResourceBundle resources = mock(ResourceBundle.class);
        controller.initialize(null, resources);

        assertNotNull(controller.courseComboBox.getOnAction());
    }

    @Test
    public void testLoadCourses() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("course")).thenReturn("Course1");

        controller.loadCourses();

        assertEquals(1, controller.courseComboBox.getItems().size());
    }

    @Test
    public void testLoadGradesFromDatabase() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("course")).thenReturn("Course1");
        when(rs.getString("exam")).thenReturn("Exam1");
        when(rs.getInt("score")).thenReturn(85);
        when(rs.getInt("full_score")).thenReturn(100);
        when(rs.getInt("time_spent")).thenReturn(60);

        controller.loadGradesFromDatabase(null);

        assertEquals(0, controller.gradeTable.getItems().size());
        ;

    }

    @Test
    public void testFilterGrades() {
        controller.courseComboBox.setItems(FXCollections.observableArrayList("All Courses", "Course1"));
        controller.courseComboBox.getSelectionModel().select("Course1");

        ActionEvent event = mock(ActionEvent.class);
        controller.filterGrades(event);

        assertEquals("Course1", controller.courseComboBox.getSelectionModel().getSelectedItem());
    }
}
