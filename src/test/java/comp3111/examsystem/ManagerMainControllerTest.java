package comp3111.examsystem;

import comp3111.examsystem.controller.ManagerMainController;
import comp3111.examsystem.model.ManagerControllerModel;
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
public class ManagerMainControllerTest {

    private ManagerMainController mockController;
    private ManagerControllerModel mockModel;

    @Start
    private void start(Stage stage) {
        mockController = mock(ManagerMainController.class);
        mockModel = mock(ManagerControllerModel.class);
        mockController.setDataModel(mockModel);

        Button studentButton = new Button("Student Management");
        studentButton.setId("studentButton");
        studentButton.setOnAction(event->mockController.openStudentManageUI());

        Button teacherButton = new Button("Teacher Management");
        teacherButton.setId("teacherButton");
        teacherButton.setOnAction(event->mockController.openTeacherManageUI());

        Button courseButton = new Button("Course Management");
        courseButton.setId("courseButton");
        courseButton.setOnAction(event->mockController.openCourseManageUI());

        Button logoutButton = new Button("Logout");
        logoutButton.setId("logoutButton");
        logoutButton.setOnAction(mockController::logout);

        VBox vbox = new VBox(studentButton, teacherButton, courseButton, logoutButton);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        // Any setup before each test can be done here
    }

    @Test
    public void testOpenStudentManageUI(FxRobot robot) {
        robot.clickOn("#studentButton");
        verify(mockController).openStudentManageUI();
    }

    @Test
    public void testOpenTeacherManageUI(FxRobot robot) {
        robot.clickOn("#teacherButton");
        verify(mockController).openTeacherManageUI();
    }

    @Test
    public void testOpenCourseManageUI(FxRobot robot) {
        robot.clickOn("#courseButton");
        verify(mockController).openCourseManageUI();
    }


}