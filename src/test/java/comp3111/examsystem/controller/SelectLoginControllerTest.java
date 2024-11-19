package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class SelectLoginControllerTest {

    private SelectLoginController controller;

    @Start
    private void start(Stage stage) throws Exception {
        controller = new SelectLoginController();
        Button studentLoginButton = new Button("Student Login");
        studentLoginButton.setId("studentLoginButton");
        studentLoginButton.setOnAction(controller::studentLogin);

        Button teacherLoginButton = new Button("Teacher Login");
        teacherLoginButton.setId("teacherLoginButton");
        teacherLoginButton.setOnAction(controller::teacherLogin);

        Button managerLoginButton = new Button("Manager Login");
        managerLoginButton.setId("managerLoginButton");
        managerLoginButton.setOnAction(controller::managerLogin);

        Scene scene = new Scene(new VBox(studentLoginButton, teacherLoginButton, managerLoginButton));
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void studentLogin(FxRobot robot) {
        robot.clickOn("#studentLoginButton");
        // Add assertions to verify the student login screen is displayed
    }

    @Test
    public void teacherLogin(FxRobot robot) {
        robot.clickOn("#teacherLoginButton");
        // Add assertions to verify the teacher login screen is displayed
    }

    @Test
    public void managerLogin(FxRobot robot) {
        robot.clickOn("#managerLoginButton");
        // Add assertions to verify the manager login screen is displayed
    }
}