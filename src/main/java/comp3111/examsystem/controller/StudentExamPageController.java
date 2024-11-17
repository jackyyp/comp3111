package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.StudentControllerModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert.AlertType;
import lombok.Getter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller for the student exam page.
 * <p>
 * This controller is responsible for managing the student's exam experience, including loading questions,
 * updating the countdown timer, and submitting the exam when completed.
 *
 * @author WANG Shao Fu
 */
public class StudentExamPageController {

    /**
     * The label displaying the exam name.
     */
    @FXML
    Label examNameLabel;

    /**
     * The label displaying the current question number.
     */
    @FXML
    Label questionNumberLabel;

    /**
     * The label displaying the countdown timer.
     */
    @FXML
    Label countdownLabel;

    /**
     * The container for the question boxes.
     */
    @FXML
    VBox questionsContainer;

    /**
     * The button to display the previous question.
     */
    @FXML
    Button previousButton;

    /**
     * The button to display the next question.
     */
    @FXML
    Button nextButton;

    /**
     * The button to submit the exam.
     */
    @FXML
    Button submitButton;

    /**
     * The list view displaying the question numbers and text.
     */
    @FXML
    ListView<String> questionListView;

    /**
     * The list of questions.
     */
    List<Question> questions = new ArrayList<>();

    /**
     * The list of question boxes.
     */
    List<VBox> questionBoxes = new ArrayList<>();

    /**
     * The current question index.
     */
    int currentQuestionIndex = 0;

    /**
     * The confirmation alert for submitting the exam.
     */
    Alert confirmationAlert;

    /**
     * The timeline for the countdown timer.
     */
    private Timeline timeline;

    /**
     * The time remaining in seconds.
     */
    private long timeRemainingSeconds;

    /**
     * The time allowed for the exam in seconds.
     */
    private long timeAllowedSeconds;

    /**
     * The data model for the student.
     */
    private StudentControllerModel dataModel;

    /**
     * Sets the data model for the student.
     *
     * @param dataModel the data model for the student
     */
    public void setDataModel(StudentControllerModel dataModel) {
        this.dataModel = dataModel;
        System.out.println("Current Username: " + dataModel.getUsername());
        System.out.println("Current Exam ID: " + dataModel.getExamId());
    }

    /**
     * Gets the data model for the student.
     *
     * @return the data model for the student
     */
    public StudentControllerModel getDataModel() {
        return dataModel;
    }

    /**
     * Sets the exam name.
     *
     * @param examName the exam name
     */
    public void setExamName(String examName) {
        examNameLabel.setText(examName);
    }

    /**
     * Gets the exam name.
     *
     * @return the exam name
     */
    public String getExamName() {
        return examNameLabel.getText();
    }

    /**
     * Initializes the controller.
     */
    public void initialize() {
        // Disable the close button
        Platform.runLater(() -> {
            Stage stage = (Stage) examNameLabel.getScene().getWindow();
            if (stage != null) {
                stage.setOnCloseRequest(event -> {
                    event.consume(); // Prevents the window from closing
                    Alert alert = new Alert(AlertType.INFORMATION, "Closing is disabled during the exam.", ButtonType.OK);
                    alert.showAndWait();
                });
            }
        });
    }

