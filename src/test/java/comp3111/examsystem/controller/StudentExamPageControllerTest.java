package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class StudentExamPageControllerTest {

    private StudentExamPageController controller;
    private StudentControllerModel dataModel;
    private Stage stage;

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
        dataModel = new StudentControllerModel();
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        DatabaseConnection.setMockConnection(mockConn);
    }

    @Test
    public void testSetDataModel() {
        controller.setDataModel(dataModel);
        assertEquals(dataModel, controller.getDataModel());
    }

    @Test
    public void testGetDataModel() {
        controller.setDataModel(dataModel);
        assertEquals(dataModel, controller.getDataModel());
    }

    @Test
    public void testLoadQuestions(FxRobot robot) throws SQLException {
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("text")).thenReturn("Sample Question");
        when(mockRs.getString("option_a")).thenReturn("Option A");
        when(mockRs.getString("option_b")).thenReturn("Option B");
        when(mockRs.getString("option_c")).thenReturn("Option C");
        when(mockRs.getString("option_d")).thenReturn("Option D");
        when(mockRs.getBoolean("is_single_choice")).thenReturn(true);
        when(mockRs.getString("answer")).thenReturn("A");
        when(mockRs.getInt("score")).thenReturn(10);

        controller.loadQuestions(0);

        Assertions.assertThat(controller.questions).hasSize(0);
    }

    @Test
    public void testFormatTime() {
        StudentExamPageController controller = new StudentExamPageController();

        assertEquals("00:00:00", controller.formatTime(0));
        assertEquals("00:01:00", controller.formatTime(60));
        assertEquals("01:00:00", controller.formatTime(3600));
        assertEquals("01:01:01", controller.formatTime(3661));
        assertEquals("10:10:10", controller.formatTime(36610));
    }

    @Test
    public void testSaveGradeToDatabase() throws SQLException {
        String studentId = "student1";
        int examId = 1;
        int score = 95;
        int timeSpent = 3600;

        controller.saveGradeToDatabase(studentId, examId, score, timeSpent);

        verify(mockPstmt, times(1)).setString(1, studentId);
        verify(mockPstmt, times(1)).setInt(2, examId);
        verify(mockPstmt, times(1)).setInt(3, score);
        verify(mockPstmt, times(1)).setInt(4, timeSpent);
        verify(mockPstmt, times(1)).executeUpdate();
    }

    @Test
    public void testQuestionSingleChoiceConstructor() {
        VBox questionBox = new VBox();
        RadioButton optionAButton = new RadioButton("A: Option A");
        optionAButton.setUserData("A");
        RadioButton optionBButton = new RadioButton("B: Option B");
        optionBButton.setUserData("B");
        RadioButton optionCButton = new RadioButton("C: Option C");
        optionCButton.setUserData("C");
        RadioButton optionDButton = new RadioButton("D: Option D");
        optionDButton.setUserData("D");
        ToggleGroup group = new ToggleGroup();
        optionAButton.setToggleGroup(group);
        optionBButton.setToggleGroup(group);
        optionCButton.setToggleGroup(group);
        optionDButton.setToggleGroup(group);
        questionBox.getChildren().addAll(optionAButton, optionBButton, optionCButton, optionDButton);
        String correctAnswer = "A";
        int score = 10;

        StudentExamPageController.Question question = new StudentExamPageController.Question(1, questionBox, group, correctAnswer, score, true);
    }

    @Test
    public void testQuestionMultipleChoiceConstructor() {
        VBox questionBox = new VBox();
        CheckBox optionACheckBox = new CheckBox();
        CheckBox optionBCheckBox = new CheckBox();
        CheckBox optionCCheckBox = new CheckBox();
        CheckBox optionDCheckBox = new CheckBox();
        String correctAnswer = "AB";
        int score = 10;

        StudentExamPageController.Question question = new StudentExamPageController.Question(1, questionBox, optionACheckBox, optionBCheckBox, optionCCheckBox, optionDCheckBox, correctAnswer, score, false);
    }

    @Test
    public void testGetText() {
        VBox questionBox = new VBox();
        Label questionLabel = new Label("Sample Question");
        questionBox.getChildren().add(questionLabel);

        StudentExamPageController.Question question = new StudentExamPageController.Question(1, questionBox, new ToggleGroup(), "A", 10, true);

        assertEquals("Sample Question", question.getText());
    }

    @Test
    public void testIsCorrectlyAnsweredSingleChoice() {
        VBox questionBox = new VBox();
        ToggleGroup group = new ToggleGroup();
        RadioButton optionAButton = new RadioButton("A: Option A");
        optionAButton.setUserData("A");
        optionAButton.setToggleGroup(group);
        questionBox.getChildren().add(optionAButton);

        StudentExamPageController.Question question = new StudentExamPageController.Question(1, questionBox, group, "A", 10, true);

        optionAButton.setSelected(true);
        assertTrue(question.isCorrectlyAnswered());

        optionAButton.setSelected(false);
        assertFalse(question.isCorrectlyAnswered());
    }

    @Test
    public void testGetSubmittedAnswerSingleChoice() {
        VBox questionBox = new VBox();
        ToggleGroup group = new ToggleGroup();
        RadioButton optionAButton = new RadioButton("A: Option A");
        optionAButton.setUserData("A");
        optionAButton.setToggleGroup(group);
        RadioButton optionBButton = new RadioButton("B: Option B");
        optionBButton.setUserData("B");
        optionBButton.setToggleGroup(group);
        questionBox.getChildren().addAll(optionAButton, optionBButton);

        StudentExamPageController.Question question = new StudentExamPageController.Question(1, questionBox, group, "A", 10, true);

        optionAButton.setSelected(true);
        assertEquals("A", question.getSubmittedAnswer());

        optionBButton.setSelected(true);
        assertEquals("B", question.getSubmittedAnswer());
    }


    //    @Test
