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

        // Initialize TableColumns
        controller.usernameColumn = new TableColumn<>("Username");
        controller.nameColumn = new TableColumn<>("Name");
        controller.ageColumn = new TableColumn<>("Age");
        controller.genderColumn = new TableColumn<>("Gender");
        controller.departmentColumn = new TableColumn<>("Department");
        controller.passwordColumn = new TableColumn<>("Password");

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
    public void testFilterStudentsWithEmptyFilters(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(false);

        robot.interact(() -> controller.filterStudents());

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).isEmpty();
    }

    @Test
    public void testFilterStudentsWithNonEmptyFilters(FxRobot robot) throws SQLException {
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

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getUsername()).isEqualTo("S123");
        Assertions.assertThat(students.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    public void testDeleteStudentWithSelection(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> controller.deleteStudent());

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).isEmpty();
    }

    @Test
    public void testDeleteStudentWithNoSelection(FxRobot robot) {
        robot.interact(() -> controller.deleteStudent());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No student selected.");
    }

    @Test
    public void testAddStudentWithAllFieldsFilled(FxRobot robot) throws SQLException {
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

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getUsername()).isEqualTo("S123");
        Assertions.assertThat(students.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    public void testAddStudentWithEmptyFields(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("");
            controller.nameField.setText("");
            controller.ageField.setText("");
            controller.genderComboBox.setValue(null);
            controller.departmentField.setText("");
            controller.passwordField.setText("");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddStudentWithExistingUsername(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);

        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Username already exists.");
    }

    @Test
    public void testUpdateStudentWithSelectionAndAllFieldsUpdated(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);

        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
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
    public void testUpdateStudentWithSelectionAndNoFieldsUpdated(FxRobot robot) {
        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> controller.updateStudent());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: No fields to update.");
    }

    @Test
    public void testUpdateStudentWithNoSelection(FxRobot robot) {
        robot.interact(() -> controller.updateStudent());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("No student selected.");
    }

    @Test
    public void testLoadStudentsFromDatabase(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("S123");
        when(mockRs.getString("name")).thenReturn("John Doe");
        when(mockRs.getInt("age")).thenReturn(20);
        when(mockRs.getString("gender")).thenReturn("Male");
        when(mockRs.getString("department")).thenReturn("CS");
        when(mockRs.getString("password")).thenReturn("password");

        robot.interact(() -> controller.loadStudentsFromDatabase());

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getUsername()).isEqualTo("S123");
        Assertions.assertThat(students.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    public void testFilterStudentsWithUsernameOnly(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("S123");
        when(mockRs.getString("name")).thenReturn("John Doe");
        when(mockRs.getInt("age")).thenReturn(20);
        when(mockRs.getString("gender")).thenReturn("Male");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> {
            controller.usernameFilter.setText("S123");
            controller.filterStudents();
        });

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getUsername()).isEqualTo("S123");
    }

    @Test
    public void testFilterStudentsWithNameOnly(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("S123");
        when(mockRs.getString("name")).thenReturn("John Doe");
        when(mockRs.getInt("age")).thenReturn(20);
        when(mockRs.getString("gender")).thenReturn("Male");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> {
            controller.nameFilter.setText("John Doe");
            controller.filterStudents();
        });

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    public void testFilterStudentsWithDepartmentOnly(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("username")).thenReturn("S123");
        when(mockRs.getString("name")).thenReturn("John Doe");
        when(mockRs.getInt("age")).thenReturn(20);
        when(mockRs.getString("gender")).thenReturn("Male");
        when(mockRs.getString("department")).thenReturn("CS");

        robot.interact(() -> {
            controller.departmentFilter.setText("CS");
            controller.filterStudents();
        });

        ObservableList<StudentManagementController.Student> students = controller.studentTable.getItems();
        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getDepartment()).isEqualTo("CS");
    }

    @Test
    public void testAddStudentWithNegativeAge(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("-1");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testUpdateStudentWithNegativeAge(FxRobot robot) {
        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> {
            controller.ageField.setText("-1");
            controller.updateStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testUpdateStudentWithEmptyUsername(FxRobot robot) {
        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> {
            controller.usernameField.setText("");
            controller.updateStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: No fields to update.");
    }

    @Test
    public void testUpdateStudentWithDuplicateUsername(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1); // Mock that the username already exists

        StudentManagementController.Student student1 = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
        StudentManagementController.Student student2 = new StudentManagementController.Student("S124", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student1);
        controller.studentTable.getItems().add(student2);
        controller.studentTable.getSelectionModel().select(student2);

        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.updateStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Duplicate username with other student.");
    }

    @Test
    public void testFilterStudentsDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        robot.interact(() -> controller.filterStudents());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testDeleteStudentDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
        controller.studentTable.getItems().add(student);
        controller.studentTable.getSelectionModel().select(student);

        robot.interact(() -> controller.deleteStudent());

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testAddStudentDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testUpdateStudentDatabaseError(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        StudentManagementController.Student student = new StudentManagementController.Student("S123", "John Doe", 20, "Male", "CS", "password");
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

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error connecting to the database.");
    }

    @Test
    public void testAddStudentWithEmptyUsername(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddStudentWithEmptyName(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddStudentWithNullGender(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue(null);
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddStudentWithEmptyAge(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddStudentWithEmptyDepartment(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("");
            controller.passwordField.setText("password");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testAddStudentWithEmptyPassword(FxRobot robot) {
        robot.interact(() -> {
            controller.usernameField.setText("S123");
            controller.nameField.setText("John Doe");
            controller.ageField.setText("20");
            controller.genderComboBox.setValue("Male");
            controller.departmentField.setText("CS");
            controller.passwordField.setText("");
            controller.addStudent();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testResetFilter(FxRobot robot) throws SQLException {
        // Set up initial filter values
        controller.usernameFilter.setText("testUsername");
        controller.nameFilter.setText("testName");
        controller.departmentFilter.setText("testDepartment");

        // Call resetFilter method
        robot.interact(() -> controller.resetFilter());

        // Verify that the filters are cleared
        Assertions.assertThat(controller.usernameFilter.getText()).isEmpty();
        Assertions.assertThat(controller.nameFilter.getText()).isEmpty();
        Assertions.assertThat(controller.departmentFilter.getText()).isEmpty();
    }

    @Test
    public void testInitialize(FxRobot robot) {
        // Call initialize method
        robot.interact(() -> controller.initialize());

        // Verify that the genderComboBox is populated
        ObservableList<String> expectedGenders = FXCollections.observableArrayList("Male", "Female", "Other");
        Assertions.assertThat(controller.genderComboBox.getItems()).isEqualTo(expectedGenders);

        // Verify that the table columns are set up correctly
        Assertions.assertThat(controller.usernameColumn.getCellValueFactory()).isNotNull();
        Assertions.assertThat(controller.nameColumn.getCellValueFactory()).isNotNull();
        Assertions.assertThat(controller.ageColumn.getCellValueFactory()).isNotNull();
        Assertions.assertThat(controller.genderColumn.getCellValueFactory()).isNotNull();
        Assertions.assertThat(controller.departmentColumn.getCellValueFactory()).isNotNull();
        Assertions.assertThat(controller.passwordColumn.getCellValueFactory()).isNotNull();
    }



}