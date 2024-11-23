package comp3111.examsystem;

import comp3111.examsystem.controller.TeacherGradeStatisticController;
import comp3111.examsystem.controller.TeacherGradeStatisticController.Grade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherGradeStatisticControllerTest extends ApplicationTest {

    private TeacherGradeStatisticController controller;
    @BeforeEach
    public void setUp() {
        controller = new TeacherGradeStatisticController();
        controller.courseCombox = new ChoiceBox<>();
        controller.examCombox = new ChoiceBox<>();
        controller.studentCombox = new ChoiceBox<>();
        controller.gradeTable = new TableView<>();

        // Initialize axes for BarChart
        controller.categoryAxisBar = new CategoryAxis();
        controller.numberAxisBar = new NumberAxis();
        controller.barChart = new BarChart<>(controller.categoryAxisBar, controller.numberAxisBar);

        // Initialize axes for LineChart
        controller.categoryAxisLine = new CategoryAxis();
        controller.numberAxisLine = new NumberAxis();
        controller.lineChart = new LineChart<>(controller.categoryAxisLine, controller.numberAxisLine);

        controller.pieChart = new PieChart();

        controller.studentColumn = new TableColumn<>();
        controller.courseColumn = new TableColumn<>();
        controller.examColumn = new TableColumn<>();
        controller.scoreColumn = new TableColumn<>();
        controller.fullScoreColumn = new TableColumn<>();
        controller.timeSpendColumn = new TableColumn<>();
    }

    @Test
    public void testInitialize() {
        controller.initialize();
        assertFalse(controller.barChart.getData().isEmpty());
    }

    @Test
    public void testQueryWithNoFilters() {
        controller.query();
        assertTrue(controller.gradeTable.getItems().isEmpty());
    }

    @Test
    public void testQueryWithCourseFilter() {
        controller.courseCombox.setValue("Course1");
        controller.query();
        assertTrue(controller.gradeTable.getItems().isEmpty());
    }

    @Test
    public void testQueryWithExamFilter() {
        controller.examCombox.setValue("Exam1");
        controller.query();
        assertTrue(controller.gradeTable.getItems().isEmpty());
    }

    @Test
    public void testQueryWithStudentFilter() {
        controller.studentCombox.setValue("Student1");
        controller.query();
        assertTrue(controller.gradeTable.getItems().isEmpty());
    }

    @Test
    public void testLoadChart() {
        ObservableList<Grade> gradeList = FXCollections.observableArrayList(
                new Grade("Student1", "Course1", "Exam1", 80, 100, 60),
                new Grade("Student2", "Course1", "Exam1", 90, 100, 50)
        );
        controller.gradeList.setAll(gradeList);
        controller.loadChart();
        assertEquals(1, controller.barChart.getData().size());
        assertEquals(2, controller.pieChart.getData().size());
        assertEquals(1, controller.lineChart.getData().size());
    }
}