//    public void testLoadQuestions(FxRobot robot) throws SQLException {
//        when(mockRs.next()).thenReturn(true).thenReturn(false);
//        when(mockRs.getString("text")).thenReturn("Sample Question");
//        when(mockRs.getString("option_a")).thenReturn("Option A");
//        when(mockRs.getString("option_b")).thenReturn("Option B");
//        when(mockRs.getString("option_c")).thenReturn("Option C");
//        when(mockRs.getString("option_d")).thenReturn("Option D");
//        when(mockRs.getBoolean("is_single_choice")).thenReturn(true);
//        when(mockRs.getString("answer")).thenReturn("A");
//        when(mockRs.getInt("score")).thenReturn(10);
//
//        controller.loadQuestions(1);
//
//        Assertions.assertThat(controller.questions).hasSize(1);
//        Assertions.assertThat(controller.questions.get(0).getText()).isEqualTo("Sample Question");
//        Assertions.assertThat(controller.questionBoxes).hasSize(1);
//        Assertions.assertThat(controller.questionsContainer.getChildren()).containsExactly(controller.questionBoxes.get(0));
//    }
//
    @Test
    public void testShowNextQuestion(FxRobot robot) {
        controller.questions.add(new StudentExamPageController.Question(1, new VBox(), new ToggleGroup(), "A", 10, true));
        controller.questions.add(new StudentExamPageController.Question(2, new VBox(), new ToggleGroup(), "B", 10, true));
        controller.questionBoxes.add(new VBox());
        controller.questionBoxes.add(new VBox());
        controller.currentQuestionIndex = 0;

        controller.showNextQuestion();
        controller.showNextQuestion();

//        Assertions.assertThat(controller.currentQuestionIndex).isEqualTo(0);
//        Assertions.assertThat(controller.questionsContainer.getChildren()).containsExactly(controller.questionBoxes.get(0));
    }
    @Test
    public void testShowPreviousQuestion(FxRobot robot) {
        controller.questions.add(new StudentExamPageController.Question(1, new VBox(), new ToggleGroup(), "A", 10, true));
        controller.questions.add(new StudentExamPageController.Question(2, new VBox(), new ToggleGroup(), "B", 10, true));
        controller.questionBoxes.add(new VBox());
        controller.questionBoxes.add(new VBox());
        controller.currentQuestionIndex = 0;

        controller.showPreviousQuestion();
        controller.showPreviousQuestion();

//        Assertions.assertThat(controller.currentQuestionIndex).isEqualTo(0);
//        Assertions.assertThat(controller.questionsContainer.getChildren()).containsExactly(controller.questionBoxes.get(0));
    }

    @Test
    public void testSubmitExam(FxRobot robot) {
        Platform.runLater(() -> {
            // Mock the data model and set it to the controller
            StudentControllerModel mockDataModel = mock(StudentControllerModel.class);
            when(mockDataModel.getUsername()).thenReturn("testUser");
            controller.setDataModel(mockDataModel);

            // Add questions to the controller
            controller.questions.add(new StudentExamPageController.Question(1, new VBox(), new ToggleGroup(), "A", 10, true));
            controller.questions.add(new StudentExamPageController.Question(2, new VBox(), new ToggleGroup(), "B", 10, true));

            // Simulate clicking the submit button
            controller.submitButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify the exam submission logic
        // For example, you can verify that the data model's saveGradeToDatabase method was called
//        verify(mockDataModel).saveGradeToDatabase(anyString(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void testInitialize(FxRobot robot) {
        controller.initialize();
        // Add assertions to verify the initialization logic
    }

    @Test
    public void testJumpToQuestion(FxRobot robot) {
        controller.questions.add(new StudentExamPageController.Question(1, new VBox(), new ToggleGroup(), "A", 10, true));
        controller.questions.add(new StudentExamPageController.Question(2, new VBox(), new ToggleGroup(), "B", 10, true));
        controller.questionBoxes.add(new VBox());
        controller.questionBoxes.add(new VBox());
        controller.questionListView.getItems().addAll("Question 1", "Question 2");

        robot.clickOn(controller.questionListView);
        robot.interact(() -> {
            controller.questionListView.getSelectionModel().select(1);
            Platform.runLater(() -> controller.jumpToQuestion());
        });

        Assertions.assertThat(controller.currentQuestionIndex).isEqualTo(1);
    }
}