    /**
     * Loads the questions for the exam.
     *
     * @param examId the ID of the exam
     */
    public void loadQuestions(int examId) {
        System.out.println("Fetching questions for exam ID: " + examId);

        // Fetch the exam details including time limit
        String examSql = "SELECT time_limit FROM exam WHERE id = ?";
        String questionSql = "SELECT q.id, q.text, q.option_a, q.option_b, q.option_c, q.option_d, q.is_single_choice, q.answer, q.score " + "FROM question q JOIN exam_question_link eql ON q.id = eql.question_id " + "WHERE eql.exam_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Fetch the time limit for the exam
            PreparedStatement examStmt = conn.prepareStatement(examSql);
            examStmt.setInt(1, examId);
            ResultSet examRs = examStmt.executeQuery();
            if (examRs.next()) {
                timeAllowedSeconds = examRs.getLong("time_limit");
                timeRemainingSeconds = timeAllowedSeconds;
            }

            // Fetch the questions for the exam
            PreparedStatement pstmt = conn.prepareStatement(questionSql);
            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String questionText = rs.getString("text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                boolean isSingleChoice = rs.getBoolean("is_single_choice");
                String answer = rs.getString("answer");
                int score = rs.getInt("score");

                System.out.println("Question: " + questionText);
                System.out.println("Options: A) " + optionA + " B) " + optionB + " C) " + optionC + " D) " + optionD);

                VBox questionBox = new VBox();
                questionBox.setSpacing(5);

                Label questionLabel = new Label(questionText);

                if (isSingleChoice) {
                    ToggleGroup group = new ToggleGroup();
                    RadioButton optionAButton = new RadioButton("A: " + optionA);
                    optionAButton.setUserData("A");
                    optionAButton.setToggleGroup(group);
                    RadioButton optionBButton = new RadioButton("B: " + optionB);
                    optionBButton.setUserData("B");
                    optionBButton.setToggleGroup(group);
                    RadioButton optionCButton = new RadioButton("C: " + optionC);
                    optionCButton.setUserData("C");
                    optionCButton.setToggleGroup(group);
                    RadioButton optionDButton = new RadioButton("D: " + optionD);
                    optionDButton.setUserData("D");
                    optionDButton.setToggleGroup(group);

                    questionBox.getChildren().addAll(questionLabel, optionAButton, optionBButton, optionCButton, optionDButton);

                    questions.add(new Question(id, questionBox, group, answer, score, true));
                } else {
                    CheckBox optionACheckBox = new CheckBox("A: " + optionA);
                    optionACheckBox.setUserData("A");
                    CheckBox optionBCheckBox = new CheckBox("B: " + optionB);
                    optionBCheckBox.setUserData("B");
                    CheckBox optionCCheckBox = new CheckBox("C: " + optionC);
                    optionCCheckBox.setUserData("C");
                    CheckBox optionDCheckBox = new CheckBox("D: " + optionD);
                    optionDCheckBox.setUserData("D");

                    questionBox.getChildren().addAll(questionLabel, optionACheckBox, optionBCheckBox, optionCCheckBox, optionDCheckBox);

                    questions.add(new Question(id, questionBox, optionACheckBox, optionBCheckBox, optionCCheckBox, optionDCheckBox, answer, score, false));
                }

                questionBoxes.add(questionBox);
            }

            if (!questionBoxes.isEmpty()) {
                questionsContainer.getChildren().setAll(questionBoxes.get(0));
                updateQuestionNumber();
            }

            updateNavigationButtons();
            // Populate the ListView with question numbers and text
            questionListView.setItems(FXCollections.observableArrayList(getQuestionListItems()));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Start the countdown timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemainingSeconds--;
            countdownLabel.setText("Time Remaining: " + formatTime(timeRemainingSeconds));

            if (timeRemainingSeconds <= 10) {
                countdownLabel.setStyle("-fx-text-fill: red;");
            } else {
                countdownLabel.setStyle("-fx-text-fill: black;");
            }

            if (timeRemainingSeconds <= 0) {
                timeline.stop();

                // Close the confirmation alert if it is showing
                if (confirmationAlert != null && confirmationAlert.isShowing()) {
                    confirmationAlert.hide();
                }

                submitExam(false); // Auto-submit without confirmation when time is up
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Gets the list of question list items.
     *
     * @return the list of question list items
     */
    private List<String> getQuestionListItems() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            items.add("Question " + (i + 1) + ": " + questions.get(i).getText());
        }
        return items;
    }

