package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.Exam;
import comp3111.examsystem.model.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for handling the exam management interface for teachers.
 * This class is responsible for managing the UI interactions for exam management.
 *
 * @author Wong Cheuk Yuen
 * @version 1.0
 */
public class TeacherExamManageController {
    @FXML
    public TextField examField, questionFilterField, examnameField, timelimitField;
    @FXML
    public TableView<Exam> examTable;
    @FXML
    public TextField courseField;
    @FXML
    public ComboBox<String> publishedComboBox;
    @FXML
    public ComboBox<String> typeComboBox;
    @FXML
    public TableView<Question> examQuestionTable, questionTable;
    @FXML
    public TableColumn<Exam, Integer> examIdColumn;
    @FXML
    public TableColumn<Exam, String> examNameColumn, examCourseColumn;
    @FXML
    public TableColumn<Exam, Boolean> examPublishedColumn;
    @FXML
    public TableColumn<Exam, Integer> examTimeLimitColumn;
    @FXML
    public TableColumn<Question, Integer> examQuestionIdColumn, questionIdColumn;
    @FXML
    public TableColumn<Question, String> examQuestionTextColumn, questionTextColumn, questionOptionAColumn, questionOptionBColumn, questionOptionCColumn, questionOptionDColumn, questionAnswerColumn, questionTypeColumn;
    @FXML
    public TableColumn<Question, Integer> questionScoreColumn;
    @FXML
    public Label errorLabel;
    @FXML
    public TextField scoreField;
    @FXML
    public TextField questionField;
    @FXML
    public ComboBox<String> editpublishedComboBox;
    @FXML
    public TextField editcourseField;
    @FXML
    public TableColumn<Question, String> examQuestionTypeColumn;
    @FXML
    public TableColumn<Question, Integer> examQuestionScoreColumn;

