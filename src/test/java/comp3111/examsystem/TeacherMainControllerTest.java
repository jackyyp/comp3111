package comp3111.examsystem;
import javafx.event.ActionEvent;
import comp3111.examsystem.controller.TeacherMainController;
import javafx.application.Platform;
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
import org.testfx.util.WaitForAsyncUtils;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
public class TeacherMainControllerTest {

    private TeacherMainController controller;


    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/comp3111/examsystem/TeacherMainUI.fxml"));
        VBox vbox = loader.load();
        controller = loader.getController();
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        // Initialize any necessary data or mocks here
    }

    @Test
    public void testOpenQuestionManageUI(FxRobot robot) throws TimeoutException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openQuestionManageUI();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(robot.window("Question Management")).isShowing();
    }

    @Test
    public void testOpenExamManageUI(FxRobot robot) throws TimeoutException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openExamManageUI();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(robot.window("Exam Management")).isShowing();
    }

    @Test
    public void testOpenGradeStatistic(FxRobot robot) throws TimeoutException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.openGradeStatistic();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(robot.window("Grade Statistics")).isShowing();
    }

    @Test
    public void testLogout(FxRobot robot) throws TimeoutException {
        Platform.runLater(() -> {
            // Simulate the ActionEvent
            ActionEvent event = new ActionEvent(controller,null);
            controller.logout(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(robot.window("Login")).isShowing();
    }
}