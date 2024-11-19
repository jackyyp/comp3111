package comp3111.examsystem.controller;

import comp3111.examsystem.controller.TeacherManagementController;
import comp3111.examsystem.controller.TeacherManagementController.Teacher;
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
public class TeacherManagementControllerTest {

    private TeacherManagementController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        controller = new TeacherManagementController();
        controller.usernameField = new TextField();
        controller.nameField = new TextField();
        controller.ageField = new TextField();
        controller.genderComboBox = new ComboBox<>();
        controller.departmentField = new TextField();
        controller.passwordField = new TextField();
        controller.positionComboBox = new ComboBox<>();
        controller.teacherTable = new TableView<>();
        controller.errorMessageLbl = new Label();
        controller.usernameFilter = new TextField();
        controller.nameFilter = new TextField();
        controller.departmentFilter = new TextField();

        VBox vbox = new VBox(controller.usernameField, controller.nameField, controller.ageField, controller.genderComboBox, controller.departmentField, controller.passwordField, controller.positionComboBox, controller.teacherTable, controller.errorMessageLbl, controller.usernameFilter, controller.nameFilter, controller.departmentFilter);
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
            controller.usernameFilter.setText("T123");
            controller.nameFilter.setText("Jane Smith");
            controller.departmentFilter.setText("Math");
        });

        robot.interact(() -> controller.resetFilter());

        Assertions.assertThat(controller.usernameFilter).hasText("");
        Assertions.assertThat(controller.nameFilter).hasText("");
        Assertions.assertThat(controller.departmentFilter).hasText("");
    }

    @Test
    public void testFilterTeachers(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("T123");
        when(mockRs.getString("name")).thenReturn("Jane Smith");
        when(mockRs.getInt("age")).thenReturn(35);
        when(mockRs.getString("gender")).thenReturn("Female");
        when(mockRs.getString("department")).thenReturn("Math");

        robot.interact(() -> {
            controller.usernameFilter.setText("T123");
            controller.nameFilter.setText("Jane Smith");
            controller.departmentFilter.setText("Math");
            controller.filterTeachers();
        });

        ObservableList<Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
        Assertions.assertThat(teachers.get(0).getAge()).isEqualTo(35);
        Assertions.assertThat(teachers.get(0).getGender()).isEqualTo("Female");
        Assertions.assertThat(teachers.get(0).getDepartment()).isEqualTo("Math");
    }

    @Test
    public void testAddTeacher(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        robot.interact(() -> {
            controller.usernameField.setText("T123");
            controller.nameField.setText("Jane Smith");
            controller.ageField.setText("35");
            controller.genderComboBox.setValue("Female");
            controller.departmentField.setText("Math");
            controller.passwordField.setText("password");
            controller.positionComboBox.setValue("Junior");
            controller.addTeacher();
        });

        ObservableList<Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    public void testUpdateTeacher(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Teacher teacher = new Teacher("T123", "Jane Smith", "Female",35,"Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        robot.interact(() -> {
            controller.usernameField.setText("T123");
            controller.nameField.setText("Janet Smith");
            controller.ageField.setText("36");
            controller.genderComboBox.setValue("Female");
            controller.departmentField.setText("Physics");
            controller.passwordField.setText("newpassword");
            controller.positionComboBox.setValue("Senior");
            controller.updateTeacher();
        });

        Assertions.assertThat(teacher.getUsername()).isEqualTo("T123");
        Assertions.assertThat(teacher.getName()).isEqualTo("Janet Smith");
        Assertions.assertThat(teacher.getAge()).isEqualTo(36);
        Assertions.assertThat(teacher.getGender()).isEqualTo("Female");
        Assertions.assertThat(teacher.getDepartment()).isEqualTo("Physics");
        Assertions.assertThat(teacher.getPassword()).isEqualTo("newpassword");
        Assertions.assertThat(teacher.getPosition()).isEqualTo("Senior");
    }

    @Test
    public void testDeleteTeacher(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Teacher teacher = new Teacher("T123", "Jane Smith", "Female",35,"Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        robot.interact(() -> controller.deleteTeacher());

        ObservableList<Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).isEmpty();
    }
}