    /**
     * Formats the time in seconds into a string.
     *
     * @param seconds the time in seconds
     * @return the formatted time string
     */
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    /**
     * Shows the previous question.
     */
    @FXML
    void showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            questionsContainer.getChildren().setAll(questionBoxes.get(currentQuestionIndex));
            updateQuestionNumber();
            updateNavigationButtons();
        }
    }

    /**
     * Shows the next question.
     */
    @FXML
    void showNextQuestion() {
        if (currentQuestionIndex < questionBoxes.size() - 1) {
            currentQuestionIndex++;
            questionsContainer.getChildren().setAll(questionBoxes.get(currentQuestionIndex));
            updateQuestionNumber();
            updateNavigationButtons();
        }
    }

    /**
     * Updates the navigation buttons.
     */
    private void updateNavigationButtons() {
        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == questionBoxes.size() - 1);
    }

    /**
     * Updates the question number.
     */
    private void updateQuestionNumber() {
        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questionBoxes.size());
    }

    /**
     * Submits the exam.
     */
    @FXML
    void submitExam() {
        submitExam(true); // Call submitExam with true indicating confirmation is needed
    }

    /**
     * Submits the exam.
     *
     * @param requireConfirmation whether to require confirmation
     */
    void submitExam(boolean requireConfirmation) {
        Stage stage = (Stage) examNameLabel.getScene().getWindow();

        if (requireConfirmation) {
            confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Submission");
            confirmationAlert.setHeaderText("Are you sure you want to submit the exam?");
            confirmationAlert.setContentText("Once submitted, you cannot change your answers.");

            // Show the confirmation dialog and wait for user response
            Platform.runLater(() -> {
                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        proceedWithSubmission(stage);
                    }
                });
            });
        } else {
            proceedWithSubmission(stage);
        }
    }

    /**
     * Proceeds with the submission of the exam.
     *
     * @param stage the stage of the exam
     */
    private void proceedWithSubmission(Stage stage) {
        timeline.stop();
        int totalScore = 0;
        int correctAnswers = 0;

        for (Question question : questions) {
            String submittedAnswer = question.getSubmittedAnswer();
            String correctAnswer = question.getCorrectAnswer();
            System.out.println("Submitted Answer: " + submittedAnswer);
            System.out.println("Correct Answer: " + correctAnswer);

            if (question.isCorrectlyAnswered()) {
                correctAnswers++;
                totalScore += question.getScore();
            }
        }
        long timeSpent = timeAllowedSeconds - timeRemainingSeconds;
        int totalQuestions = questions.size();
        double precision = ((double) correctAnswers / totalQuestions) * 100;

        int finalCorrectAnswers = correctAnswers;
        int finalTotalScore = totalScore;

        saveGradeToDatabase(dataModel.getUsername(), dataModel.getExamId(), totalScore, (int) timeSpent);

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Exam Submitted");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Time spent is: %d\nYou got %d/%d correct.\nPrecision is %.2f%%\nYour total score is: %d", timeSpent, finalCorrectAnswers, totalQuestions, precision, finalTotalScore));

            alert.setOnCloseRequest(event -> loadStudentMainPage(stage));
            alert.showAndWait().ifPresent(alertResponse -> loadStudentMainPage(stage));
        });
        if (stage != null) {
            stage.setOnCloseRequest(null); // re-enable the close button
        }
    }

    /**
     * Saves the grade to the database.
     *
     * @param studentId the ID of the student
     * @param examId    the ID of the exam
     * @param score     the score of the student
     * @param timeSpent the time spent by the student
     */
    private void saveGradeToDatabase(String studentId, int examId, int score, int timeSpent) {
        String sql = "INSERT INTO grade (student_id, exam_id, score, time_spent) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, examId);
            pstmt.setInt(3, score);
            pstmt.setInt(4, timeSpent);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the student main page.
     *
     * @param stage the stage of the exam
     */
    private void loadStudentMainPage(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentMainUI.fxml"));
            fxmlLoader.setControllerFactory(param -> {
                StudentMainController controller = new StudentMainController();
                controller.setDataModel(dataModel); // Pass the dataModel to the new controller
                return controller;
            });
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the student main page.
     */
    void loadStudentMainPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentMainUI.fxml"));
            Stage stage = (Stage) examNameLabel.getScene().getWindow(); // Get the current stage
            Scene scene = new Scene(fxmlLoader.load());
            System.out.println(stage);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A question.
     */
    private static class Question {
        private final int id;
        private final VBox questionBox;
        private final ToggleGroup group;
        private final CheckBox optionACheckBox;
        private final CheckBox optionBCheckBox;
        private final CheckBox optionCCheckBox;
        private final CheckBox optionDCheckBox;
        /**
         * -- GETTER --
         *  Gets the correct answer for the question.
         *
         * @return the correct answer for the question
         */
        @Getter
        private final String correctAnswer;
        /**
         * -- GETTER --
         *  Gets the score for the question.
         *
         * @return the  score for the question
         */
        @Getter
        private final int score;
        private final boolean isSingleChoice;

        /**
         * Creates a question.
         *
         * @param id             the ID of the question
         * @param questionBox    the box for the question
         * @param group          the toggle group for the question
         * @param correctAnswer  the correct answer for the question
         * @param score          the score for the question
         * @param isSingleChoice whether the question is single choice
         */
        public Question(int id, VBox questionBox, ToggleGroup group, String correctAnswer, int score, boolean isSingleChoice) {
            this.id = id;
            this.questionBox = questionBox;
            this.group = group;
            this.optionACheckBox = null;
            this.optionBCheckBox = null;
            this.optionCCheckBox = null;
            this.optionDCheckBox = null;
            this.correctAnswer = correctAnswer;
            this.score = score;
            this.isSingleChoice = isSingleChoice;
        }

        /**
         * Creates a question.
         *
         * @param id              the ID of the question
         * @param questionBox     the box for the question
         * @param optionACheckBox the check box for option A
         * @param optionBCheckBox the check box for option B
         * @param optionCCheckBox the check box for option C
         * @param optionDCheckBox the check box for option D
         * @param correctAnswer   the correct answer for the question
         * @param score           the score for the question
         * @param isSingleChoice  whether the question is single choice
         */
        public Question(int id, VBox questionBox, CheckBox optionACheckBox, CheckBox optionBCheckBox, CheckBox optionCCheckBox, CheckBox optionDCheckBox, String correctAnswer, int score, boolean isSingleChoice) {
            this.id = id;
            this.questionBox = questionBox;
            this.group = null;
            this.optionACheckBox = optionACheckBox;
            this.optionBCheckBox = optionBCheckBox;
            this.optionCCheckBox = optionCCheckBox;
            this.optionDCheckBox = optionDCheckBox;
            this.correctAnswer = correctAnswer;
            this.score = score;
            this.isSingleChoice = isSingleChoice;
        }

        /**
         * Gets the text of the question.
         *
         * @return the text of the question
         */
        public String getText() {
            Label questionLabel = (Label) questionBox.getChildren().get(0);
            return questionLabel.getText();
        }

        /**
         * Checks if the question is correctly answered.
         *
         * @return whether the question is correctly answered
         */
        public boolean isCorrectlyAnswered() {
            String submittedAnswer = getSubmittedAnswer();
            return submittedAnswer.equalsIgnoreCase(correctAnswer);
        }

        /**
         * Gets the submitted answer for the question.
         *
         * @return the submitted answer for the question
         */
        public String getSubmittedAnswer() {
            StringBuilder submittedAnswers = new StringBuilder();
            if (isSingleChoice) {
                if (group != null && group.getSelectedToggle() != null) {
                    RadioButton selectedButton = (RadioButton) group.getSelectedToggle();
                    submittedAnswers.append(selectedButton.getUserData().toString());
                }
            } else {
                if (optionACheckBox.isSelected()) submittedAnswers.append("A");
                if (optionBCheckBox.isSelected()) submittedAnswers.append("B");
                if (optionCCheckBox.isSelected()) submittedAnswers.append("C");
                if (optionDCheckBox.isSelected()) submittedAnswers.append("D");
            }
            return submittedAnswers.toString();
        }


    }

    /**
     * Jumps to the selected question.
     */
    @FXML
    private void jumpToQuestion() {
        int selectedIndex = questionListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < questionBoxes.size()) {
            currentQuestionIndex = selectedIndex;
            questionsContainer.getChildren().setAll(questionBoxes.get(currentQuestionIndex));
            updateQuestionNumber();
            updateNavigationButtons();
        }
    }
}