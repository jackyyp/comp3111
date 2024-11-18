package comp3111.examsystem.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentControllerModelTest {

    @Test
    void testGetAndSetUsername() {
        StudentControllerModel model = new StudentControllerModel();
        model.setUsername("testUser");
        assertEquals("testUser", model.getUsername());
    }

    @Test
    void testGetAndSetExamId() {
        StudentControllerModel model = new StudentControllerModel();
        model.setExamId(123);
        assertEquals(123, model.getExamId());
    }

    @Test
    void testEquals() {
        StudentControllerModel model1 = new StudentControllerModel();
        model1.setUsername("testUser");
        model1.setExamId(123);

        StudentControllerModel model2 = new StudentControllerModel();
        model2.setUsername("testUser");
        model2.setExamId(123);

        assertEquals(model1, model2);
    }

    @Test
    void testCanEqual() {
        StudentControllerModel model1 = new StudentControllerModel();
        StudentControllerModel model2 = new StudentControllerModel();
        assertTrue(model1.canEqual(model2));
    }

    @Test
    void testHashCode() {
        StudentControllerModel model1 = new StudentControllerModel();
        model1.setUsername("testUser");
        model1.setExamId(123);

        StudentControllerModel model2 = new StudentControllerModel();
        model2.setUsername("testUser");
        model2.setExamId(123);

        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testToString() {
        StudentControllerModel model = new StudentControllerModel();
        model.setUsername("testUser");
        model.setExamId(123);
        String expected = "StudentControllerModel(username=testUser, examId=123)";
        assertEquals(expected, model.toString());
    }
}