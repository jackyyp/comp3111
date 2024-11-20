package comp3111.examsystem.controller;

import comp3111.examsystem.controller.CourseManagementController;
import comp3111.examsystem.controller.CourseManagementController.Course;
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
        controller.courseIdField = new TextField();
        controller.courseNameField = new TextField();
        controller.departmentField = new TextField();
        controller.errorMessageLbl = new Label();

        controller.courseIdColumn = new TableColumn<>("Course ID");
        controller.courseNameColumn = new TableColumn<>("Course Name");
        controller.departmentColumn = new TableColumn<>("Department");


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
    public void testInitialize(FxRobot robot) {
        // Call the initialize method
        robot.interact(() -> controller.initialize());

        // Verify that the TableColumns are set up correctly
        Assertions.assertThat(controller.courseIdColumn.getText()).isEqualTo("Course ID");
        Assertions.assertThat(controller.courseNameColumn.getText()).isEqualTo("Course Name");
        Assertions.assertThat(controller.departmentColumn.getText()).isEqualTo("Department");

        // Verify that the TableView is empty initially
        Assertions.assertThat(controller.courseTable.getItems()).isEmpty();
    }

    @Test
    public void testResetFilter(FxRobot robot) {
        robot.clickOn(controller.courseIdFilter).write("CS101");
        robot.clickOn(controller.courseNameFilter).write("Intro to CS");
        robot.clickOn(controller.departmentFilter).write("CS");

        robot.interact(() -> controller.resetFilter());

        Assertions.assertThat(controller.courseIdFilter).hasText("");
        Assertions.assertThat(controller.courseNameFilter).hasText("");
        Assertions.assertThat(controller.departmentFilter).hasText("");
    }

    @Test
    public void testFilterCourses(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("courseId")).thenReturn("CS101");
        when(mockRs.getString("courseName")).thenReturn("Intro to CS");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> controller.filterCourses());

        ObservableList<Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).hasSize(1);
        Assertions.assertThat(courses.get(0).getCourseId()).isEqualTo("CS101");
        Assertions.assertThat(courses.get(0).getCourseName()).isEqualTo("Intro to CS");
        Assertions.assertThat(courses.get(0).getDepartment()).isEqualTo("CS");
    }

    @Test
    public void testAddCourse(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(1);

        robot.clickOn(controller.courseIdField).write("CS102");
        robot.clickOn(controller.courseNameField).write("Data Structures");
        robot.clickOn(controller.departmentField).write("CS");

        robot.interact(() -> controller.addCourse());

        ObservableList<Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).hasSize(1);
        Assertions.assertThat(courses.get(0).getCourseId()).isEqualTo("CS102");
        Assertions.assertThat(courses.get(0).getCourseName()).isEqualTo("Data Structures");
        Assertions.assertThat(courses.get(0).getDepartment()).isEqualTo("CS");
    }

    @Test
    public void testUpdateCourse(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Course course = new Course("CS101", "Intro to CS", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.clickOn(controller.courseIdField).write("CS101");
        robot.clickOn(controller.courseNameField).write("Advanced CS");
        robot.clickOn(controller.departmentField).write("CS");

        robot.interact(() -> controller.updateCourse());

        Assertions.assertThat(course.getCourseId()).isEqualTo("CS101");
        Assertions.assertThat(course.getCourseName()).isEqualTo("Advanced CS");
        Assertions.assertThat(course.getDepartment()).isEqualTo("CS");
    }

    @Test
    public void testDeleteCourse(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Course course = new Course("CS101", "Intro to CS", "CS");
        controller.courseTable.getItems().add(course);
        controller.courseTable.getSelectionModel().select(course);

        robot.interact(() -> controller.deleteCourse());

        ObservableList<Course> courses = controller.courseTable.getItems();
        Assertions.assertThat(courses).isEmpty();
    }
}