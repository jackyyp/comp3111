package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class StudentMainControllerTest {

    private StudentMainController controller;
    private StudentControllerModel mockDataModel;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        Platform.runLater(() -> {
            controller = new StudentMainController();
            controller.examCombox = new ComboBox<>();
            controller.errorLabel = new Label();
            mockDataModel = mock(StudentControllerModel.class);
            when(mockDataModel.getUsername()).thenReturn("testUser");

            VBox vbox = new VBox(controller.examCombox, controller.errorLabel);
            Scene scene = new Scene(vbox);
            stage.setScene(scene);
            stage.show();
        });
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
    public void testSetDataModel() {
        Platform.runLater(() -> {
            controller.setDataModel(mockDataModel);
            Assertions.assertThat(controller.dataModel.getUsername()).isEqualTo("testUser");
        });
    }

    @Test
    public void testLoadExamsWithExams(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getString("course")).thenReturn("Course1");
        when(mockRs.getString("name")).thenReturn("Exam1");
        when(mockRs.getInt("id")).thenReturn(1);

        Platform.runLater(() -> {
            controller.setDataModel(mockDataModel);
            controller.loadExams();

            Assertions.assertThat(controller.examCombox.getItems()).isNotEmpty();
            Assertions.assertThat(controller.examCombox.getItems().get(0)).isEqualTo("Course1 | Exam1");
        });
    }

    @Test
    public void testLoadExamsNoExams(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(false);

        Platform.runLater(() -> {
            controller.setDataModel(mockDataModel);
            controller.loadExams();

            Assertions.assertThat(controller.examCombox.getItems()).isEmpty();
            Assertions.assertThat(controller.errorLabel).hasText("Hooray! You have finished all exams!");
        });
    }


    @Test
    public void testOpenExamUIWithoutSelection(FxRobot robot) {
        Platform.runLater(() -> {
            controller.setDataModel(mockDataModel);
            controller.openExamUI(new ActionEvent());

            Assertions.assertThat(controller.errorLabel).hasText("Please select an exam before starting.");
        });
    }
}