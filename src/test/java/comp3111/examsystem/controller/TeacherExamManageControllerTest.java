package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.Exam;
import comp3111.examsystem.model.Question;
import javafx.collections.ObservableList;
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
public class TeacherExamManageControllerTest {

    private TeacherExamManageController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private PreparedStatement mockCheckStmt;
    private ResultSet mockRs;


    @Start
    private void start(Stage stage) {
        controller = new TeacherExamManageController();
        controller.examnameField = new TextField();
        controller.editcourseField = new TextField();
        controller.timelimitField = new TextField();
        controller.editpublishedComboBox = new ComboBox<>();
        controller.errorLabel = new Label();
        controller.examTable = new TableView<>();
        controller.questionTable = new TableView<>();
        controller.examQuestionTable = new TableView<>();
        controller.questionField = new TextField();
        controller.typeComboBox = new ComboBox<>();
        controller.scoreField = new TextField();
        controller.examField = new TextField();
        controller.courseField = new TextField();
        controller.publishedComboBox = new ComboBox<>();

        VBox vbox = new VBox(controller.examnameField, controller.editcourseField, controller.timelimitField, controller.editpublishedComboBox, controller.errorLabel, controller.examTable, controller.questionTable, controller.examQuestionTable, controller.questionField, controller.typeComboBox, controller.scoreField);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockCheckStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        when(mockConn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPstmt);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockCheckStmt);

