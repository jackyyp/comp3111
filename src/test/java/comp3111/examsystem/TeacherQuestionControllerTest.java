package comp3111.examsystem;

import comp3111.examsystem.controller.ManagerMainController;
import comp3111.examsystem.controller.TeacherQuestionController;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
        controller = new TeacherQuestionController();
// Initialize fields if not set by FXML loader
        controller.questionField =  new TextField();
        if (controller.editQuestionField == null) {
            controller.editQuestionField = new TextField();
        }
        if (controller.editOptionAField == null) {
            controller.editOptionAField = new TextField();
        }
        if (controller.editOptionBField == null) {
            controller.editOptionBField = new TextField();
        }
        if (controller.editOptionCField == null) {
            controller.editOptionCField = new TextField();
        }
        if (controller.editOptionDField == null) {
            controller.editOptionDField = new TextField();
        }
        if (controller.editAnswerField == null) {
            controller.editAnswerField = new TextField();
        }
        if (controller.editTypeComboBox == null) {
            controller.editTypeComboBox = new ComboBox<>();
        }
        if (controller.editScoreField == null) {
            controller.editScoreField = new TextField();
        }
        if (controller.errorLabel == null) {
            controller.errorLabel = new Label();
        }
        if (controller.questionTable == null) {
            controller.questionTable = new TableView<>();
        }
        controller.typeComboBox = new ComboBox<>();
        controller.scoreField = new TextField();

        controller.questionList = FXCollections.observableArrayList();

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
    public void testFailedAddQuestionMissing(FxRobot robot) throws SQLException {
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
    public void testFailedAddQuestionAnsFormat(FxRobot robot) throws SQLException {
        when(mockPstmt.executeUpdate()).thenThrow(new SQLException());

        robot.interact(() -> {
            controller.editQuestionField.setText("What is Java?");
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Multiple");
            controller.editScoreField.setText("1");
            controller.editAnswerField.setText("1234");
            controller.handleAdd();
        });

        Assertions.assertThat(controller.errorLabel.isVisible()).isTrue();
        Assertions.assertThat(controller.errorLabel).hasText("Answer format incorrect");
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
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question deleted successfully.");
    }

    @Test
    public void testHandleFailUpdateType(FxRobot robot) throws SQLException {
        // Clear the question table before the test
        robot.interact(() -> controller.questionTable.getItems().clear());

        // Mock the add statement
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // Ensure no existing question is found

        // Add a question using the handleAdd method
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

        // Verify the question is added

        // Select the added question in the table
        robot.interact(() -> controller.questionTable.getSelectionModel().select(0));

        // Mock the update statement
        when(mockPstmt.executeUpdate()).thenReturn(1);

        // Update the question
        robot.interact(() -> {
            controller.editQuestionField.setText("");
            controller.editOptionAField.setText("");
            controller.editOptionBField.setText("");
            controller.editOptionCField.setText("");
            controller.editOptionDField.setText("");
            controller.editTypeComboBox.setValue(null);
            controller.editAnswerField.setText("");
            controller.editScoreField.setText("");
            controller.editAnswerField.setText("AC");
            controller.handleUpdate();
        });

        // Verify the update
        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(1);
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Answer format incorrect");
    }


    @Test
    public void testHandleFailUpdateDup(FxRobot robot) throws SQLException {
        // Clear the question table before the test
        robot.interact(() -> controller.questionTable.getItems().clear());

        // Mock the add statement
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); //Ensure no existing question is found

        // Add a question using the handleAdd method
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

        // Verify the question is added
        ObservableList<Question> questions = controller.questionTable.getItems();

        // Select the added question in the table
        robot.interact(() -> controller.questionTable.getSelectionModel().select(0));

        // Mock the update statement
        when(mockPstmt.executeUpdate()).thenReturn(1);

        // Update the question
        robot.interact(() -> {
            controller.editOptionAField.setText("");
            controller.editOptionBField.setText("");
            controller.editOptionCField.setText("");
            controller.editOptionDField.setText("");
            controller.editTypeComboBox.setValue(null);
            controller.editAnswerField.setText("");
            controller.editScoreField.setText("");
            controller.editAnswerField.setText("AC");
            controller.editTypeComboBox.setValue("Single");
            controller.handleUpdate();
        });

        // Verify the update
        //questions = controller.questionTable.getItems();
        //Assertions.assertThat(questions).hasSize(1);
        //Assertions.assertThat(questions.get(0).getAnswer()).isEqualTo("B");
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("The question already exists.");
    }


    @Test
    public void testHandleUpdate(FxRobot robot) throws SQLException {
        // Clear the question table before the test
        robot.interact(() -> controller.questionTable.getItems().clear());

        // Mock the add statement
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // Ensure no existing question is found

        // Add a question using the handleAdd method
        /*robot.interact(() -> {
            controller.editQuestionField.setText("What is Java?");
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Single");
            controller.editAnswerField.setText("A");
            controller.editScoreField.setText("1");
            controller.handleAdd();
        });*/
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));

        // Verify the question is added
        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(1);

        // Select the added question in the table
        robot.interact(() -> {
            controller.questionTable.getSelectionModel().select(0);
            Question selectedQuestion = controller.questionTable.getSelectionModel().getSelectedItem();
            Assertions.assertThat(selectedQuestion.getQuestion()).isEqualTo("What is Java?");
        });

        // Mock the update statement
        when(mockPstmt.executeUpdate()).thenReturn(1);

        // Update the question
        robot.interact(() -> {
            controller.editAnswerField.setText("AC");
            controller.editTypeComboBox.setValue("Multiple");
            controller.handleUpdate();
            controller.questionTable.getSelectionModel().select(0);
            Question selectedQuestion = controller.questionTable.getSelectionModel().getSelectedItem();
            Assertions.assertThat(selectedQuestion.getAnswer()).isEqualTo("AC");
            Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question updated successfully.");
        });
    }


    @Test
    public void testHandleFilter(FxRobot robot) throws SQLException {
        // Mock the ResultSet for the filter method
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getInt("id")).thenReturn(1);
        when(mockRs.getString("text")).thenReturn("What is Java?");
        when(mockRs.getString("option_a")).thenReturn("A programming language");
        when(mockRs.getString("option_b")).thenReturn("A coffee");
        when(mockRs.getString("option_c")).thenReturn("An island");
        when(mockRs.getString("option_d")).thenReturn("A car");
        when(mockRs.getString("answer")).thenReturn("A");
        when(mockRs.getBoolean("is_single_choice")).thenReturn(true);
        when(mockRs.getInt("score")).thenReturn(1);

        // Filter the questions
        robot.interact(() -> {
            controller.questionField.setText("Java");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.setText("1");
            controller.handleFilter();
        });

        // Verify the filter
        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(1);
        Assertions.assertThat(questions.get(0).getText()).isEqualTo("What is Java?");
    }

    @Test
    public void testHandleReset(FxRobot robot) {
        // Set some values in the fields
        robot.interact(() -> {
            controller.questionField.setText("Java");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.setText("1");
        });

        // Reset the fields
        robot.interact(() -> controller.handleReset());

        // Verify the reset
        Assertions.assertThat(controller.questionField.getText()).isEmpty();
        Assertions.assertThat(controller.typeComboBox.getValue()).isNull();
        Assertions.assertThat(controller.scoreField.getText()).isEmpty();
    }

    @Test
    public void testHandleRefresh(FxRobot robot) throws SQLException {
        // Mock the ResultSet for the refresh method
        ObservableList<Question> prev_questions=controller.questionTable.getItems();
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getInt("id")).thenReturn(1);
        when(mockRs.getString("text")).thenReturn("What is Java?");
        when(mockRs.getString("option_a")).thenReturn("A programming language");
        when(mockRs.getString("option_b")).thenReturn("A coffee");
        when(mockRs.getString("option_c")).thenReturn("An island");
        when(mockRs.getString("option_d")).thenReturn("A car");
        when(mockRs.getString("answer")).thenReturn("A");
        when(mockRs.getBoolean("is_single_choice")).thenReturn(true);
        when(mockRs.getInt("score")).thenReturn(1);

        // Refresh the questions
        robot.interact(() -> controller.handleRefresh());

        // Verify the refresh
        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(prev_questions.size());
        Assertions.assertThat(questions).isEqualTo(prev_questions);
    }
}