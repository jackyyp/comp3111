package comp3111.examsystem.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testGetAndSetId() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setId(2);
        assertEquals(2, question.getId());
    }

    @Test
    void testGetAndSetText() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setText("What is 3+3?");
        assertEquals("What is 3+3?", question.getText());
    }

    @Test
    void testGetAndSetOptionA() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setOptionA("5");
        assertEquals("5", question.getOptionA());
    }

    @Test
    void testGetAndSetOptionB() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setOptionB("6");
        assertEquals("6", question.getOptionB());
    }

    @Test
    void testGetAndSetOptionC() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setOptionC("7");
        assertEquals("7", question.getOptionC());
    }

    @Test
    void testGetAndSetOptionD() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setOptionD("8");
        assertEquals("8", question.getOptionD());
    }

    @Test
    void testGetAndSetAnswer() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setAnswer("C");
        assertEquals("C", question.getAnswer());
    }

    @Test
    void testGetAndSetType() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setType("Short Answer");
        assertEquals("Short Answer", question.getType());
    }

    @Test
    void testGetAndSetScore() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        question.setScore(10);
        assertEquals(10, question.getScore());
    }

    @Test
    void testGetQuestion() {
        Question question = new Question(1, "What is 2+2?", "1", "2", "3", "4", "D", "MCQ", 5);
        assertEquals("What is 2+2?", question.getQuestion());
    }
}