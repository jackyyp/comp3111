package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
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
public class TeacherRegisterControllerTest {

    private TeacherRegisterController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        controller = new TeacherRegisterController();
        controller.usernameTxt = new TextField();
        controller.nameTxt = new TextField();
        controller.genderComboBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        controller.positionComboBox = new ComboBox<>(FXCollections.observableArrayList("Junior", "Senior", "Parttime"));
        controller.ageTxt = new TextField();
        controller.departmentTxt = new TextField();
        controller.passwordTxt = new PasswordField();
        controller.confirmPasswordTxt = new PasswordField();
        controller.errorMessageLbl = new Label();
        Button registerButton = new Button("Register");
        registerButton.setId("registerButton");

        VBox vbox = new VBox(controller.usernameTxt, controller.nameTxt, controller.genderComboBox, controller.positionComboBox, controller.ageTxt, controller.departmentTxt, controller.passwordTxt, controller.confirmPasswordTxt, registerButton, controller.errorMessageLbl);
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
    public void testRegisterWithMismatchedPasswords(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("differentPassword");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testRegisterWithInvalidInputs(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("");
        robot.clickOn(controller.nameTxt).write("");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("-1");
        robot.clickOn(controller.departmentTxt).write("");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("differentPassword");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testRegisterWithNegativeAge(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);

        robot.interact(() -> {
            controller.usernameTxt.setText("teacher1");
            controller.nameTxt.setText("Jane Doe");
            controller.genderComboBox.setValue("Female");
            controller.positionComboBox.setValue("Junior");
            controller.ageTxt.setText("-1");
            controller.departmentTxt.setText("Math");
            controller.passwordTxt.setText("password");
            controller.confirmPasswordTxt.setText("password");
            controller.register();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
    }

    @Test
    public void testSuccessfulRegister(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);

        robot.interact(() -> {
            controller.usernameTxt.setText("teacher1");
            controller.nameTxt.setText("Jane Doe");
            controller.genderComboBox.setValue("Female");
            controller.positionComboBox.setValue("Junior");
            controller.ageTxt.setText("30");
            controller.departmentTxt.setText("Math");
            controller.passwordTxt.setText("password");
            controller.confirmPasswordTxt.setText("password");

            controller.register();
        });

        Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Add Successful!");
    }

    @Test
    public void testInitialize(FxRobot robot) {
        robot.interact(() -> controller.initialize());

        Assertions.assertThat(controller.usernameTxt.getText()).isEmpty();
        Assertions.assertThat(controller.nameTxt.getText()).isEmpty();
        Assertions.assertThat(controller.genderComboBox.getItems()).containsExactly("Male", "Female", "Other");
        Assertions.assertThat(controller.positionComboBox.getItems()).containsExactly("Junior", "Senior", "Parttime");
        Assertions.assertThat(controller.ageTxt.getText()).isEmpty();
        Assertions.assertThat(controller.departmentTxt.getText()).isEmpty();
        Assertions.assertThat(controller.passwordTxt.getText()).isEmpty();
        Assertions.assertThat(controller.confirmPasswordTxt.getText()).isEmpty();
    }

    @Test
    public void testNonNumberAgeInput(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("thirty");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testMissingUserName(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testMissingName(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testMissingAge(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testMissingDepartment(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testMissingPassword(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testAlreadyExistUsername(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);

        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Username already exists.");
    }

    @Test
    public void testErrorConnectingToDatabase(FxRobot robot) throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        robot.clickOn(controller.usernameTxt).write("T123");
        robot.clickOn(controller.nameTxt).write("Jane Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Female");
        robot.clickOn(controller.positionComboBox).clickOn("Junior");
        robot.clickOn(controller.ageTxt).write("30");
        robot.clickOn(controller.departmentTxt).write("Math");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.interact(() -> controller.register());

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error connecting to the database.");
    }
}