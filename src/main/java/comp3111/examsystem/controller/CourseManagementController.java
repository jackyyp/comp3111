package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for managing the course functionality.
 *
 * This class handles the UI and operations for managing courses.
 * It includes methods for navigating to different sections and performing various tasks.
 *
 * @author Poon Chin Hung
 * @version 1.0
 */
public class CourseManagementController {

    @Data
    @AllArgsConstructor
    public static class Course {
        private String courseId;
        private String courseName;
        private String department;
    }

    @FXML
    public TextField courseIdFilter;
    @FXML
    public TextField courseNameFilter;
    @FXML
    public TextField departmentFilter;
    @FXML
    public TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, String> courseIdColumn, courseNameColumn, departmentColumn;
    @FXML
    public TextField courseIdField;
    @FXML
    public TextField courseNameField;
    @FXML
    public TextField departmentField;
    @FXML
    public Label errorMessageLbl;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        courseTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double columnWidth = tableWidth / 3;

            courseIdColumn.setPrefWidth(columnWidth);
            courseNameColumn.setPrefWidth(columnWidth);
            departmentColumn.setPrefWidth(columnWidth);
        });

        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        courseTable.setItems(courseList);
        loadCoursesFromDatabase();
    }

    /**
     * Resets the filter fields and reloads the courses from the database.
     */
    @FXML
    public void resetFilter() {
        courseIdFilter.clear();
        courseNameFilter.clear();
        departmentFilter.clear();
        loadCoursesFromDatabase();
    }

    /**
     * Filters the courses based on the filter fields.
     */
    @FXML
    public void filterCourses() {
        String courseId = courseIdFilter.getText();
        String courseName = courseNameFilter.getText();
        String department = departmentFilter.getText();

        StringBuilder sql = new StringBuilder("SELECT courseId, courseName, department FROM course WHERE 1=1");
        if (courseId != null && !courseId.isEmpty()) {
            sql.append(" AND courseId LIKE ?");
        }
        if (courseName != null && !courseName.isEmpty()) {
            sql.append(" AND courseName LIKE ?");
        }
        if (department != null && !department.isEmpty()) {
            sql.append(" AND department LIKE ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (courseId != null && !courseId.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + courseId + "%");
            }
            if (courseName != null && !courseName.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + courseName + "%");
            }
            if (department != null && !department.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + department + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            courseTable.getItems().clear();

            while (rs.next()) {
                String resultCourseId = rs.getString("courseId");
                String resultCourseName = rs.getString("courseName");
                String resultDepartment = rs.getString("department");

                courseTable.getItems().add(new Course(resultCourseId, resultCourseName, resultDepartment));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected course from the database.
     */
    @FXML
    public void deleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            String sql = "DELETE FROM course WHERE courseId = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, selectedCourse.getCourseId());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    courseTable.getItems().remove(selectedCourse);
                } else {
                    errorMessageLbl.setText("Failed to delete course.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                errorMessageLbl.setText("Error connecting to the database.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
            }
        } else {
            errorMessageLbl.setText("No course selected.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

    /**
     * Adds a new course to the database.
     */
    @FXML
    public void addCourse() {
        String courseId = courseIdField.getText();
        String courseName = courseNameField.getText();
        String department = departmentField.getText();

        if (courseId.isEmpty() || courseName.isEmpty() || department.isEmpty()) {
            errorMessageLbl.setText("Error: Please check your inputs.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM course WHERE courseId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, courseId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                errorMessageLbl.setText("Error: Course ID already exists.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
            return;
        }

        String sql = "INSERT INTO course (courseId, courseName, department) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            pstmt.setString(2, courseName);
            pstmt.setString(3, department);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                errorMessageLbl.setText("Add Successful!");
                errorMessageLbl.setStyle("-fx-text-fill: green;");
                errorMessageLbl.setVisible(true);
                courseTable.getItems().add(new Course(courseId, courseName, department));
            } else {
                errorMessageLbl.setText("Failed to add course.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

    /**
     * Updates the selected course in the database.
     */
    @FXML
    public void updateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            String oldCourseId = selectedCourse.getCourseId();
            String courseId = courseIdField.getText();
            String courseName = courseNameField.getText();
            String department = departmentField.getText();

            boolean isAnyFieldUpdated = false;
            if (courseId != null && !courseId.isEmpty()) {
                if (!courseId.equals(oldCourseId)) {
                    String checkSql = "SELECT COUNT(*) FROM course WHERE courseId = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                        checkStmt.setString(1, courseId);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            errorMessageLbl.setText("Error: Course ID already exists.");
                            errorMessageLbl.setStyle("-fx-text-fill: red;");
                            errorMessageLbl.setVisible(true);
                            return;
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        errorMessageLbl.setText("Error connecting to the database.");
                        errorMessageLbl.setStyle("-fx-text-fill: red;");
                        errorMessageLbl.setVisible(true);
                        return;
                    }
                }
                selectedCourse.setCourseId(courseId);
                isAnyFieldUpdated = true;
            }
            if (courseName != null && !courseName.isEmpty()) {
                selectedCourse.setCourseName(courseName);
                isAnyFieldUpdated = true;
            }
            if (department != null && !department.isEmpty()) {
                selectedCourse.setDepartment(department);
                isAnyFieldUpdated = true;
            }
            if (!isAnyFieldUpdated) {
                errorMessageLbl.setText("Error: No fields to update.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }

            String sql = "UPDATE course SET courseId=?, courseName = ?, department = ? WHERE courseId = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, selectedCourse.getCourseId());
                pstmt.setString(2, selectedCourse.getCourseName());
                pstmt.setString(3, selectedCourse.getDepartment());
                pstmt.setString(4, oldCourseId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    errorMessageLbl.setText("Update Successful!");
                    errorMessageLbl.setStyle("-fx-text-fill: green;");
                    errorMessageLbl.setVisible(true);
                    courseTable.refresh();
                } else {
                    errorMessageLbl.setText("Failed to update course.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                errorMessageLbl.setText("Error connecting to the database.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
            }
        } else {
            errorMessageLbl.setText("No course selected.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

    /**
     * Loads the courses from the database and populates the table.
     */
    private void loadCoursesFromDatabase() {
        String sql = "SELECT courseId, courseName, department FROM course";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            courseTable.getItems().clear();

            while (rs.next()) {
                String courseId = rs.getString("courseId");
                String courseName = rs.getString("courseName");
                String department = rs.getString("department");

                courseTable.getItems().add(new Course(courseId, courseName, department));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}