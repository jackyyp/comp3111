package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
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
public class StudentLoginControllerTest {

    private StudentLoginController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        controller = new StudentLoginController();
        controller.usernameTxt = new TextField();
        controller.passwordTxt = new PasswordField();
        controller.errorMessageLbl = new Label();
        Button loginButton = new Button("Login");
        loginButton.setId("loginButton"); // Set the id for the login button
        loginButton.setOnAction(controller::login);

        VBox vbox = new VBox(controller.usernameTxt, controller.passwordTxt, loginButton, controller.errorMessageLbl);
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
    public void testFailedLogin(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(false);

        robot.clickOn(controller.usernameTxt).write("");
        robot.clickOn(controller.passwordTxt).write("");
        robot.clickOn("#loginButton");

        Assertions.assertThat(controller.errorMessageLbl).hasText("");
    }
}