        // Mock the ResultSet for the loadExams method
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false); // Simulate one row in the ResultSet
        when(mockRs.getInt("id")).thenReturn(1);
        when(mockRs.getString("name")).thenReturn("Midterm Exam");
        when(mockRs.getString("course")).thenReturn("COMP3211");
        when(mockRs.getBoolean("is_published")).thenReturn(true);
        when(mockRs.getString("time_limit")).thenReturn("120");

        DatabaseConnection.setMockConnection(mockConn);

        // Clear the exam table before each test
        if (controller != null && controller.examTable != null) {
            controller.examTable.getItems().clear();
        }
    }


    @Test
    public void testFailAddExamNum(FxRobot robot) throws SQLException {
        // Clear the exam table before the test
        robot.interact(() -> controller.examTable.getItems().clear());

        // Mock the database interactions
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // Ensure no existing exam is found

        // Interact with the UI elements to add an exam
        robot.interact(() -> {
            controller.examnameField.setText("Midterm Exam");
            controller.editcourseField.setText("COMP3211");
            controller.timelimitField.setText("Text");
            controller.editpublishedComboBox.setValue("Yes");

            // Debugging statements to print the values of the fields
            System.out.println("Exam Name: " + controller.examnameField.getText());
            System.out.println("Course ID: " + controller.editcourseField.getText());
            System.out.println("Time Limit: " + controller.timelimitField.getText());
            System.out.println("Published: " + controller.editpublishedComboBox.getValue());

            controller.handleAddExam();
        });
        // Retrieve the items from the exam table
        ObservableList<Exam> exams = controller.examTable.getItems();

        // Debugging statement to print the size of the exam list
        System.out.println("Number of exams after adding: " + exams.size());

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Time limit must be a valid integer.");
    }

    @Test
    public void testFailedAddExamMissing(FxRobot robot) throws SQLException {
        // Clear the exam table before the test
        robot.interact(() -> controller.examTable.getItems().clear());

        // Mock the database interactions
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // Ensure no existing exam is found

        // Interact with the UI elements to add an exam
        robot.interact(() -> {
            controller.examnameField.setText("Midterm Exam");
            controller.editcourseField.setText("COMP3111");
            controller.editpublishedComboBox.setValue("Yes");

            // Debugging statements to print the values of the fields
            System.out.println("Exam Name: " + controller.examnameField.getText());
            System.out.println("Course ID: " + controller.editcourseField.getText());
            System.out.println("Time Limit: " + controller.timelimitField.getText());
            System.out.println("Published: " + controller.editpublishedComboBox.getValue());

            controller.handleAddExam();
        });
        // Retrieve the items from the exam table
        ObservableList<Exam> exams = controller.examTable.getItems();

        // Debugging statement to print the size of the exam list
        System.out.println("Number of exams after adding: " + exams.size());

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("All fields must be filled out.");
    }

    @Test
    public void testDeleteExam(FxRobot robot) throws SQLException {
        // Mock the PreparedStatement for the delete method
        when(mockPstmt.executeUpdate()).thenReturn(1);

        // Add an exam to the table
        Exam exam = new Exam(1, "Midterm Exam", true, 120, "COMP3111");
        robot.interact(() -> controller.examTable.getItems().add(exam));
        robot.interact(() -> controller.examTable.getSelectionModel().select(exam));

        // Delete the selected exam
        robot.interact(() -> controller.handleDeleteExam());

        // Verify that the table is empty
        ObservableList<Exam> exams = controller.examTable.getItems();
        Assertions.assertThat(exams).isEmpty();
    }
    @Test
    public void testHandleFailUpdate(FxRobot robot) throws SQLException {
        // Clear the exam table before the test
        robot.interact(() -> controller.examTable.getItems().clear());

        // Mock the add statement
        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // Ensure no existing exam is found

        // Add an exam
        Exam exam = new Exam(1, "Midterm Exam",true , 120, "COMP3111");
        robot.interact(() -> controller.examTable.getItems().add(exam));
        robot.interact(() -> controller.examTable.getSelectionModel().select(exam));

        // Verify the exam is added
        ObservableList<Exam> exams = controller.examTable.getItems();
        Assertions.assertThat(exams).hasSize(1);
        Assertions.assertThat(exams.get(0).getName()).isEqualTo("Midterm Exam");

        // Select the added exam in the table
        robot.interact(() -> controller.examTable.getSelectionModel().select(0));

        // Mock the update statement
        when(mockPstmt.executeUpdate()).thenReturn(1);

        // Update the exam
        robot.interact(() -> {
            controller.handleUpdateExam();
        });

        // Verify the update
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Some fields must be filled out.");
    }


    @Test
    public void testHandleReset(FxRobot robot) {
        // Set some values in the fields
        robot.interact(() -> {
            controller.examField.setText("Midterm");
            controller.courseField.setText("2023-12-01");
            controller.publishedComboBox.setValue("Yes");
        });

        // Reset the fields
        robot.interact(() -> controller.handleResetExam());

        // Verify the reset
        Assertions.assertThat(controller.examField.getText()).isEmpty();
        Assertions.assertThat(controller.courseField.getText()).isEmpty();
        Assertions.assertThat(controller.publishedComboBox.getValue()).isNull();
    }

    @Test
    public void testHandleFilterExam(FxRobot robot) throws SQLException {
        // Mock the ResultSet for the filter method
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getInt("id")).thenReturn(1);
        when(mockRs.getString("name")).thenReturn("Final Exam");
        when(mockRs.getString("course")).thenReturn("COMP3999");
        when(mockRs.getString("is_published")).thenReturn("No");

        // Set the filter criteria
        robot.interact(() -> {
            controller.examField.setText("Final Exam");
            controller.courseField.setText("COMP3999");
            controller.publishedComboBox.setValue("No");
        });

        // Filter the exams
        robot.interact(() -> controller.handleFilterExam());

        // Verify the filter
        ObservableList<Exam> exams = controller.examTable.getItems();
        Assertions.assertThat(exams).hasSize(1);
        Assertions.assertThat(exams.get(0).getName()).isEqualTo("Final Exam");
    }

    @Test
    public void testHandleFilterQuestion(FxRobot robot) throws SQLException {
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
                controller.handleFilterQuestion();
            });

            // Verify the filter
            ObservableList<Question> questions = controller.questionTable.getItems();
            Assertions.assertThat(questions).hasSize(1);
            Assertions.assertThat(questions.get(0).getText()).isEqualTo("What is Java?");
    }

    @Test
    public void testHandleFilterQuestionEmptyScore(FxRobot robot) throws SQLException {
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

        // Interact with the UI elements to set the filter criteria
        robot.interact(() -> {
            controller.questionField.setText("Java");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.clear(); // Ensure scoreField is empty

            controller.handleFilterQuestion();
        });

        // Verify the filter
        ObservableList<Question> questions = controller.questionTable.getItems();
        Assertions.assertThat(questions).hasSize(1);
        Assertions.assertThat(questions.get(0).getText()).isEqualTo("What is Java?");
    }

    @Test
    public void testHandleResetQuestion(FxRobot robot) {
        robot.interact(() -> {
            controller.questionField.setText("Java");
            controller.typeComboBox.setValue("Single");
            controller.scoreField.setText("1");
            controller.handleResetQuestion();
        });

        Assertions.assertThat(controller.questionField.getText()).isEmpty();
        Assertions.assertThat(controller.typeComboBox.getValue()).isNull();
        Assertions.assertThat(controller.scoreField.getText()).isEmpty();
    }

    @Test
    public void testHandleAddExamEmptyExamName(FxRobot robot) {
        robot.interact(() -> {
            controller.examnameField.clear();
            controller.editcourseField.setText("COMP3111");
            controller.editpublishedComboBox.setValue("Yes");
            controller.timelimitField.setText("120");
            controller.handleAddExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("All fields must be filled out.");
    }

    @Test
    public void testHandleAddExamEmptyCourseId(FxRobot robot) {
        robot.interact(() -> {
            controller.examnameField.setText("Midterm Exam");
            controller.editcourseField.clear();
            controller.editpublishedComboBox.setValue("Yes");
            controller.timelimitField.setText("120");
            controller.handleAddExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("All fields must be filled out.");
    }

    @Test
    public void testHandleAddExamEmptyPublished(FxRobot robot) {
        robot.interact(() -> {
            controller.examnameField.setText("Midterm Exam");
            controller.editcourseField.setText("COMP3111");
            controller.editpublishedComboBox.setValue(null);
            controller.timelimitField.setText("120");
            controller.handleAddExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("All fields must be filled out.");
    }

    @Test
    public void testHandleAddExistingExam(FxRobot robot) throws SQLException {
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(1);

        robot.interact(() -> {
            controller.examnameField.setText("Midterm Exam");
            controller.editcourseField.setText("COMP3111");
            controller.editpublishedComboBox.setValue("Yes");
            controller.timelimitField.setText("120");
            controller.handleAddExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("The exam already exists.");
    }

    @Test
    public void testHandleAddExam(FxRobot robot) throws SQLException {
        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

        robot.interact(() -> {
            controller.examnameField.setText("Midterm Exam");
            controller.editcourseField.setText("COMP3111");
            controller.editpublishedComboBox.setValue("Yes");
            controller.timelimitField.setText("120");
            controller.handleAddExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Exam added successfully.");
    }

    @Test
    public void testHandleUpdateExamNoSelection(FxRobot robot) {
        robot.interact(() -> controller.handleUpdateExam());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Please select an exam to update.");
    }

    @Test
    public void testHandleUpdateExamBranches(FxRobot robot) throws SQLException {
        Exam exam = new Exam(1, "Midterm Exam", true, 120, "COMP3111");
        robot.interact(() -> controller.examTable.getItems().add(exam));
        robot.interact(() -> controller.examTable.getSelectionModel().select(exam));

        robot.interact(() -> {
            controller.examnameField.clear();
            controller.editcourseField.clear();
            controller.editpublishedComboBox.setValue(null);
            controller.timelimitField.clear();
            controller.handleUpdateExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Some fields must be filled out.");
    }

    @Test
    public void testHandleUpdateExam(FxRobot robot) throws SQLException {
        Exam exam = new Exam(1, "Midterm Exam", true, 120, "COMP3111");
        robot.interact(() -> controller.examTable.getItems().add(exam));
        robot.interact(() -> controller.examTable.getSelectionModel().select(exam));

        robot.interact(() -> {
            controller.examnameField.setText("Final Exam");
            controller.editcourseField.setText("COMP3111");
            controller.editpublishedComboBox.setValue("No");
            controller.timelimitField.setText("90");
            controller.handleUpdateExam();
        });

        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Exam updated successfully.");
    }

    @Test
    public void testHandleDeleteExamNoSelection(FxRobot robot) {
        robot.interact(() -> controller.handleDeleteExam());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Please select an exam to delete.");
    }

//    @Test
//    public void testHandleRefresh(FxRobot robot) throws SQLException {
//        // Mock the ResultSet for the refresh method
//        when(mockRs.next()).thenReturn(true).thenReturn(false);
//        when(mockRs.getInt("id")).thenReturn(1);
//        when(mockRs.getString("name")).thenReturn("Midterm Exam");
//        when(mockRs.getString("course")).thenReturn("COMP3111");
//        when(mockRs.getBoolean("is_published")).thenReturn(true);
//        when(mockRs.getString("time_limit")).thenReturn("120");
//
//        // Mock the PreparedStatement for the refresh method
//        when(mockCheckStmt.executeQuery()).thenReturn(mockRs);
//
//        // Interact with the UI elements to refresh the data
//        robot.interact(() -> controller.handleRefresh());
//
//        // Verify the refresh
//        ObservableList<Exam> exams = controller.examTable.getItems();
//        Assertions.assertThat(exams).hasSize(1);
//        Assertions.assertThat(exams.get(0).getName()).isEqualTo("Midterm Exam");
//
//        ObservableList<Question> questions = controller.questionTable.getItems();
//        Assertions.assertThat(questions).isNotEmpty();
//    }

    @Test
    public void testHandleDeleteFromExamNoSelection(FxRobot robot) {
        robot.interact(() -> controller.handleDeleteFromExam());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("No question selected for deletion.");
    }

    @Test
    public void testHandleDeleteFromExam(FxRobot robot) throws SQLException {
        Question question = new Question(1, "Sample Question", "A", "B", "C", "D", "A", "Single", 1);
        robot.interact(() -> controller.examQuestionTable.getItems().add(question));
        robot.interact(() -> controller.examQuestionTable.getSelectionModel().select(question));

        robot.interact(() -> controller.handleDeleteFromExam());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question deleted from exam successfully.");
    }

    @Test
    public void testHandleAddToExamNoSelection(FxRobot robot) {
        robot.interact(() -> controller.handleAddToExam());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("No question selected for adding.");
    }

    @Test
    public void testHandleAddToExam(FxRobot robot) throws SQLException {
        Question question = new Question(1, "Sample Question", "A", "B", "C", "D", "A", "Single", 1);
        robot.interact(() -> controller.questionTable.getItems().add(question));
        robot.interact(() -> controller.questionTable.getSelectionModel().select(question));

        robot.interact(() -> controller.handleAddToExam());
        Assertions.assertThat(controller.errorLabel.getText()).isEqualTo("Question added to exam successfully.");
    }

}