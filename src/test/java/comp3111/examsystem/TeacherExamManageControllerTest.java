package comp3111.examsystem;

import comp3111.examsystem.controller.TeacherExamManageController;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.Exam;
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
public class TeacherExamManageControllerTest {

    private TeacherExamManageController controller;
    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private PreparedStatement mockCheckStmt;
    private ResultSet mockRs;
    private ResultSet mockGeneratedKeys;

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/comp3111/examsystem/ExamManagement.fxml"));
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
        when(mockPstmt.executeUpdate()).thenReturn(1);
        ObservableList<Exam> prev_content = controller.examTable.getItems();
        System.out.println(prev_content.size());

        // Add an exam to the table
        Exam exam = new Exam(1, "Midterm Exam",true , 120, "COMP3111");
        robot.interact(() -> controller.examTable.getItems().add(exam));
        robot.interact(() -> controller.examTable.getSelectionModel().select(exam));
        System.out.println(controller.examTable.getItems().size());

        // Delete the selected exam
        robot.interact(() -> controller.handleDeleteExam());

        // Verify that the table is empty
        ObservableList<Exam> exams = controller.examTable.getItems();
        Assertions.assertThat(exams).isEqualTo(prev_content);
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
}