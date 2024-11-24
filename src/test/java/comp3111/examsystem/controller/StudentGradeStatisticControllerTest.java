package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
    public void setUp()  throws SQLException{


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

        DatabaseConnection.setMockConnection(conn);
        when(rs.next()).thenReturn(true).thenReturn(false); // Ensure rs.next() returns true once, then false
        when(rs.getString("course")).thenReturn("Course1");
        when(pstmt.executeQuery()).thenReturn(rs);

        DatabaseConnection.setMockConnection(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        controller.loadCourses();

        assertEquals(2, controller.courseComboBox.getItems().size()); // Expecting "All Courses" and "Course1"
        assertEquals("All Courses", controller.courseComboBox.getItems().get(0));
        assertEquals("Course1", controller.courseComboBox.getItems().get(1));
    }

    @Test
    public void testLoadGradesFromDatabaseWithFilter() throws SQLException {
        // Mock the Connection, PreparedStatement, and ResultSet
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        // Set up the DatabaseConnection to return the mocked Connection
        when(pstmt.executeQuery()).thenReturn(rs);
        DatabaseConnection.setMockConnection(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(rs.getString("course")).thenReturn("Course1");
        when(rs.getString("exam")).thenReturn("Exam1");
        when(rs.getInt("score")).thenReturn(85);
        when(rs.getInt("full_score")).thenReturn(100);
        when(rs.getInt("time_spent")).thenReturn(60);
        DatabaseConnection.setMockConnection(conn);

        doNothing().when(pstmt).setString(anyInt(), anyString());
        controller.loadGradesFromDatabase("Course1");
        when(rs.next()).thenReturn(true).thenReturn(false); // Ensure rs.next() returns true once, then false

        assertEquals(0, controller.gradeTable.getItems().size());

    }

    @Test
    public void testLoadGradesFromDatabaseWithoutFilter() throws SQLException {
        // Mock the Connection, PreparedStatement, and ResultSet
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        // Set up the DatabaseConnection to return the mocked Connection
        DatabaseConnection.setMockConnection(conn);
        // Mock the behavior of the Connection, PreparedStatement, and ResultSet
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false); // Ensure rs.next() returns true once, then false
        when(rs.getString("course")).thenReturn("Course1");
        when(rs.getString("exam")).thenReturn("Exam1");
        when(rs.getInt("score")).thenReturn(85);
        when(rs.getInt("full_score")).thenReturn(100);
        when(rs.getInt("time_spent")).thenReturn(60);

        // Mock the setString method
        doNothing().when(pstmt).setString(anyInt(), anyString());
        controller.loadGradesFromDatabase(null);

        assertEquals(1, controller.gradeTable.getItems().size());
        assertEquals("Course1", controller.gradeTable.getItems().get(0).getCourse());
        assertEquals("Exam1", controller.gradeTable.getItems().get(0).getExam());
        assertEquals(85, controller.gradeTable.getItems().get(0).getScore());
        assertEquals(100, controller.gradeTable.getItems().get(0).getFullScore());
        assertEquals(60, controller.gradeTable.getItems().get(0).getTime());
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