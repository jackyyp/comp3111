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
    void testEquals() {
        TeacherControllerModel model1 = new TeacherControllerModel();
        model1.setUsername("teacherUser");
        TeacherControllerModel model2 = new TeacherControllerModel();
        model2.setUsername("teacherUser");

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
        TeacherControllerModel model2 = new TeacherControllerModel();
        model2.setUsername("teacherUser");
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testToString() {
        TeacherControllerModel model = new TeacherControllerModel();
        model.setUsername("teacherUser");
        String expected = "TeacherControllerModel(username=teacherUser, name=null)";
        assertEquals(expected, model.toString());
    }
}