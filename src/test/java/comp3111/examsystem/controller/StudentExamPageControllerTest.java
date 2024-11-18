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
public class StudentExamPageControllerTest {

    private StudentExamPageController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;

    @Start
    private void start(Stage stage) {
        controller = new StudentExamPageController();
        controller.examNameLabel = new Label();
        controller.questionNumberLabel = new Label();
        controller.countdownLabel = new Label();
        controller.questionsContainer = new VBox();
        controller.previousButton = new Button("Previous");
        controller.nextButton = new Button("Next");
        controller.submitButton = new Button("Submit");
        controller.questionListView = new ListView<>();

        VBox vbox = new VBox(controller.examNameLabel, controller.questionNumberLabel, controller.countdownLabel, controller.questionsContainer, controller.previousButton, controller.nextButton, controller.submitButton, controller.questionListView);
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
    public void testLoadQuestions(FxRobot robot) throws SQLException {

    }

    @Test
    public void testShowPreviousQuestion(FxRobot robot) {
        // Add logic to test showing the previous question
    }

    @Test
    public void testShowNextQuestion(FxRobot robot) {
        // Add logic to test showing the next question
    }

    @Test
    public void testSubmitExam(FxRobot robot) {
        // Add logic to test submitting the exam
    }
}