/*package comp3111.examsystem.controller;
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


public class TeacherExamManageController {
    @FXML
    private TextField examField, questionFilterField;
    @FXML
    private TableView<Exam> examTable;
    @FXML
    private TextField courseField;
    @FXML
    private ComboBox<String> publishedComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TableView<Question> examQuestionTable, questionTable;
    @FXML
    private TableColumn<Exam, Integer> examIdColumn;
    @FXML
    private TableColumn<Exam, String> examNameColumn, examCourseColumn;
    @FXML
    private TableColumn<Exam, Boolean> examPublishedColumn;
    @FXML
    private TableColumn<Exam, Integer> examTimeLimitColumn;
    @FXML
    private TableColumn<Question, Integer> examQuestionIdColumn, questionIdColumn;
    @FXML
    private TableColumn<Question, String> examQuestionTextColumn, questionTextColumn, questionOptionAColumn, questionOptionBColumn, questionOptionCColumn, questionOptionDColumn, questionAnswerColumn, questionTypeColumn;
    @FXML
    private TableColumn<Question, Integer> questionScoreColumn;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField scoreField;
    @FXML
    private TextField questionField;
    @FXML
    private TextField examnameField;
    @FXML
    private TextField timelimitField;

    private ObservableList<Exam> examList = FXCollections.observableArrayList();
    private ObservableList<Question> examQuestionList = FXCollections.observableArrayList();
    private ObservableList<Question> questionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        examNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        examPublishedColumn.setCellValueFactory(new PropertyValueFactory<>("isPublished"));
        examTimeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        examCourseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));

        examQuestionIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        examQuestionTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

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
    }

    private void loadExams() {
        String sql = "SELECT * FROM exam";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        String sql = "SELECT * FROM question";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterQuestion(){
        // Implement filter logic here
        String question = questionField.getText();
        String type = typeComboBox.getValue();
        int score;
        try {
            score = scoreField.getText().isEmpty() ? -1 : Integer.parseInt(scoreField.getText());
        } catch (NumberFormatException e) {
            // Show an error message or handle the invalid number appropriately
            errorLabel.setText("Score must be a valid integer.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sql = "SELECT * FROM question WHERE text LIKE ? AND (is_single_choice = ? OR ? IS NULL) AND (score = ? OR ? = -1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + question + "%");
            pstmt.setBoolean(2, type != null && type.equals("Single"));
            pstmt.setString(3, type);
            pstmt.setInt(4, score);
            pstmt.setInt(5, score);

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetQuestion() {

    }

    @FXML
    private void handleFilterExam() {
        // Implement filter logic for exams
    }

    @FXML
    private void handleAddExam() {
        // Implement add exam logic
    }

    @FXML
    private void handleUpdateExam() {
        // Implement update exam logic
    }

    @FXML
    private void handleDeleteExam() {
        // Implement delete exam logic
    }

    @FXML
    private void handleRefreshExam() {
        loadExams();
    }

    @FXML
    private void handleDeleteFromExam() {
        // Implement delete question from exam logic
    }

    @FXML
    private void handleAddToExam() {
        // Implement add question to exam logic
    }
}*/

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

public class TeacherExamManageController {
    @FXML
    private TextField examField, questionFilterField, examnameField, timelimitField;
    @FXML
    private TableView<Exam> examTable;
    @FXML
    private TextField courseField;
    @FXML
    private ComboBox<String> publishedComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TableView<Question> examQuestionTable, questionTable;
    @FXML
    private TableColumn<Exam, Integer> examIdColumn;
    @FXML
    private TableColumn<Exam, String> examNameColumn, examCourseColumn;
    @FXML
    private TableColumn<Exam, Boolean> examPublishedColumn;
    @FXML
    private TableColumn<Exam, Integer> examTimeLimitColumn;
    @FXML
    private TableColumn<Question, Integer> examQuestionIdColumn, questionIdColumn;
    @FXML
    private TableColumn<Question, String> examQuestionTextColumn, questionTextColumn, questionOptionAColumn, questionOptionBColumn, questionOptionCColumn, questionOptionDColumn, questionAnswerColumn, questionTypeColumn;
    @FXML
    private TableColumn<Question, Integer> questionScoreColumn;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField scoreField;
    @FXML
    private TextField questionField;
    @FXML
    private ComboBox<String> editpublishedComboBox;
    @FXML
    private TextField editcourseField;
    @FXML
    private TableColumn<Question, String> examQuestionTypeColumn;
    @FXML
    private TableColumn<Question, Integer> examQuestionScoreColumn;

    private ObservableList<Exam> examList = FXCollections.observableArrayList();
    private ObservableList<Question> examQuestionList = FXCollections.observableArrayList();
    private ObservableList<Question> questionList = FXCollections.observableArrayList();
    private int currentExamId;
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


