package comp3111.examsystem.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Question {
    // Getters and setters for all properties
    // Getters and setters
    private int id;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String type;
    private int score;

    public Question(int id, String question, String optionA, String optionB, String optionC, String optionD, String answer, String type, int score) {
        this.id=id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        this.type = type;
        this.score = score;
    }

}
