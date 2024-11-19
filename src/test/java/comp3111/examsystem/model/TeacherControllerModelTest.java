package comp3111.examsystem.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeacherControllerModelTest {

    @Test
    void testGetAndSetUsername() {
        TeacherControllerModel model = new TeacherControllerModel();
        model.setUsername("teacherUser");
        assertEquals("teacherUser", model.getUsername());
    }

    @Test
    void testGetAndSetExamId() {
        TeacherControllerModel model = new TeacherControllerModel();
        model.setExamId(456);
        assertEquals(456, model.getExamId());
    }

    @Test
    void testEquals() {
        TeacherControllerModel model1 = new TeacherControllerModel();
        model1.setUsername("teacherUser");
        model1.setExamId(456);

        TeacherControllerModel model2 = new TeacherControllerModel();
        model2.setUsername("teacherUser");
        model2.setExamId(456);

        assertEquals(model1, model2);
    }

    @Test
    void testCanEqual() {
        TeacherControllerModel model1 = new TeacherControllerModel();
        TeacherControllerModel model2 = new TeacherControllerModel();
        assertTrue(model1.canEqual(model2));
    }

    @Test
    void testHashCode() {
        TeacherControllerModel model1 = new TeacherControllerModel();
        model1.setUsername("teacherUser");
        model1.setExamId(456);

        TeacherControllerModel model2 = new TeacherControllerModel();
        model2.setUsername("teacherUser");
        model2.setExamId(456);

        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testToString() {
        TeacherControllerModel model = new TeacherControllerModel();
        model.setUsername("teacherUser");
        model.setExamId(456);
        String expected = "TeacherControllerModel(username=teacherUser, examId=456)";
        assertEquals(expected, model.toString());
    }
}