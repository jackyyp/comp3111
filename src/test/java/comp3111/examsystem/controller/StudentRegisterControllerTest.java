package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class StudentRegisterControllerTest {

    private StudentRegisterController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;

    @Start
    private void start(Stage stage) {
        controller = new StudentRegisterController();
        controller.usernameTxt = new TextField();
        controller.nameTxt = new TextField();
        controller.genderComboBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        controller.ageTxt = new TextField();
        controller.departmentTxt = new TextField();
        controller.passwordTxt = new PasswordField();
        controller.confirmPasswordTxt = new PasswordField();
        controller.errorMessageLbl = new Label();
        Button registerButton = new Button("Register");
        registerButton.setId("registerButton");
        registerButton.setOnAction(controller::register);

        VBox vbox = new VBox(controller.usernameTxt, controller.nameTxt, controller.genderComboBox, controller.ageTxt, controller.departmentTxt, controller.passwordTxt, controller.confirmPasswordTxt, registerButton, controller.errorMessageLbl);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        DatabaseConnection.setMockConnection(mockConn);
    }

    @Test
    public void testRegisterWithMismatchedPasswords(FxRobot robot) {

        robot.clickOn(controller.usernameTxt).write("S123");
        robot.clickOn(controller.nameTxt).write("John Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Male");
        robot.clickOn(controller.ageTxt).write("20");
        robot.clickOn(controller.departmentTxt).write("CS");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("differentPassword");
        robot.clickOn("#registerButton");

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }

    @Test
    public void testSuccessfulRegister(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);
        robot.clickOn(controller.usernameTxt).write("S123");
        robot.clickOn(controller.nameTxt).write("John Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Male");
        robot.clickOn(controller.ageTxt).write("20");
        robot.clickOn(controller.departmentTxt).write("CS");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.clickOn("#registerButton");

        Assertions.assertThat(controller.errorMessageLbl).hasText("Registration successful!");

    }

    @Test
    public void testRegisterWithInvalidInputs(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("");
        robot.clickOn(controller.nameTxt).write("");
        robot.clickOn(controller.genderComboBox).clickOn("Male");
        robot.clickOn(controller.ageTxt).write("-1");
        robot.clickOn(controller.departmentTxt).write("");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("differentPassword");
        robot.clickOn("#registerButton");

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }


    @Test
    public void testRegisterWithNegativeAge(FxRobot robot) {
        robot.clickOn(controller.usernameTxt).write("S123");
        robot.clickOn(controller.nameTxt).write("John Doe");
        robot.clickOn(controller.genderComboBox).clickOn("Male");
        robot.clickOn(controller.ageTxt).write("-1");
        robot.clickOn(controller.departmentTxt).write("CS");
        robot.clickOn(controller.passwordTxt).write("password");
        robot.clickOn(controller.confirmPasswordTxt).write("password");
        robot.clickOn("#registerButton");

        Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
    }
}