    public ObservableList<Exam> examList = FXCollections.observableArrayList();
    public ObservableList<Question> examQuestionList = FXCollections.observableArrayList();
    public ObservableList<Question> questionList = FXCollections.observableArrayList();
    public int currentExamId;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        examNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        examPublishedColumn.setCellValueFactory(new PropertyValueFactory<>("isPublished"));
        examTimeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        examCourseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));

        examQuestionIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        examQuestionTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        examQuestionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        examQuestionScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        questionIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        questionOptionAColumn.setCellValueFactory(new PropertyValueFactory<>("optionA"));
        questionOptionBColumn.setCellValueFactory(new PropertyValueFactory<>("optionB"));
        questionOptionCColumn.setCellValueFactory(new PropertyValueFactory<>("optionC"));
        questionOptionDColumn.setCellValueFactory(new PropertyValueFactory<>("optionD"));
        questionAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        questionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        questionScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        loadExams();
        loadQuestions();

        examTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentExamId = newValue.getId();
                loadExamQuestions(currentExamId);
            }
        });

        questionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Loads questions for the specified exam from the database and populates the TableView.
     *
     * @param examId the ID of the exam
     */
    public void loadExamQuestions(int examId) {
        String sql = "SELECT q.* FROM question q " +
                "JOIN exam_question_link eq ON q.id = eq.question_id " +
                "WHERE eq.exam_id = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();
            examQuestionList.clear();
            while (rs.next()) {
                // Assuming you have a Question class and a method to create a Question object from ResultSet
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("answer"),
                        rs.getBoolean("is_single_choice") ? "Single" : "Multiple",
                        rs.getInt("score")
                );
                examQuestionList.add(question);
            }
            examQuestionTable.setItems(examQuestionList);
            return true;
        });
    }

    /**
     * Loads exams from the database and populates the TableView.
     */
    public void loadExams() {
        String sql = "SELECT * FROM exam";
        executePreparedStatement(sql, pstmt -> {
            ResultSet rs = pstmt.executeQuery();
            ObservableList<Exam> examList = FXCollections.observableArrayList();
            while (rs.next()) {
                Exam exam = new Exam(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("is_published"),
                        rs.getInt("time_limit"),
                        rs.getString("course")
                );
                examList.add(exam);
            }

            examTable.setItems(examList);
            examTable.refresh();
            return true;
        });
    }

    /**
     * Loads questions from the database and populates the TableView.
     */
    public void loadQuestions() {
        String sql = "SELECT * FROM question";
        executePreparedStatement(sql, pstmt -> {
            ResultSet rs = pstmt.executeQuery();
            questionList.clear();
            while (rs.next()) {
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("answer"),
                        rs.getBoolean("is_single_choice") ? "Single" : "Multiple",
                        rs.getInt("score")
                );
                questionList.add(question);
            }
            questionTable.setItems(questionList);
            questionTable.refresh();
            return true;
        });
    }

    /**
     * Handles the filter action for questions.
     */
    @FXML
    public void handleFilterQuestion() {
        String question = questionField.getText();
        String type = typeComboBox.getValue();
        String scoreText = scoreField.getText();

        StringBuilder sql = new StringBuilder("SELECT * FROM question WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (!question.isEmpty()) {
            sql.append(" AND text LIKE ?");
            parameters.add("%" + question + "%");
        }
        if (type != null) {
            sql.append(" AND is_single_choice = ?");
            parameters.add(type.equals("Single"));
        }
        if (!scoreText.isEmpty()) {
            try {
                int score = Integer.parseInt(scoreText);
                sql.append(" AND score = ?");
                parameters.add(score);
            } catch (NumberFormatException e) {
                setMessage(false, "Score must be a valid integer.");
                return;
            }
        }

        executePreparedStatement(sql.toString(), pstmt -> {
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            questionList.clear();

            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("answer"),
                        rs.getBoolean("is_single_choice") ? "Single" : "Multiple",
                        rs.getInt("score")
                );
                questionList.add(q);
            }

            questionTable.setItems(questionList);
            questionTable.refresh();
            return true;
        });

    }

    /**
     * Handles the reset action for questions.
     */
    @FXML
    public void handleResetQuestion() {
        questionField.clear();
        typeComboBox.setValue(null);
        scoreField.clear();
    }

    /**
     * Handles the filter action for exams.
     */
    @FXML
    public void handleFilterExam() {
        String examName = examField.getText();
        String courseId = courseField.getText();
        String published = publishedComboBox.getValue();

        String sql = "SELECT * FROM exam WHERE name LIKE ? AND course LIKE ? AND (is_published = ? OR ? IS NULL)";

        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, "%" + examName + "%");
            pstmt.setString(2, "%" + courseId + "%");
            pstmt.setBoolean(3, "Yes".equals(published));
            pstmt.setString(4, published);

            ResultSet rs = pstmt.executeQuery();
            examList.clear();

            while (rs.next()) {
                Exam exam = new Exam(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("is_published"),
                        rs.getInt("time_limit"),
                        rs.getString("course")
                );
                examList.add(exam);
            }
            examTable.setItems(examList);
            examTable.refresh();
            return true;
        });
    }

    /**
     * Handles the add action for exams.
     */
    @FXML
    public void handleAddExam() {
        String examName = examnameField.getText();
        String courseId = editcourseField.getText();
        String published = editpublishedComboBox.getValue();
        String timeLimitText = timelimitField.getText();

        if (examName.isEmpty() || courseId.isEmpty() || published == null || timeLimitText.isEmpty()) {
            setMessage(false, "All fields must be filled out.");
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitText);
        } catch (NumberFormatException e) {
            setMessage(false, "Time limit must be a valid integer.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM exam WHERE name = ? AND course = ?";
        boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, examName);
            checkStmt.setString(2, courseId);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.getInt(1) > 0) {
                setMessage(false, "The exam already exists.");
                return true;
            }
            return false;
        });
        if (isDuplicate) {
            return;
        }

        String sql = "INSERT INTO exam (name, course, is_published, time_limit) VALUES (?, ?, ?, ?)";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, examName);
            pstmt.setString(2, courseId);
            pstmt.setBoolean(3, "Yes".equals(published));
            pstmt.setInt(4, timeLimit);

            pstmt.executeUpdate();
            setMessage(true, "Exam added successfully.");
            loadExams();

            return true;
        });
    }

    /**
     * Handles the update action for exams.
     */
    @FXML
    public void handleUpdateExam() {
        Exam selectedExam = examTable.getSelectionModel().getSelectedItem();

        if (selectedExam == null) {
            setMessage(false, "Please select an exam to update.");
            return;
        }

        String examName = examnameField.getText();
        String prev_examName = selectedExam.getName();
        String courseId = editcourseField.getText();
        String published = editpublishedComboBox.getValue();
        String timeLimitText = timelimitField.getText();
        int prev_timeLimit = selectedExam.getTimeLimit();

        if (examName.isEmpty() && courseId.isEmpty() && published == null && timeLimitText.isEmpty()) {
            setMessage(false, "Some fields must be filled out.");
            return;
        }
        examName = examName.isEmpty() ? prev_examName : examName;
        published = published == null ? (selectedExam.isPublished() ? "Yes" : "No") : published;
        courseId = courseId.isEmpty() ? selectedExam.getCourse() : courseId;
        int timeLimit;
        try {
            timeLimit = timelimitField.getText().isEmpty() ? prev_timeLimit : Integer.parseInt(timelimitField.getText());
        } catch (NumberFormatException e) {
            setMessage(false, "Time limit must be a valid integer.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM exam WHERE name = ? AND course = ? AND id != ?";
        String finalCourseId = courseId;
        String finalExamName = examName;

        boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, finalExamName);
            checkStmt.setString(2, finalCourseId);
            checkStmt.setInt(3, selectedExam.getId());

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                setMessage(false, "Updating the exam will result in a duplicate entry.");
                return true;
            }
            return false;
        });
        if (isDuplicate) {
            return;
        }

        String sql = "UPDATE exam SET name = ?, course = ?, is_published = ?, time_limit = ? WHERE id = ?";
        String finalPublished = published;
        String finalCourseId1 = courseId;
        String finalExamName1 = examName;
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, finalExamName1);
            pstmt.setString(2, finalCourseId1);
            pstmt.setBoolean(3, "Yes".equals(finalPublished));
            pstmt.setInt(4, timeLimit);
            pstmt.setInt(5, selectedExam.getId());
            pstmt.executeUpdate();

            setMessage(true, "Exam updated successfully.");
            examTable.refresh();
            loadExams();
            return true;
        });
    }

    /**
     * Handles the delete action for exams.
     */
    @FXML
    public void handleDeleteExam() {
        Exam selectedExam = examTable.getSelectionModel().getSelectedItem();

        if (selectedExam == null) {
            setMessage(false, "Please select an exam to delete.");
            return;
        }

        String sql = "DELETE FROM exam WHERE id = ?";

        executePreparedStatement(sql, pstmt -> {
            pstmt.setInt(1, selectedExam.getId());
            pstmt.executeUpdate();
            setMessage(true, "Exam deleted successfully.");

            examTable.getItems().remove(selectedExam);
            examTable.refresh();
            return true;
        });
    }

    /**
     * Handles the reset action for exams.
     */
    @FXML
    public void handleResetExam() {
        examField.clear();
        courseField.clear();
        publishedComboBox.setValue(null);
    }

    /**
     * Handles the refresh action.
     * Reloads the exams and questions from the database.
     */
    @FXML
    public void handleRefresh() {
        loadExams();
        loadQuestions();
    }

    /**
     * Handles the delete question from exam action.
     * Deletes the selected question from the current exam.
     */
    @FXML
    public void handleDeleteFromExam() {
        Question selectedQuestion = examQuestionTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion == null) {
            setMessage(false, "No question selected for deletion.");
            return;
        }

        String sql = "DELETE FROM exam_question_link WHERE exam_id = ? AND question_id = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setInt(1, currentExamId);
            pstmt.setInt(2, selectedQuestion.getId());
            pstmt.executeUpdate();
            loadExamQuestions(currentExamId); // Refresh the table after deletion
            setMessage(true, "Question deleted from exam successfully.");
            return true;
        });

    }


    /**
     * Handles the add question to exam action.
     * Adds the selected question to the current exam.
     */
    @FXML
    public void handleAddToExam() {
        Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion == null) {
            setMessage(false, "No question selected for adding.");
            return;
        }
        String checkSql = "SELECT COUNT(*) FROM exam_question_link WHERE exam_id = ? AND question_id = ?";
        String insertSql = "INSERT INTO exam_question_link (exam_id, question_id) VALUES (?, ?)";

        boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setInt(1, currentExamId);
            checkStmt.setInt(2, selectedQuestion.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                setMessage(false, "This question has already been added to the exam.");
                return true;
            }
            return false;
        });
        if (isDuplicate) {
            return;
        }
        executePreparedStatement(insertSql, insertStmt -> {
            insertStmt.setInt(1, currentExamId);
            insertStmt.setInt(2, selectedQuestion.getId());
            insertStmt.executeUpdate();
            loadExamQuestions(currentExamId); // Refresh the table after adding
            setMessage(true, "Question added to exam successfully.");
            return true;
        });
    }
    /**
     * Executes a database operation using a connection from the database connection pool.
     *
     * @param operation the database operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executeDatabaseOperation(StudentRegisterController.DatabaseOperation operation) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return operation.execute(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            setMessage(false, "Error connecting to the database.");
            return false;
        }
    }
    /**
     * Executes a prepared statement operation using a connection from the database connection pool.
     *
     * @param sql the SQL query to be executed
     * @param operation the prepared statement operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executePreparedStatement(String sql, StudentRegisterController.PreparedStatementOperation operation) {
        return executeDatabaseOperation(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                return operation.execute(pstmt);
            } catch (SQLException e) {
                e.printStackTrace();
                setMessage(false, "Error connecting to the database.");
                return false;
            }
        });
    }

    /**
     * Functional interface for database operations.
     */
    @FunctionalInterface
    private interface DatabaseOperation {
        /**
         * Executes a database operation.
         *
         * @param conn the database connection
         * @return true if the operation was successful, false otherwise
         * @throws SQLException if a database access error occurs
         */
        boolean execute(Connection conn) throws SQLException;
    }

    /**
     * Functional interface for prepared statement operations.
     */
    @FunctionalInterface
    private interface PreparedStatementOperation {
        /**
         * Executes a prepared statement operation.
         *
         * @param pstmt the prepared statement
         * @return true if the operation was successful, false otherwise
         * @throws SQLException if a database access error occurs
         */
        boolean execute(PreparedStatement pstmt) throws SQLException;
    }

    /**
     * Sets the error message label with the specified message and style.
     *
     * @param success true if the operation was successful, false otherwise
     * @param message the message to be displayed
     */
    private void setMessage(boolean success, String message) {
        errorLabel.setText(message);
        if (success) {
            errorLabel.setStyle("-fx-text-fill: green;");
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
        }
        errorLabel.setVisible(true);
    }

}
