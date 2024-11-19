package comp3111.examsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerControllerModelTest {

    private ManagerControllerModel model;

    @BeforeEach
    void setUp() {
        model = new ManagerControllerModel();
    }

    @Test
    void testGetAndSetUsername() {
        model.setUsername("managerUser");
        assertEquals("managerUser", model.getUsername());
    }

    @Test
    void testEquals() {
        ManagerControllerModel model1 = new ManagerControllerModel();
        model1.setUsername("managerUser");

        ManagerControllerModel model2 = new ManagerControllerModel();
        model2.setUsername("managerUser");

        assertEquals(model1, model2);
    }

    @Test
    void testCanEqual() {
        ManagerControllerModel model1 = new ManagerControllerModel();
        ManagerControllerModel model2 = new ManagerControllerModel();
        assertTrue(model1.canEqual(model2));
    }

    @Test
    void testHashCode() {
        ManagerControllerModel model1 = new ManagerControllerModel();
        model1.setUsername("managerUser");

        ManagerControllerModel model2 = new ManagerControllerModel();
        model2.setUsername("managerUser");

        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testToString() {
        ManagerControllerModel model = new ManagerControllerModel();
        model.setUsername("managerUser");
        String expected = "ManagerControllerModel(username=managerUser)";
        assertEquals(expected, model.toString());
    }
}