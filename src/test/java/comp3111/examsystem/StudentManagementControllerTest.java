package comp3111.examsystem;

import comp3111.examsystem.controller.StudentManagementController;
import comp3111.examsystem.controller.StudentManagementController.Student;
import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
public class StudentManagementControllerTest {

    private StudentManagementController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        controller = new StudentManagementController();
        controller.usernameField = new TextField();
        controller.nameField = new TextField();
        controller.ageField = new TextField();
        controller.genderComboBox = new ComboBox<>();
        controller.departmentField = new TextField();
        controller.passwordField = new TextField();
        controller.studentTable = new TableView<>();
        controller.errorMessageLbl = new Label();
        controller.usernameFilter = new TextField();
        controller.nameFilter = new TextField();
        controller.departmentFilter = new TextField();

        VBox vbox = new VBox(controller.usernameField, controller.nameField, controller.ageField, controller.genderComboBox, controller.departmentField, controller.passwordField, controller.studentTable, controller.errorMessageLbl, controller.usernameFilter, controller.nameFilter, controller.departmentFilter);
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
    public void testResetFilter(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameFilter.setText("S123");
            controller.nameFilter.setText("John Doe");
            controller.departmentFilter.setText("CS");
        });

        robot.interact(() -> controller.resetFilter());

        Assertions.assertThat(controller.usernameFilter).hasText("");
        Assertions.assertThat(controller.nameFilter).hasText("");
        Assertions.assertThat(controller.departmentFilter).hasText("");
    }

    @Test
    public void testFilterStudents(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("S123");
        when(mockRs.getString("name")).thenReturn("John Doe");
        when(mockRs.getInt("age")).thenReturn(20);
        when(mockRs.getString("gender")).thenReturn("Male");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> {
            controller.usernameFilter.setText("S123");
            controller.nameFilter.setText("John Doe");
            controller.departmentFilter.setText("CS");
            controller.filterStudents();
        });

        ObservableList<Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getUsername()).isEqualTo("S123");
        Assertions.assertThat(students.get(0).getName()).isEqualTo("John Doe");
        Assertions.assertThat(students.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(students.get(0).getGender()).isEqualTo("Male");
        Assertions.assertThat(students.get(0).getDepartment()).isEqualTo("CS");
    }

    @Test
    public void testAddStudent(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        ObservableList<Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getUsername()).isEqualTo("S123");
        Assertions.assertThat(students.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    public void testUpdateStudent(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Student student = new Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("Jane Doe");
            controller.ageField.setText("21");
            controller.genderComboBox.setValue("Female");
            controller.departmentField.setText("EE");
            controller.passwordField.setText("newpassword");
            controller.updateStudent();
        });

        Assertions.assertThat(student.getUsername()).isEqualTo("S123");
        Assertions.assertThat(student.getName()).isEqualTo("Jane Doe");
        Assertions.assertThat(student.getAge()).isEqualTo(21);
        Assertions.assertThat(student.getGender()).isEqualTo("Female");
        Assertions.assertThat(student.getDepartment()).isEqualTo("EE");
        Assertions.assertThat(student.getPassword()).isEqualTo("newpassword");
    }

    @Test
    public void testDeleteStudent(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Student student = new Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> controller.deleteStudent());

        ObservableList<Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).isEmpty();
    }
}