package comp3111.examsystem.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import comp3111.examsystem.model.Question;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import comp3111.examsystem.database.DatabaseConnection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.HashSet;

/**
 * Controller class for handling the question management interface for teachers.
 * This class is responsible for managing the UI interactions for question management.
 *
 * author Wong Cheuk Yuen
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
public class TeacherQuestionController implements Initializable {

    @FXML
    public TextField questionField;
    @FXML
    public ComboBox<String> typeComboBox;
    @FXML
    public TextField scoreField;
    @FXML
    public TableView<Question> questionTable;
    @FXML
    public TableColumn<Question, String> questionColumn;
    @FXML
    public TableColumn<Question, String> optionAColumn;
    @FXML
    public TableColumn<Question, String> optionBColumn;
    @FXML
    public TableColumn<Question, String> optionCColumn;
    @FXML
    public TableColumn<Question, String> optionDColumn;
    @FXML
    public TableColumn<Question, String> answerColumn;
    @FXML
    public TableColumn<Question, String> typeColumn;
    @FXML
    public TableColumn<Question, Integer> scoreColumn;
    @FXML
    public TextField editQuestionField;
    @FXML
    public TextField editOptionAField;
    @FXML
    public TextField editOptionBField;
    @FXML
    public TextField editOptionCField;
    @FXML
    public TextField editOptionDField;
    @FXML
    public TextField editAnswerField;
    @FXML
    public ComboBox<String> editTypeComboBox;
    @FXML
    public TextField editScoreField;

    public ObservableList<Question> questionList;

    @FXML
    public Label errorLabel;

    /**
     * Initializes the controller class.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("Single", "Multiple"));
        editTypeComboBox.setItems(FXCollections.observableArrayList("Single", "Multiple"));

        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        optionAColumn.setCellValueFactory(new PropertyValueFactory<>("optionA"));
        optionBColumn.setCellValueFactory(new PropertyValueFactory<>("optionB"));
        optionCColumn.setCellValueFactory(new PropertyValueFactory<>("optionC"));
        optionDColumn.setCellValueFactory(new PropertyValueFactory<>("optionD"));
        answerColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        questionList = FXCollections.observableArrayList();
        questionTable.setItems(questionList);

        loadQuestionsFromDatabase();
    }

    /**
     * Loads questions from the database and populates the TableView.
     */
    public void loadQuestionsFromDatabase() {
        String sql = "SELECT * FROM question";

        executePreparedStatement(sql, pstmt -> {
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
            return true;
        });
    }

    /**
     * Handles the reset action.
     * Clears the input fields.
     */
    @FXML
    public void handleReset() {
        questionField.clear();
        typeComboBox.setValue(null);
        scoreField.clear();
    }

    /**
     * Handles the filter action.
     * Filters the questions based on the input fields.
     */
    @FXML
    public void handleFilter() {
        String questionText = questionField.getText();
        String type = typeComboBox.getValue();
        String scoreText = scoreField.getText();

        StringBuilder sql = new StringBuilder("SELECT * FROM question WHERE 1=1");
        if (!questionText.isEmpty()) {
            sql.append(" AND text LIKE ?");
        }
        if (type != null) {
            sql.append(" AND is_single_choice = ?");
        }
        if (!scoreText.isEmpty()) {
            sql.append(" AND score = ?");
        }

        executePreparedStatement(sql.toString(), pstmt -> {
            int paramIndex = 1;
            if (!questionText.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + questionText + "%");
            }
            if (type != null) {
                pstmt.setBoolean(paramIndex++, "Single".equals(type));
            }
            if (!scoreText.isEmpty()) {
                pstmt.setInt(paramIndex++, Integer.parseInt(scoreText));
            }
            ResultSet rs = pstmt.executeQuery();

            ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();
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
                filteredQuestions.add(question);
            }

            questionTable.setItems(filteredQuestions);
            return true;
        });
    }

    /**
     * Handles the update action.
     * Updates the selected question with the input fields.
     */
    @FXML
    public void handleUpdate() {
        Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            errorLabel.setText("No question selected for update.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        int oldId = selectedQuestion.getId();

        String prev_question = selectedQuestion.getText();
        String prev_option_a = selectedQuestion.getOptionA();
        String prev_option_b = selectedQuestion.getOptionB();
        String prev_option_c = selectedQuestion.getOptionC();
        String prev_option_d = selectedQuestion.getOptionD();
        String prev_answer = selectedQuestion.getAnswer();

        String updatedQuestion;
        String updatedOptionA;
        String updatedOptionB;
        String updatedOptionC;
        String updatedOptionD;
        String updatedAnswer;
        String updatedType;
        int updatedScore;

        if (editQuestionField.getText().isEmpty()) {
            updatedQuestion = prev_question;
        } else {
            updatedQuestion = editQuestionField.getText();
            if (updatedQuestion.equals(prev_question)) {
                    errorLabel.setText("The question already exists.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        updatedOptionA = editOptionAField.getText().isEmpty() ? prev_option_a : editOptionAField.getText();
        updatedOptionB = editOptionBField.getText().isEmpty() ? prev_option_b : editOptionBField.getText();
        updatedOptionC = editOptionCField.getText().isEmpty() ? prev_option_c : editOptionCField.getText();
        updatedOptionD = editOptionDField.getText().isEmpty() ? prev_option_d : editOptionDField.getText();
        updatedType = (editTypeComboBox.getValue() == null || editTypeComboBox.getValue().isEmpty()) ? selectedQuestion.getType() : editTypeComboBox.getValue();

        if (editAnswerField.getText().isEmpty()) {//if the answer field is empty, set the updated answer to be the same as the old one
            updatedAnswer = prev_answer;
        } else {
            updatedAnswer = editAnswerField.getText();//otherwise, first get the answer and check if it is legal
        }
        if ("Single".equals(updatedType)) {
            if (!editAnswerField.getText().matches("[ABCD]")) {//if type is single, check whether updated answer is either A,B,C, or D, otherwise error
                errorLabel.setText("Answer format incorrect");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        } else {
            if (!editAnswerField.getText().matches("[ABCD]{2,4}")) {//otherwise, check if the answer is a of length 2-4 and does not contain chars outside ABCD
                errorLabel.setText("Answer format incorrect");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }
        Set<Character> charSet = new HashSet<>();//check if the answer contains repeated choices like AA
        for (char c : editAnswerField.getText().toCharArray()) {
            if (!charSet.add(c)) {
                errorLabel.setText("Answer contains repeated choice.");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        if (editScoreField.getText().isEmpty()) {
            updatedScore = selectedQuestion.getScore();
        } else {
            try {
                updatedScore = Integer.parseInt(editScoreField.getText());
            } catch (NumberFormatException e) {
                errorLabel.setText("Score must be a valid integer.");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        String checkSql = "SELECT COUNT(*) FROM question WHERE text = ? AND id != ?";
        boolean isDup = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, updatedQuestion);
            checkStmt.setInt(2, oldId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    errorLabel.setText("The question already exists.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return true;
                }
            }
            return false;
        });
        if(isDup){
            return;
        }

        String sql = "UPDATE question SET text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, answer = ?, is_single_choice = ?, score = ? WHERE id = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, updatedQuestion);
            pstmt.setString(2, updatedOptionA);
            pstmt.setString(3, updatedOptionB);
            pstmt.setString(4, updatedOptionC);
            pstmt.setString(5, updatedOptionD);
            pstmt.setString(6, updatedAnswer);
            pstmt.setBoolean(7, "Single".equals(updatedType));
            pstmt.setInt(8, updatedScore);
            pstmt.setInt(9, oldId);

            pstmt.executeUpdate();
            errorLabel.setText("Question updated successfully.");
            errorLabel.setStyle("-fx-text-fill: green;");
            loadQuestionsFromDatabase();

            questionTable.refresh();
            return true;
        });
    }

    /**
     * Handles the refresh action.
     * Reloads the questions from the database.
     */
    @FXML
    public void handleRefresh() {
        String sql = "SELECT * FROM question";

        executePreparedStatement(sql, pstmt -> {
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
            return true;
        });
    }

    /**
     * Handles the add action.
     * Adds a new question to the database.
     */
    @FXML
    public void handleAdd() {
        String editquestion = editQuestionField.getText();
        String type = editTypeComboBox.getValue();
        String scoreText = editScoreField.getText();
        String optionA = editOptionAField.getText();
        String optionB = editOptionBField.getText();
        String optionC = editOptionCField.getText();
        String optionD = editOptionDField.getText();
        String Answer = editAnswerField.getText();

        if (editquestion.isEmpty() || type == null || scoreText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || Answer.isEmpty()) {

                errorLabel.setText("All fields must be filled out.");
                errorLabel.setStyle("-fx-text-fill: red;");

            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {

                errorLabel.setText("Score must be a valid integer.");
                errorLabel.setStyle("-fx-text-fill: red;");

            return;
        }

        if ("Single".equals(type)) {
            if (!editAnswerField.getText().matches("[ABCD]")) {
                errorLabel.setText("Answer format incorrect");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        } else {
            if (!editAnswerField.getText().matches("[ABCD]{2,4}")) {
                errorLabel.setText("Answer format incorrect");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        Set<Character> charSet = new HashSet<>();
        for (char c : editAnswerField.getText().toCharArray()) {
            if (!charSet.add(c)) {
                errorLabel.setText("Answer contains repeated choice.");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        String checkSql = "SELECT COUNT(*) FROM question WHERE text = ?";
        boolean isDup = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, editquestion);
            ResultSet rs = checkStmt.executeQuery();
                if (rs.getInt(1) > 0) {
                    errorLabel.setText("The question already exists.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return true;
                }
                return false;
        });
        if(isDup){
            return;
        }

        String sql = "INSERT INTO question (text, option_a, option_b, option_c, option_d, answer, is_single_choice, score) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, editquestion);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, Answer);
            pstmt.setBoolean(7, type.equals("Single"));
            pstmt.setInt(8, score);

            pstmt.executeUpdate();
            errorLabel.setText("Question added successfully.");
            errorLabel.setStyle("-fx-text-fill: green;");


            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int id = generatedKeys.getInt(1);
            Question newQuestion = new Question(id, editquestion, optionA, optionB, optionC, optionD, Answer, type, score);
            questionList.add(newQuestion);
            questionTable.setItems(questionList);

            return true;
        });
    }

    /**
     * Handles the delete action.
     * Deletes the selected question from the database.
     *
     */
    @FXML
    public void handleDelete() {
        Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            errorLabel.setText("Please select a question to delete.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }


        String sql = "DELETE FROM question WHERE id = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setInt(1, selectedQuestion.getId());
            pstmt.executeUpdate();

            ObservableList<Question> allQuestions = questionTable.getItems();
            allQuestions.remove(selectedQuestion);

            errorLabel.setText("Question deleted successfully.");
            errorLabel.setStyle("-fx-text-fill: green;");
            return true;
        });
    }
    /**
     * Executes a database operation using a connection from the database connection pool.
     *
     * @param operation the database operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executeDatabaseOperation(DatabaseOperation operation) {
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
    private boolean executePreparedStatement(String sql, PreparedStatementOperation operation) {
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
    interface DatabaseOperation {
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
    interface PreparedStatementOperation {
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
    void setMessage(boolean success, String message) {
        errorLabel.setText(message);
        if (success) {
            errorLabel.setStyle("-fx-text-fill: green;");
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
        }
        errorLabel.setVisible(true);
    }
}