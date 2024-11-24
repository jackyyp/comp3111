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

        // Initialize TableColumns
        controller.usernameColumn = new TableColumn<>("Username");
        controller.nameColumn = new TableColumn<>("Name");
        controller.ageColumn = new TableColumn<>("Age");
        controller.genderColumn = new TableColumn<>("Gender");
        controller.departmentColumn = new TableColumn<>("Department");
        controller.passwordColumn = new TableColumn<>("Password");
        controller.positionColumn = new TableColumn<>("Position");

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
    public void testFilterTeachersWithEmptyFilters(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(false);

        robot.interact(() -> controller.filterTeachers());

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).isEmpty();
    }

    @Test
    public void testFilterTeachersWithNonEmptyFilters(FxRobot robot) throws SQLException {
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

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    public void testDeleteTeacherWithSelection(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith",  "Female", 35,"Junior","Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        robot.interact(() -> controller.deleteTeacher());

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).isEmpty();
    }

    @Test
    public void testDeleteTeacherWithNoSelection(FxRobot robot) {
        robot.interact(() -> controller.deleteTeacher());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No teacher selected.");
    }

    @Test
    public void testAddTeacherWithAllFieldsFilled(FxRobot robot) throws SQLException {
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

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    public void testAddTeacherWithEmptyFields(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("");
            controller.nameField.setText("Jane Smith");
            controller.ageField.setText("35");
            controller.genderComboBox.setValue("Female");
            controller.departmentField.setText("Math");
            controller.passwordField.setText("password");
            controller.positionComboBox.setValue("Junior");
            controller.addTeacher();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddTeacherWithExistingUsername(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);

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

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Username already exists.");
    }

    @Test
    public void testUpdateTeacherWithSelectionAndAllFieldsUpdated(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith",  "Female", 35,"Junior","Math", "password");        controller.teacherTable.getItems().add(teacher);
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
    public void testUpdateTeacherWithSelectionAndNoFieldsUpdated(FxRobot robot) {
        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith",  "Female", 35,"Junior","Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        robot.interact(() -> controller.updateTeacher());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: No fields to update.");
    }

    @Test
    public void testUpdateTeacherWithNoSelection(FxRobot robot) {
        robot.interact(() -> controller.updateTeacher());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No teacher selected.");
    }

    @Test
    public void testLoadTeachersFromDatabase(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("T123");
        when(mockRs.getString("name")).thenReturn("Jane Smith");
        when(mockRs.getInt("age")).thenReturn(35);
        when(mockRs.getString("gender")).thenReturn("Female");
        when(mockRs.getString("department")).thenReturn("Math");
        when(mockRs.getString("password")).thenReturn("password");
        when(mockRs.getString("position")).thenReturn("Junior");

        robot.interact(() -> controller.loadTeachersFromDatabase());

        ObservableList<TeacherManagementController.Teacher> teachers = controller.teacherTable.getItems();
        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getUsername()).isEqualTo("T123");
        Assertions.assertThat(teachers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    public void testFilterTeachersDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        robot.interact(() -> controller.filterTeachers());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testDeleteTeacherDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith",  "Female", 35,"Junior","Math", "password");        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        robot.interact(() -> controller.deleteTeacher());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testAddTeacherDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

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

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testUpdateTeacherDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith",  "Female", 35,"Junior","Math", "password");        controller.teacherTable.getItems().add(teacher);
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

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testInitialize(FxRobot robot) {
        robot.interact(() -> controller.initialize());

        ObservableList<String> expectedGenders = FXCollections.observableArrayList("Male", "Female", "Other");
        ObservableList<String> expectedPositions = FXCollections.observableArrayList("Junior", "Senior", "Parttime");

        Assertions.assertThat(controller.genderComboBox.getItems()).containsExactlyElementsOf(expectedGenders);
        Assertions.assertThat(controller.positionComboBox.getItems()).containsExactlyElementsOf(expectedPositions);
        Assertions.assertThat(controller.teacherTable.getItems()).isEmpty();
    }
    @Test
    public void testResetFilter(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameFilter.setText("T123");
            controller.nameFilter.setText("Jane Smith");
            controller.departmentFilter.setText("Math");
            controller.resetFilter();
        });

        Assertions.assertThat(controller.usernameFilter).hasText("");
        Assertions.assertThat(controller.nameFilter).hasText("");
        Assertions.assertThat(controller.departmentFilter).hasText("");
    }
    @Test
    public void testAddTeacherWithMissingFields(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("");
            controller.nameField.setText("Jane Smith");
            controller.ageField.setText("35");
            controller.genderComboBox.setValue("Female");
            controller.departmentField.setText("Math");
            controller.passwordField.setText("password");
            controller.positionComboBox.setValue("Junior");
            controller.addTeacher();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }
    @Test
    public void testUpdateTeacherWithDuplicateUsername(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);

        TeacherManagementController.Teacher teacher = new TeacherManagementController.Teacher("T123", "Jane Smith", "Female", 35, "Junior", "Math", "password");
        controller.teacherTable.getItems().add(teacher);
        controller.teacherTable.getSelectionModel().select(teacher);

        robot.interact(() -> {
            controller.usernameField.setText("T124");
            controller.nameField.setText("Janet Smith");
            controller.ageField.setText("36");
            controller.genderComboBox.setValue("Female");
            controller.departmentField.setText("Physics");
            controller.passwordField.setText("newpassword");
            controller.positionComboBox.setValue("Senior");
            controller.updateTeacher();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Duplicate username with other teacher.");
    }


}