package comp3111.examsystem.controller;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.sql.*;

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
        if (controller == null) {
            controller = new TeacherQuestionController();
        }
        controller.questionField = new TextField();
        controller.editQuestionField = new TextField();
        controller.editOptionAField = new TextField();
        controller.editOptionBField = new TextField();
        controller.editOptionCField = new TextField();
        controller.editOptionDField = new TextField();
        controller.editAnswerField = new TextField();
        controller.editTypeComboBox = new ComboBox<>();
        controller.editScoreField = new TextField();
        controller.errorLabel = new Label();
        controller.questionTable = new TableView<>();
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

        //insert
        when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPstmt);
        //select
        when(mockConn.prepareStatement(anyString())).thenReturn(mockCheckStmt);
        when(mockPstmt.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);
        when(mockCheckStmt.getGeneratedKeys()).thenReturn(mockGeneratedKeys);

        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
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

        DatabaseConnection.setMockConnection(mockConn);
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

        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));

        robot.interact(() -> controller.handleDelete());

        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).isEqualTo(prev_content);
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question deleted successfully.");
    }

    @Test
    public void testHandleFailUpdateDupOrWrongType(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());

        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

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

        robot.interact(() -> controller.questionTable.getSelectionModel().select(0));

        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);


        robot.interact(() -> {
            controller.editQuestionField.setText("What is Java?");
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Single");
            controller.editAnswerField.setText("B");
            controller.editScoreField.setText("2");
            controller.handleUpdate();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("The question already exists.");

        //test for error2
        robot.interact(() -> controller.questionTable.getItems().clear());

        when(mockPstmt.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

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

        robot.interact(() -> controller.questionTable.getSelectionModel().select(0));

        when(mockPstmt.executeUpdate()).thenThrow(new SQLException());

        robot.interact(() -> {
            controller.editQuestionField.setText("");;
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Single");
            controller.editAnswerField.setText("AB");
            controller.editScoreField.setText("2");
            controller.handleUpdate();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Answer format incorrect");
    }

    @Test
    public void testHandleUpdate(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());

        when(mockPstmt.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

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

        robot.interact(() -> controller.questionTable.getSelectionModel().select(0));

        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeUpdate()).thenReturn(0);

        robot.interact(() -> {
            controller.editQuestionField.setText("");
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.editTypeComboBox.setValue("Single");
            controller.editAnswerField.setText("B");
            controller.editScoreField.setText("2");
            controller.handleUpdate();
        });

        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(0);
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question updated successfully.");
    }

    @Test
    public void testHandleFilter(FxRobot robot) throws SQLException {
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

        robot.interact(() -> {
            controller.questionField.setText("Java");
            controller.handleFilter();
        });

        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(0);
        Assertions.assertThat(questions.get(0).getText()).isEqualTo("What is Java?");
    }

    @Test
    public void testHandleReset(FxRobot robot) {
        robot.interact(() -> {
            controller.questionField.setText("Java");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.setText("1");
        });

        robot.interact(() -> controller.handleReset());

        Assertions.assertThat(controller.questionField.getText()).isEmpty();
        Assertions.assertThat(controller.typeComboBox.getValue()).isNull();
        Assertions.assertThat(controller.scoreField.getText()).isEmpty();
    }

    @Test
    public void testHandleRefresh(FxRobot robot) throws SQLException {
        ObservableList<Question> prev_questions = controller.questionTable.getItems();
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

        robot.interact(() -> controller.handleRefresh());

        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(prev_questions.size());
        Assertions.assertThat(questions).isEqualTo(prev_questions);
    }

    @Test
    public void testFilterFieldsEmpty(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));

        // Test with empty question field
        robot.interact(() -> {
            controller.questionField.setText("");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.setText("1");
            controller.handleFilter();
        });

        Assertions.assertThat(controller.questionTable.getItems()).isNotEmpty();

        // Test with empty type field
        robot.interact(() -> {
            controller.questionField.setText("What is Java?");
            controller.typeComboBox.setValue(null);
            controller.scoreField.setText("1");
            controller.handleFilter();
        });
        Assertions.assertThat(controller.questionTable.getItems()).isEmpty();

        // Test with empty score field
        robot.interact(() -> {
            controller.questionField.setText("What is Java?");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.setText("");
            controller.handleFilter();
        });
        Assertions.assertThat(controller.questionTable.getItems()).isEmpty();
    }
    @Test
    public void testDeleteWithoutSelection(FxRobot robot) {
        robot.interact(() -> controller.questionTable.getSelectionModel().clearSelection());
        robot.interact(() -> controller.handleDelete());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Please select a question to delete.");
    }


    @Test
    public void testUpdateOptionBEmpty(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));

        // Select the question
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));
        robot.interact(() -> {
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("");
            controller.editOptionDField.setText("A car");
            controller.handleUpdate();
        });
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question updated successfully.");
    }
    @Test
    public void testUpdateOptionAEmpty(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));

        // Select the question
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));
        robot.interact(() -> {
            controller.editOptionAField.setText("");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("A car");
            controller.handleUpdate();
        });
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question updated successfully.");
    }

    @Test
    public void testUpdateOptionCEmpty(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));

        // Select the question
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));
        robot.interact(() -> {
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("");
            controller.editOptionDField.setText("A car");
            controller.handleUpdate();
        });
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question updated successfully.");
    }

    @Test
    public void testUpdateOptionDEmpty(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));

        // Select the question
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));
        robot.interact(() -> {
            controller.editOptionAField.setText("A programming language");
            controller.editOptionBField.setText("A coffee");
            controller.editOptionCField.setText("An island");
            controller.editOptionDField.setText("");
            controller.handleUpdate();
        });
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question updated successfully.");
    }




    @Test
    public void testUpdateWithRepeatedChoice(FxRobot robot) throws SQLException {
        robot.interact(() -> controller.questionTable.getItems().clear());
        Question question = new Question(1, "What is Java?", "A programming language", "A coffee", "An island", "A car", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));

        // Select the question
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));
        robot.interact(() -> {
            controller.editAnswerField.setText("AA");
            controller.handleUpdate();
        });
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Answer format incorrect");
    }
}