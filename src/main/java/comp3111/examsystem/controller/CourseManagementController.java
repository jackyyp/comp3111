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
    TableColumn<Course, String> courseIdColumn;
    @FXML
    TableColumn<Course, String> courseNameColumn;
    @FXML
    TableColumn<Course, String> departmentColumn;
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
        if (!courseId.isEmpty()) {
            sql.append(" AND courseId LIKE ?");
        }
        if (!courseName.isEmpty()) {
            sql.append(" AND courseName LIKE ?");
        }
        if (!department.isEmpty()) {
            sql.append(" AND department LIKE ?");
        }

        executePreparedStatement(sql.toString(), pstmt -> {
            int paramIndex = 1;
            if (!courseId.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + courseId + "%");
            }
            if (!courseName.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + courseName + "%");
            }
            if (!department.isEmpty()) {
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
            return true;
        });
    }
    /**
     * Deletes the selected course from the database.
     */
    @FXML
    public void deleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            setMessage(false, "No course selected.");
            return;
        }

        String sql = "DELETE FROM course WHERE courseId = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, selectedCourse.getCourseId());
            pstmt.executeUpdate();
            setMessage(true, "Delete Successful!");

            courseTable.getItems().remove(selectedCourse);
            courseTable.refresh();
            return true;
        });
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
            setMessage(false, "Error: Please check your inputs.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM course WHERE courseId = ?";
        boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, courseId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.getInt(1) > 0) {
                setMessage(false, "Error: Course ID already exists.");
                return true;
            }
            return false;
        });
        if (isDuplicate) {
            return;
        }

        String sql = "INSERT INTO course (courseId, courseName, department) VALUES (?, ?, ?)";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, courseId);
            pstmt.setString(2, courseName);
            pstmt.setString(3, department);
            pstmt.executeUpdate();

            setMessage(true, "Add Successful!");
            courseTable.getItems().add(new Course(courseId, courseName, department));
            courseTable.refresh();
            return true;
        });
    }
    /**
     * Updates the selected course in the database.
     */
    @FXML
    public void updateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            setMessage(false, "No course selected.");
            return;
        }

        String oldCourseId = selectedCourse.getCourseId();
        String courseId = courseIdField.getText();
        String courseName = courseNameField.getText();
        String department = departmentField.getText();
        boolean isAnyFieldUpdated = false;

        if (!courseId.isEmpty()) {
            if (!courseId.equals(selectedCourse.getCourseId())) {
                String checkSql = "SELECT COUNT(*) FROM course WHERE courseId = ?";
                boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
                    checkStmt.setString(1, courseId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.getInt(1) > 0) {
                        setMessage(false, "Error: Course ID already exists.");
                        return true;
                    }
                    return false;
                });
                if (isDuplicate) {
                    return;
                }
            }
            selectedCourse.setCourseId(courseId);
            isAnyFieldUpdated = true;
        }

        if (!courseName.isEmpty()) {
            selectedCourse.setCourseName(courseName);
            isAnyFieldUpdated = true;
        }

        if (!department.isEmpty()) {
            selectedCourse.setDepartment(department);
            isAnyFieldUpdated = true;
        }

        if (!isAnyFieldUpdated) {
            setMessage(false, "Error: No fields to update.");
            return;
        }

        String sql = "UPDATE course SET courseId=?, courseName=?, department=? WHERE courseId=?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, selectedCourse.getCourseId());
            pstmt.setString(2, selectedCourse.getCourseName());
            pstmt.setString(3, selectedCourse.getDepartment());
            pstmt.setString(4, oldCourseId);

            pstmt.executeUpdate();
            setMessage(true, "Update Successful!");
            courseTable.refresh();
            return true;
        });
    }
    /**
     * Loads the courses from the database and populates the table.
     */
    void loadCoursesFromDatabase() {
        String sql = "SELECT courseId, courseName, department FROM course";
        executePreparedStatement(sql, pstmt -> {
            ResultSet rs = pstmt.executeQuery();
            courseTable.getItems().clear();
            while (rs.next()) {
                String courseId = rs.getString("courseId");
                String courseName = rs.getString("courseName");
                String department = rs.getString("department");

                courseTable.getItems().add(new Course(courseId, courseName, department));
            }
            return true;
        });
    }
    /**
     * Executes a database operation using a connection from the database connection pool.
     *
     * @param operation the database operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executeDatabaseOperation(DatabaseOperation operation) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return operation.execute(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            setMessage(false, "Error connecting to the database.");
            return false;
        }
    }
    /**
     * Executes a prepared statement operation using a connection from the database connection pool.
     *
     * @param sql the SQL query to be executed
     * @param operation the prepared statement operation to be executed
     * @return true if the operation was successful, false otherwise
     */
    private boolean executePreparedStatement(String sql, PreparedStatementOperation operation) {
        return executeDatabaseOperation(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                return operation.execute(pstmt);
            } catch (SQLException e) {
                e.printStackTrace();
                setMessage(false, "Error connecting to the database.");
                return false;
            }
        });
    }
    /**
     * Functional interface for database operations.
     */
    @FunctionalInterface
    private interface DatabaseOperation {
        /**
         * Executes a database operation.
         *
         * @param conn the database connection
         * @return true if the operation was successful, false otherwise
         * @throws SQLException if a database access error occurs
         */
        boolean execute(Connection conn) throws SQLException;
    }
    /**
     * Functional interface for prepared statement operations.
     */
    @FunctionalInterface
    private interface PreparedStatementOperation {
        /**
         * Executes a prepared statement operation.
         *
         * @param pstmt the prepared statement
         * @return true if the operation was successful, false otherwise
         * @throws SQLException if a database access error occurs
         */
        boolean execute(PreparedStatement pstmt) throws SQLException;
    }
    /**
     * Sets the error message label with the specified message and style.
     *
     * @param success true if the operation was successful, false otherwise
     * @param message the message to be displayed
     */
    void setMessage(boolean success, String message) {
        errorMessageLbl.setText(message);
        if (success) {
            errorMessageLbl.setStyle("-fx-text-fill: green;");
        } else {
            errorMessageLbl.setStyle("-fx-text-fill: red;");
        }
        errorMessageLbl.setVisible(true);
    }
}