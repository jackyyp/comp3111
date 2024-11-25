package comp3111.examsystem.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a question in the exam system.
 * @author: Wong Cheuk Yuen
 */
@Getter
@Setter
public class Question {
    private int id;
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String type;
    private int score;

    /**
     * Constructs a new Question with the specified details.
     *
     * @param id the unique identifier of the question
     * @param question the text of the question
     * @param optionA the text for option A
     * @param optionB the text for option B
     * @param optionC the text for option C
     * @param optionD the text for option D
     * @param answer the correct answer for the question
     * @param type the type of the question
     * @param score the score for the question
     */
    public Question(int id, String question, String optionA, String optionB, String optionC, String optionD, String answer, String type, int score) {
        this.id = id;
        this.text = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        this.type = type;
        this.score = score;
    }

    /**
     * Gets the text of the question.
     *
     * @return the text of the question
     */
    public String getQuestion() {
        return text;
    }
}