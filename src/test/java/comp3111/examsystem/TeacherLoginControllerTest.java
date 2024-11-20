package comp3111.examsystem;
import comp3111.examsystem.controller.TeacherRegisterController;
import comp3111.examsystem.controller.TeacherLoginController;
import comp3111.examsystem.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.scene.control.Label;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.control.Button;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import javafx.scene.layout.GridPane;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class TeacherLoginControllerTest {

    private TeacherLoginController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/comp3111/examsystem/TeacherLoginUI.fxml"));
        VBox vbox = loader.load();
        controller = loader.getController();
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
    public void testSuccessfulLogin(FxRobot robot) throws SQLException, InterruptedException {
        // Assume the database has a user with username "Kennith" and password "1234"
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("username")).thenReturn("teacher");
        when(mockRs.getString("password")).thenReturn("password");

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.usernameTxt.setText("Kennith");
                controller.passwordTxt.setText("1234");
                Button loginButton = new Button();
                // Add the button to the scene graph
                ((GridPane) controller.usernameTxt.getParent()).add(loginButton, 0, 8);
                ActionEvent event = new ActionEvent(loginButton, null);
                controller.login(event);
                Assertions.assertThat(controller.errorMessageLbl.isVisible()).isFalse();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
        WaitForAsyncUtils.waitForFxEvents(); // Ensure all JavaFX events are processed
    }

    @Test
    public void testFailedLogin(FxRobot robot) throws SQLException, InterruptedException {
        when(mockRs.next()).thenReturn(false);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                robot.clickOn("#usernameTxt").write("wronguser");
                robot.clickOn("#passwordTxt").write("wrongpass");
                robot.clickOn("Login");
                Assertions.assertThat(controller.errorMessageLbl.isVisible()).isTrue();
                Assertions.assertThat(controller.errorMessageLbl).hasText("Error: Please check your inputs.");
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
    }

    @Test
    public void testOpenRegisterPage(FxRobot robot) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.register();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(robot.window("Register for Examination Management System")).isShowing();
    }
}