        // Add listener to update currentExamId when a new exam is selected
        examTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentExamId = newValue.getId();
                loadExamQuestions(currentExamId);
            }
        });
    }

    private void loadExamQuestions(int examId) {
        String sql = "SELECT q.* FROM question q " +
                "JOIN exam_question_link eq ON q.id = eq.question_id " +
                "WHERE eq.exam_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();

            examQuestionList.clear();
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
                examQuestionList.add(question);
            }
            examQuestionTable.setItems(examQuestionList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadExams() {
        String sql = "SELECT * FROM exam";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        String sql = "SELECT * FROM question";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterQuestion() {
        String question = questionField.getText();
        String type = typeComboBox.getValue();
        int score;
        try {
            score = scoreField.getText().isEmpty() ? -1 : Integer.parseInt(scoreField.getText());
        } catch (NumberFormatException e) {
            errorLabel.setText("Score must be a valid integer.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sql = "SELECT * FROM question WHERE text LIKE ? AND (is_single_choice = ? OR ? IS NULL) AND (score = ? OR ? = -1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + question + "%");
            pstmt.setBoolean(2, type != null && type.equals("Single"));
            pstmt.setString(3, type);
            pstmt.setInt(4, score);
            pstmt.setInt(5, score);

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetQuestion() {
        questionField.clear();
        typeComboBox.setValue(null);
        scoreField.clear();
        //loadQuestions();
    }

    @FXML
    private void handleFilterExam() {
        String examName = examField.getText();
        String courseId = courseField.getText();
        String published = publishedComboBox.getValue();

        String sql = "SELECT * FROM exam WHERE name LIKE ? AND course LIKE ? AND (is_published = ? OR ? IS NULL)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddExam() {
        String examName = examnameField.getText();
        String courseId = editcourseField.getText();
        String published = editpublishedComboBox.getValue();
        String timeLimitText = timelimitField.getText();

        if (examName.isEmpty() || courseId.isEmpty() || published == null || timeLimitText.isEmpty()) {
            errorLabel.setText("All fields must be filled out.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Time limit must be a valid integer.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sql = "INSERT INTO exam (name, course, is_published, time_limit) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, examName);
            pstmt.setString(2, courseId);
            pstmt.setBoolean(3, "Yes".equals(published));
            pstmt.setInt(4, timeLimit);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                errorLabel.setText("Exam added successfully.");
                errorLabel.setStyle("-fx-text-fill: green;");
                loadExams();
            } else {
                errorLabel.setText("Failed to add the exam.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleUpdateExam() {
        Exam selectedExam = examTable.getSelectionModel().getSelectedItem();

        if (selectedExam == null) {
            errorLabel.setText("Please select an exam to update.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String examName = examnameField.getText();
        String courseId = editcourseField.getText();
        String published = editpublishedComboBox.getValue();
        String timeLimitText = timelimitField.getText();

        if (examName.isEmpty() || courseId.isEmpty() || published == null || timeLimitText.isEmpty()) {
            errorLabel.setText("All fields must be filled out.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Time limit must be a valid integer.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sql = "UPDATE exam SET name = ?, course = ?, is_published = ?, time_limit = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, examName);
            pstmt.setString(2, courseId);
            pstmt.setBoolean(3, "Yes".equals(published));
            pstmt.setInt(4, timeLimit);
            pstmt.setInt(5, selectedExam.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                errorLabel.setText("Exam updated successfully.");
                errorLabel.setStyle("-fx-text-fill: green;");
                loadExams();
            } else {
                errorLabel.setText("Failed to update the exam.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleDeleteExam() {
        Exam selectedExam = examTable.getSelectionModel().getSelectedItem();

        if (selectedExam == null) {
            errorLabel.setText("Please select an exam to delete.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sql = "DELETE FROM exam WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedExam.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                errorLabel.setText("Exam deleted successfully.");
                errorLabel.setStyle("-fx-text-fill: green;");
                loadExams();
            } else {
                errorLabel.setText("Failed to delete the exam.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    @FXML
    private void handleResetExam() {
        examField.clear();
        courseField.clear();
        publishedComboBox.setValue(null);
    }

    @FXML
    private void handleRefresh() {
        loadExams();
        loadQuestions();
    }

    @FXML
    private void handleDeleteFromExam() {
        // Implement delete question from exam logic
        Question selectedQuestion = examQuestionTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM exam_question_link WHERE exam_id = ? AND question_id = ?")) {
                pstmt.setInt(1, currentExamId);
                pstmt.setInt(2, selectedQuestion.getId());
                pstmt.executeUpdate();
                loadExamQuestions(currentExamId); // Refresh the table after deletion
            } catch (SQLException e) {
                e.printStackTrace();
                errorLabel.setText("Error deleting question from exam: " + e.getMessage());
            }
        } else {
            errorLabel.setText("No question selected for deletion.");
        }
    }

    @FXML
    private void handleAddToExam() {
        Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO exam_question_link (exam_id, question_id) VALUES (?, ?)")) {
                pstmt.setInt(1, currentExamId);
                pstmt.setInt(2, selectedQuestion.getId());
                pstmt.executeUpdate();
                loadExamQuestions(currentExamId); // Refresh the table after adding
                errorLabel.setText("Question added to exam successfully.");
                errorLabel.setStyle("-fx-text-fill: green;");
            } catch (SQLException e) {
                e.printStackTrace();
                errorLabel.setText("Error adding question to exam: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            errorLabel.setText("No question selected for adding.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
