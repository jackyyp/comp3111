package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Flow;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class StudentMainControllerTest {

    private StudentMainController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;
    private StudentControllerModel mockDataModel;

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/comp3111/examsystem/StudentMainUI.fxml"));
        if (loader.getLocation() == null) {
            throw new IOException("FXML resource not found: /comp3111/examsystem/StudentMainUI.fxml");
        }
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
    public void testLoadExamsWithExams(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getString("course")).thenReturn("Course1");
        when(mockRs.getString("name")).thenReturn("Exam1");
        when(mockRs.getInt("id")).thenReturn(1);

        robot.interact(() -> controller.setDataModel(mockDataModel));
        robot.clickOn("#loadExamsButton");
        Assertions.assertThat(robot.lookup("#examCombox").queryComboBox().getItems()).isNotEmpty();
        Assertions.assertThat(robot.lookup("#examCombox").queryComboBox().getItems().get(0)).isEqualTo("Course1 | Exam1");
    }

    @Test
    public void testLoadExamsNoExams(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(false);

        robot.interact(() -> controller.setDataModel(mockDataModel));
        robot.clickOn("#loadExamsButton");
        Assertions.assertThat(robot.lookup("#examCombox").queryComboBox().getItems()).isEmpty();
        Assertions.assertThat(robot.lookup("#errorLabel").queryLabeled()).hasText("Hooray! You have finished all exams!");
    }
}