package comp3111.examsystem;

import comp3111.examsystem.controller.TeacherQuestionController;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.Question;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class TeacherQuestionControllerTest {

    private TeacherQuestionController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private PreparedStatement mockCheckStmt;
    private ResultSet mockRs;
    private ResultSet mockGeneratedKeys;

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/comp3111/examsystem/TeacherQuestion.fxml"));
        AnchorPane anchorPane = loader.load();
        controller = loader.getController();
        Scene scene = new Scene(anchorPane);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockCheckStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        mockGeneratedKeys = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPstmt);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockCheckStmt);
        when(mockPstmt.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);

        // Mock the ResultSet for the loadQuestionsFromDatabase method
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false); // Simulate one row in the ResultSet
        when(mockRs.getInt("id")).thenReturn(1);
        when(mockRs.getString("text")).thenReturn("What is Java?");
        when(mockRs.getString("option_a")).thenReturn("A programming language");
        when(mockRs.getString("option_b")).thenReturn("A coffee");
        when(mockRs.getString("option_c")).thenReturn("An island");
        when(mockRs.getString("option_d")).thenReturn("A car");
        when(mockRs.getString("answer")).thenReturn("A");
        when(mockRs.getBoolean("is_single_choice")).thenReturn(true);
        when(mockRs.getInt("score")).thenReturn(1);

        DatabaseConnection.setMockConnection(mockConn);

        // Clear the question table before each test
        if (controller != null && controller.questionTable != null) {
            controller.questionTable.getItems().clear();
        }
    }

    @Test
    public void testAddQuestion(FxRobot robot) throws SQLException {
        // Clear the question table before the test
        robot.interact(() -> controller.questionTable.getItems().clear());

        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);// Ensure no existing question is found
        ObservableList<Question> questions = controller.questionTable.getItems();
        int prev_size = questions.size();
        robot.interact(() -> {
            controller.editQuestionField.setText("What is Java?");
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Single");
            controller.editAnswerField.setText("A");
            controller.editScoreField.setText("1");
            controller.handleAdd();
        });

        questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(prev_size+1);
    }

    @Test
    public void testFailedAddQuestion(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException());

        robot.interact(() -> {
            controller.editQuestionField.setText("What is Java?");
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Multiple");
            controller.editAnswerField.setText("A");
            controller.handleAdd();
        });

        Assertions.assertThat(controller.errorLabel.isVisible()).isTrue();
        Assertions.assertThat(controller.errorLabel).hasText("All fields must be filled out.");
    }

    @Test
    public void testDeleteQuestion(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenReturn(1);
        ObservableList<Question> prev_content = controller.questionTable.getItems();

        // Add a question to the table
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));

        // Delete the selected question
        robot.interact(() -> controller.handleDelete(null));

        // Verify that the table is empty
        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).isEqualTo(prev_content);
    }
}