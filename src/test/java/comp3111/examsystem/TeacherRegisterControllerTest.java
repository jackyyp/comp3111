package comp3111.examsystem;
import comp3111.examsystem.controller.StudentManagementController;
import comp3111.examsystem.controller.TeacherManagementController;
import comp3111.examsystem.model.TeacherControllerModel;
import javafx.application.Platform;
import comp3111.examsystem.controller.TeacherRegisterController;
import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.testfx.api.FxToolkit;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class TeacherRegisterControllerTest {

    private TeacherRegisterController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/comp3111/examsystem/TeacherRegisterPageUI.fxml"));
        VBox vbox = loader.load();
        controller = loader.getController();
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();

        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        DatabaseConnection.setMockConnection(mockConn);

        // Initialize ComboBox items
        controller.genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        controller.positionComboBox.setItems(FXCollections.observableArrayList("Junior", "Senior", "Parttime"));
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
        controller.usernameTxt.clear();
        controller.nameTxt.clear();
        controller.genderComboBox.getSelectionModel().clearSelection();
        controller.positionComboBox.getSelectionModel().clearSelection();
        controller.ageTxt.clear();
        controller.departmentTxt.clear();
        controller.passwordTxt.clear();
        controller.confirmPasswordTxt.clear();
        controller.errorMessageLbl.setVisible(false);
    }
    @Test
    public void testSuccessfulRegistration(FxRobot robot) throws SQLException, InterruptedException {
        // Clear the username text field before the test
        robot.interact(() -> controller.usernameTxt.clear());

        when(mockRs.next()).thenReturn(false); // No existing user
        when(mockPstmt.executeUpdate()).thenReturn(1);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.usernameTxt.setText("newteacher");
                controller.nameTxt.setText("NewTeacher");
                controller.ageTxt.setText("20");
                controller.positionComboBox.setValue("Junior");
                controller.genderComboBox.setValue("Male");
                controller.departmentTxt.setText("CS");
                controller.passwordTxt.setText("newpassword");
                controller.confirmPasswordTxt.setText("newpassword");
                controller.register();
                Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Add Successful!");
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
    }
    @Test
    public void testFailedRegistration(FxRobot robot) throws SQLException,InterruptedException {
        // Clear the username text field before the test
        robot.interact(() -> controller.usernameTxt.clear());

        when(mockRs.next()).thenReturn(false); // No existing user
        when(mockPstmt.executeUpdate()).thenReturn(1);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.usernameTxt.setText("newteacher");
                controller.nameTxt.setText("NewTeacher");
                controller.ageTxt.setText("A");
                controller.positionComboBox.setValue("Junior");
                controller.genderComboBox.setValue("Male");
                controller.departmentTxt.setText("CS");
                controller.passwordTxt.setText("newpassword");
                controller.confirmPasswordTxt.setText("newpassword");
                controller.register();
                Assertions.assertThat(controller.errorMessageLbl.isVisible()).isTrue();
                Assertions.assertThat(controller.errorMessageLbl.getText()).isEqualTo("Error: Please check your inputs.");
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the Platform.runLater to complete
    }
}