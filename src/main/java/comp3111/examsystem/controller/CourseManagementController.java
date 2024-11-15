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

public class CourseManagementController {

    @Data
    @AllArgsConstructor
    public static class Course {
        private String courseId;
        private String courseName;
        private String department;
    }

    @FXML
    private TextField courseIdFilter, courseNameFilter, departmentFilter;
    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, String> courseIdColumn, courseNameColumn, departmentColumn;
    @FXML
    private TextField courseIdField, courseNameField, departmentField;
    @FXML
    private Label errorMessageLbl;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();

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

    @FXML
    private void resetFilter() {
        courseIdFilter.clear();
        courseNameFilter.clear();
        departmentFilter.clear();
        loadCoursesFromDatabase();
    }

    @FXML
    private void filterCourses() {
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

    @FXML
    private void deleteCourse() {
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

    @FXML
    private void addCourse() {
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

    @FXML
    private void updateCourse() {
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