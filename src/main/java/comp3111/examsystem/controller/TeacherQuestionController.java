package comp3111.examsystem.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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

        String sql = "SELECT * FROM question WHERE text LIKE ? AND is_single_choice = ? AND score = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + questionText + "%");
            pstmt.setBoolean(2, "Single".equals(type));
            pstmt.setInt(3, Integer.parseInt(scoreText));

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the update action.
     * Updates the selected question with the input fields.
     */
    @FXML
    public void handleUpdate() {
        Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
        if (selectedQuestion == null) {
            Platform.runLater(() -> {
                errorLabel.setText("No question selected for update.");
                errorLabel.setStyle("-fx-text-fill: red;");
            });
            return;
        }
        String prev_question= selectedQuestion.getText();
        String prev_option_a= selectedQuestion.getOptionA();
        String prev_option_b= selectedQuestion.getOptionB();
        String prev_option_c= selectedQuestion.getOptionC();
        String prev_option_d= selectedQuestion.getOptionD();
        String prev_answer= selectedQuestion.getAnswer();
        String updatedQuestion;
        String updatedOptionA;
        String updatedOptionB;
        String updatedOptionC;
        String updatedOptionD;
        String updatedAnswer;
        String updatedType;
        int updatedScore;
        if (selectedQuestion != null) {
            if(editQuestionField.getText().isEmpty()){
                updatedQuestion = prev_question;
            } else{
                updatedQuestion=editQuestionField.getText();
                if(updatedQuestion.equals(prev_question)){
                    Platform.runLater(() -> {
                        errorLabel.setText("The question already exists.");
                        errorLabel.setStyle("-fx-text-fill: red;");
                    });
                    return;
                }
            }
            if(editOptionAField.getText().isEmpty()){
                updatedOptionA=prev_option_a;
            } else{
                updatedOptionA=editOptionAField.getText();
            }
            if(editOptionBField.getText().isEmpty()){
                updatedOptionB=prev_option_b;
            } else{
                updatedOptionB=editOptionBField.getText();
            }
            if(editOptionCField.getText().isEmpty()){
                updatedOptionC=prev_option_c;
            } else{
                updatedOptionC=editOptionCField.getText();
            }
            if(editOptionDField.getText().isEmpty()){
                updatedOptionD=prev_option_d;
            } else{
                updatedOptionD=editOptionDField.getText();
            }
            if (editTypeComboBox.getValue() == null || editTypeComboBox.getValue().isEmpty()) {
                updatedType = selectedQuestion.getType();
            } else {
                updatedType = editTypeComboBox.getValue();
            }
            if(editAnswerField.getText().isEmpty()){
                updatedAnswer=prev_answer;
            } else{
                if ("Single".equals(updatedType)) {
                    if (!editAnswerField.getText().matches("[ABCD]")) {
                        errorLabel.setText("Answer format incorrect");
                        errorLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                } else{
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
                updatedAnswer=editAnswerField.getText();
            }
            if(editScoreField.getText().isEmpty()){
                updatedScore=selectedQuestion.getScore();
            } else {
                try {
                    updatedScore = Integer.parseInt(editScoreField.getText());
                } catch (NumberFormatException e) {
                    errorLabel.setText("Score must be a valid integer.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                updatedScore = Integer.parseInt(editScoreField.getText());
            }

            String checkSql = "SELECT COUNT(*) FROM question WHERE text = ? AND id != ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                checkStmt.setString(1, updatedQuestion);
                checkStmt.setInt(2, selectedQuestion.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    errorLabel.setText("The question already exists.");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                errorLabel.setText("Database error: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }


            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE question SET text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, answer = ?, is_single_choice = ?, score = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, updatedQuestion);
                    pstmt.setString(2, updatedOptionA);
                    pstmt.setString(3, updatedOptionB);
                    pstmt.setString(4, updatedOptionC);
                    pstmt.setString(5, updatedOptionD);
                    pstmt.setString(6, updatedAnswer);
                    pstmt.setBoolean(7, "Single".equals(updatedType));
                    pstmt.setInt(8, updatedScore);
                    pstmt.setInt(9, selectedQuestion.getId());

                    //int affectedRows =
                    pstmt.executeUpdate();
                    //if (affectedRows >0) {
                    selectedQuestion.setText(updatedQuestion);
                    selectedQuestion.setOptionA(updatedOptionA);
                    selectedQuestion.setOptionB(updatedOptionB);
                    selectedQuestion.setOptionC(updatedOptionC);
                    selectedQuestion.setOptionD(updatedOptionD);
                    selectedQuestion.setAnswer(updatedAnswer);
                    selectedQuestion.setType(updatedType);
                    selectedQuestion.setScore(updatedScore);
                    questionTable.refresh();
                    errorLabel.setText("Question updated successfully.");
                    errorLabel.setStyle("-fx-text-fill: green;");

                }
            } catch (SQLException e) {
                e.printStackTrace();
                errorLabel.setText("Error updating the question.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            errorLabel.setText("No question selected.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handles the refresh action.
     * Reloads the questions from the database.
     */
    @FXML
    public void handleRefresh() {
        String sql = "SELECT * FROM question";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
            Platform.runLater(() -> {
                errorLabel.setText("All fields must be filled out.");
                errorLabel.setStyle("-fx-text-fill: red;");
            });
            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                errorLabel.setText("Score must be a valid integer.");
                errorLabel.setStyle("-fx-text-fill: red;");
            });
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
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, editquestion);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                errorLabel.setText("The question already exists.");
                errorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sql = "INSERT INTO question (text, option_a, option_b, option_c, option_d, answer, is_single_choice, score) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, editquestion);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, Answer);
            pstmt.setBoolean(7, type.equals("Single"));
            pstmt.setInt(8, score);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                errorLabel.setText("Question added successfully.");
                errorLabel.setStyle("-fx-text-fill: green;");
            } else {
                errorLabel.setText("Failed to add the question.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                Question newQuestion = new Question(id, editquestion, optionA, optionB, optionC, optionD, Answer, type, score);
                questionList.add(newQuestion);
                questionTable.setItems(questionList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handles the delete action.
     * Deletes the selected question from the database.
     *
     * @param event the action event triggered by the delete button
     */
    @FXML
    public void handleDelete(ActionEvent event) {
        Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            errorLabel.setText("Please select a question to delete.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM question WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedQuestion.getId());
            pstmt.executeUpdate();

            ObservableList<Question> allQuestions = questionTable.getItems();
            allQuestions.remove(selectedQuestion);

            errorLabel.setText("Question deleted successfully.");
            errorLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Error deleting question.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
}