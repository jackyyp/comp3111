package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class StudentMainControllerTest {

    private StudentMainController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;
    private StudentControllerModel mockDataModel;

    @Start
    private void start(Stage stage) {
        controller = new StudentMainController();
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        mockDataModel = mock(StudentControllerModel.class);
        controller.errorLabel = new Label();
        controller.examCombox = new ComboBox<>();

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        DatabaseConnection.setMockConnection(mockConn);
        controller.setDataModel(mockDataModel);
    }

    @Test
    public void testLoadExamsWithExams() throws SQLException {
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getString("course")).thenReturn("Course1");
        when(mockRs.getString("name")).thenReturn("Exam1");
        when(mockRs.getInt("id")).thenReturn(1);

        controller.loadExams();
        Assertions.assertThat(controller.getExamCombox().getItems()).isNotEmpty();
        Assertions.assertThat(controller.getExamCombox().getItems().get(0)).isEqualTo("Course1 | Exam1");
    }

}