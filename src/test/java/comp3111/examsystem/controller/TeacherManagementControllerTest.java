package comp3111.examsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TeacherManagementControllerTest extends ApplicationTest {

    private TeacherManagementController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @BeforeEach
    public void setUp() throws Exception {
        controller = new TeacherManagementController();
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        controller.usernameFilter = new TextField();
        controller.nameFilter = new TextField();
        controller.departmentFilter = new TextField();
        controller.teacherTable = new TableView<>();
        controller.usernameField = new TextField();
        controller.nameField = new TextField();
        controller.ageField = new TextField();
        controller.departmentField = new TextField();
        controller.passwordField = new TextField();
        controller.genderComboBox = new ComboBox<>();
        controller.positionComboBox = new ComboBox<>();
        controller.errorMessageLbl = new Label();

        controller.genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        controller.positionComboBox.setItems(FXCollections.observableArrayList("Junior", "Senior", "Parttime"));
    }

    @Test
    public void testInitialize() {
        controller.initialize();

        ObservableList<String> expectedGenders = FXCollections.observableArrayList("Male", "Female", "Other");
        ObservableList<String> expectedPositions = FXCollections.observableArrayList("Junior", "Senior", "Parttime");

        Assertions.assertThat(controller.genderComboBox.getItems()).containsExactlyElementsOf(expectedGenders);
        Assertions.assertThat(controller.positionComboBox.getItems()).containsExactlyElementsOf(expectedPositions);
        Assertions.assertThat(controller.teacherTable.getItems()).isEmpty();
    }

    @Test
    public void testResetFilter() {
        controller.usernameFilter.setText("T123");
        controller.nameFilter.setText("Jane Smith");
        controller.departmentFilter.setText("Math");

        controller.resetFilter();

        Assertions.assertThat(controller.usernameFilter).hasText("");
        Assertions.assertThat(controller.nameFilter).hasText("");
        Assertions.assertThat(controller.departmentFilter).hasText("");
    }

    @Test
    public void testFilterTeachers() throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("T123");
        when(mockRs.getString("name")).thenReturn("Jane Smith");
        when(mockRs.getInt("age")).thenReturn(35);
        when(mockRs.getString("gender")).thenReturn("Female");
        when(mockRs.getString("department")).thenReturn("Math");

        controller.usernameFilter.setText("T123");
        controller.nameFilter.setText("Jane Smith");
        controller.departmentFilter.setText("Math");
        controller.filterTeachers();

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
        Assertions.assertThat(teachers.get(0).getAge()).isEqualTo(35);
        Assertions.assertThat(teachers.get(0).getGender()).isEqualTo("Female");
        Assertions.assertThat(teachers.get(0).getDepartment()).isEqualTo("Math");
    }

    @Test
    public void testAddTeacher() throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        controller.usernameField.setText("T123");
        controller.nameField.setText("Jane Smith");
        controller.ageField.setText("35");
        controller.genderComboBox.setValue("Female");
        controller.departmentField.setText("Math");
        controller.passwordField.setText("password");
        controller.positionComboBox.setValue("Junior");
        controller.addTeacher();

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    public void testAddTeacherMissingFields() {
        controller.usernameField.setText("");
        controller.nameField.setText("Jane Smith");
        controller.ageField.setText("35");
        controller.genderComboBox.setValue("Female");
        controller.departmentField.setText("Math");
        controller.passwordField.setText("password");
        controller.positionComboBox.setValue("Junior");
        controller.addTeacher();

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddTeacherDatabaseError() throws SQLException {
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException());

        controller.usernameField.setText("T123");
        controller.nameField.setText("Jane Smith");
        controller.ageField.setText("35");
        controller.genderComboBox.setValue("Female");
        controller.departmentField.setText("Math");
        controller.passwordField.setText("password");
        controller.positionComboBox.setValue("Junior");
        controller.addTeacher();

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testUpdateTeacher() throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith", "Female", 35, "Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        controller.usernameField.setText("T123");
        controller.nameField.setText("Janet Smith");
        controller.ageField.setText("36");
        controller.genderComboBox.setValue("Female");
        controller.departmentField.setText("Physics");
        controller.passwordField.setText("newpassword");
        controller.positionComboBox.setValue("Senior");
        controller.updateTeacher();

        Assertions.assertThat(teacher.getUsername()).isEqualTo("T123");
        Assertions.assertThat(teacher.getName()).isEqualTo("Janet Smith");
        Assertions.assertThat(teacher.getAge()).isEqualTo(36);
        Assertions.assertThat(teacher.getGender()).isEqualTo("Female");
        Assertions.assertThat(teacher.getDepartment()).isEqualTo("Physics");
        Assertions.assertThat(teacher.getPassword()).isEqualTo("newpassword");
        Assertions.assertThat(teacher.getPosition()).isEqualTo("Senior");
    }

    @Test
    public void testUpdateTeacherMissingFields() {
        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith", "Female", 35, "Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        controller.usernameField.setText("");
        controller.nameField.setText("Janet Smith");
        controller.ageField.setText("36");
        controller.genderComboBox.setValue("Female");
        controller.departmentField.setText("Physics");
        controller.passwordField.setText("newpassword");
        controller.positionComboBox.setValue("Senior");
        controller.updateTeacher();

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Could not update teacher.");
    }

    @Test
    public void testUpdateTeacherDatabaseError() throws SQLException {
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException());

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith", "Female", 35, "Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        controller.usernameField.setText("T123");
        controller.nameField.setText("Janet Smith");
        controller.ageField.setText("36");
        controller.genderComboBox.setValue("Female");
        controller.departmentField.setText("Physics");
        controller.passwordField.setText("newpassword");
        controller.positionComboBox.setValue("Senior");
        controller.updateTeacher();

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Could not update teacher.");
    }

    @Test
    public void testDeleteTeacher() throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith", "Female", 35, "Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        controller.deleteTeacher();

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).isEmpty();
    }

    @Test
    public void testDeleteTeacherNoSelection() {
        controller.deleteTeacher();

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No teacher selected.");
    }

    @Test
    public void testDeleteTeacherDatabaseError() throws SQLException {
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException());

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith", "Female", 35, "Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        controller.deleteTeacher();

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Could not delete teacher.");
    }
}