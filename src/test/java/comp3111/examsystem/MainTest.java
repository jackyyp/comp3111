package comp3111.examsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainTest extends ApplicationTest {

    private Main main;

    @Override
    public void start(Stage stage) {
        main = new Main();
        main.start(stage);
    }

    @Test
    void testMain() {
        assertNotNull(main);
    }
}