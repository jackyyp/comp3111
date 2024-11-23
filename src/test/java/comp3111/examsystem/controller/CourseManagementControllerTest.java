package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class CourseManagementControllerTest {

    private CourseManagementController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        controller = new CourseManagementController();
        controller.courseIdFilter = new TextField();
        controller.courseNameFilter = new TextField();
        controller.departmentFilter = new TextField();
        controller.courseTable = new TableView<>();
        controller.courseIdColumn = new TableColumn<>("Course ID");
        controller.courseNameColumn = new TableColumn<>("Course Name");
        controller.departmentColumn = new TableColumn<>("Department");
        controller.courseIdField = new TextField();
        controller.courseNameField = new TextField();
        controller.departmentField = new TextField();
        controller.errorMessageLbl = new Label();

        VBox vbox = new VBox(controller.courseIdFilter, controller.courseNameFilter, controller.departmentFilter, controller.courseTable, controller.courseIdField, controller.courseNameField, controller.departmentField, controller.errorMessageLbl);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        DatabaseConnection.setMockConnection(mockConn);
    }

    @Test
    public void testFilterCoursesWithEmptyFilters(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(false);

        robot.interact(() -> controller.filterCourses());

        ObservableList<CourseManagementController.Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).isEmpty();
    }

    @Test
    public void testFilterCoursesWithNonEmptyFilters(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("courseId")).thenReturn("C123");
        when(mockRs.getString("courseName")).thenReturn("Algorithms");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> {
            controller.courseIdFilter.setText("C123");
            controller.courseNameFilter.setText("Algorithms");
            controller.departmentFilter.setText("CS");
            controller.filterCourses();
        });

        ObservableList<CourseManagementController.Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).hasSize(1);
        Assertions.assertThat(courses.get(0).getCourseId()).isEqualTo("C123");
        Assertions.assertThat(courses.get(0).getCourseName()).isEqualTo("Algorithms");
    }

    @Test
    public void testDeleteCourseWithSelection(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        CourseManagementController.Course course = new CourseManagementController.Course("C123", "Algorithms", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.interact(() -> controller.deleteCourse());

        ObservableList<CourseManagementController.Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).isEmpty();
    }

    @Test
    public void testDeleteCourseWithNoSelection(FxRobot robot) {
        robot.interact(() -> controller.deleteCourse());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No course selected.");
    }

    @Test
    public void testAddCourseWithAllFieldsFilled(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        robot.interact(() -> {
            controller.courseIdField.setText("C123");
            controller.courseNameField.setText("Algorithms");
            controller.departmentField.setText("CS");
            controller.addCourse();
        });

        ObservableList<CourseManagementController.Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).hasSize(1);
        Assertions.assertThat(courses.get(0).getCourseId()).isEqualTo("C123");
        Assertions.assertThat(courses.get(0).getCourseName()).isEqualTo("Algorithms");
    }

    @Test
    public void testAddCourseWithEmptyFields(FxRobot robot) {
        robot.interact(() -> {
            controller.courseIdField.setText("");
            controller.courseNameField.setText("Algorithms");
            controller.departmentField.setText("CS");
            controller.addCourse();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddCourseWithExistingCourseId(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);

        robot.interact(() -> {
            controller.courseIdField.setText("C123");
            controller.courseNameField.setText("Algorithms");
            controller.departmentField.setText("CS");
            controller.addCourse();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Course ID already exists.");
    }

    @Test
    public void testUpdateCourseWithSelectionAndAllFieldsUpdated(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        CourseManagementController.Course course = new CourseManagementController.Course("C123", "Algorithms", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.interact(() -> {
            controller.courseIdField.setText("C123");
            controller.courseNameField.setText("Advanced Algorithms");
            controller.departmentField.setText("CS");
            controller.updateCourse();
        });

        Assertions.assertThat(course.getCourseId()).isEqualTo("C123");
        Assertions.assertThat(course.getCourseName()).isEqualTo("Advanced Algorithms");
        Assertions.assertThat(course.getDepartment()).isEqualTo("CS");
    }

    @Test
    public void testUpdateCourseWithSelectionAndNoFieldsUpdated(FxRobot robot) {
        CourseManagementController.Course course = new CourseManagementController.Course("C123", "Algorithms", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.interact(() -> controller.updateCourse());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: No fields to update.");
    }

    @Test
    public void testUpdateCourseWithNoSelection(FxRobot robot) {
        robot.interact(() -> controller.updateCourse());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No course selected.");
    }

    @Test
    public void testLoadCoursesFromDatabase(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("courseId")).thenReturn("C123");
        when(mockRs.getString("courseName")).thenReturn("Algorithms");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> controller.loadCoursesFromDatabase());

        ObservableList<CourseManagementController.Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).hasSize(1);
        Assertions.assertThat(courses.get(0).getCourseId()).isEqualTo("C123");
        Assertions.assertThat(courses.get(0).getCourseName()).isEqualTo("Algorithms");
    }

    @Test
    public void testFilterCoursesDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        robot.interact(() -> controller.filterCourses());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testDeleteCourseDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        CourseManagementController.Course course = new CourseManagementController.Course("C123", "Algorithms", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.interact(() -> controller.deleteCourse());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testAddCourseDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        robot.interact(() -> {
            controller.courseIdField.setText("C123");
            controller.courseNameField.setText("Algorithms");
            controller.departmentField.setText("CS");
            controller.addCourse();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testUpdateCourseDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        CourseManagementController.Course course = new CourseManagementController.Course("C123", "Algorithms", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.interact(() -> {
            controller.courseIdField.setText("C123");
            controller.courseNameField.setText("Advanced Algorithms");
            controller.departmentField.setText("CS");
            controller.updateCourse();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testInitialize(FxRobot robot) {
        robot.interact(() -> controller.initialize());

        Assertions.assertThat(controller.courseTable.getItems()).isEmpty();
    }

    @Test
    public void testResetFilter(FxRobot robot) {
        robot.interact(() -> {
            controller.courseIdFilter.setText("C123");
            controller.courseNameFilter.setText("Algorithms");
            controller.departmentFilter.setText("CS");
            controller.resetFilter();
        });

        Assertions.assertThat(controller.courseIdFilter).hasText("");
        Assertions.assertThat(controller.courseNameFilter).hasText("");
        Assertions.assertThat(controller.departmentFilter).hasText("");
    }
}