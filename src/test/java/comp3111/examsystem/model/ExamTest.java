package comp3111.examsystem.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExamTest {

    @Test
    void testGetAndSetId() {
        Exam exam = new Exam(1, "Math", true, 60, "COMP3111");
        exam.setId(2);
        assertEquals(2, exam.getId());
    }

    @Test
    void testGetAndSetName() {
        Exam exam = new Exam(1, "Math", true, 60, "COMP3111");
        exam.setName("Physics");
        assertEquals("Physics", exam.getName());
    }

    @Test
    void testGetAndSetIsPublished() {
        Exam exam = new Exam(1, "Math", true, 60, "COMP3111");
        exam.setIsPublished(false);
        assertFalse(exam.getIsPublished());
    }

    @Test
    void testGetAndSetTimeLimit() {
        Exam exam = new Exam(1, "Math", true, 60, "COMP3111");
        exam.setTimeLimit(90);
        assertEquals(90, exam.getTimeLimit());
    }

    @Test
    void testGetAndSetCourse() {
        Exam exam = new Exam(1, "Math", true, 60, "COMP3111");
        exam.setCourse("COMP3112");
        assertEquals("COMP3112", exam.getCourse());
    }

    @Test
    void testIsPublished() {
        Exam exam = new Exam(1, "Math", true, 60, "COMP3111");
        assertTrue(exam.isPublished());
    }
}