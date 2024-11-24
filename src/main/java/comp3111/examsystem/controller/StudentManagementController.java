package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.collections.FXCollections;
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
 * Controller class for managing the student functionality.
 *
 * This class handles the UI and operations for managing students.
 * It includes methods for navigating to different sections and performing various tasks.
 *
 * @author Poon Chin Hung
 * @version 1.0
 */
public class StudentManagementController {

    @Data
    @AllArgsConstructor
    public static class Student {
        private String username;
        private String name;
        private int age;
        private String gender;
        private String department;
        private String password;
    }

    @FXML
    public TextField usernameFilter;
    @FXML
    public TextField nameFilter;
    @FXML
    public TextField departmentFilter;

    @FXML
    public TableView<Student> studentTable;
    @FXML
    TableColumn<Student, String> usernameColumn;
    @FXML
    TableColumn<Student, String> nameColumn;
    @FXML
    TableColumn<Student, Integer> ageColumn;
    @FXML
    TableColumn<Student, String> genderColumn;
    @FXML
    TableColumn<Student, String> departmentColumn;
    @FXML
    TableColumn<Student, String> passwordColumn;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField ageField;
    @FXML
    public ComboBox<String> genderComboBox;
    @FXML
    public TextField departmentField;
    @FXML
    public TextField passwordField;
    @FXML
    public Label errorMessageLbl;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        ageField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        studentTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double columnWidth = tableWidth / 6;

            usernameColumn.setPrefWidth(columnWidth);
            nameColumn.setPrefWidth(columnWidth);
            ageColumn.setPrefWidth(columnWidth);
            genderColumn.setPrefWidth(columnWidth);
            departmentColumn.setPrefWidth(columnWidth);
            passwordColumn.setPrefWidth(columnWidth);
        });

        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        loadStudentsFromDatabase();
    }
    /**
     * Resets the filter fields and reloads the students from the database.
     */
    @FXML
    public void resetFilter() {
        usernameFilter.clear();
        nameFilter.clear();
        departmentFilter.clear();
        loadStudentsFromDatabase();
        studentTable.refresh();
    }
    /**
     * Filters the students based on the filter fields.
     */
    @FXML
    public void filterStudents() {
        String username = usernameFilter.getText();
        String name = nameFilter.getText();
        String department = departmentFilter.getText();

        StringBuilder sql = new StringBuilder("SELECT username, name, age, gender, department, password FROM student WHERE 1=1");
        if (!username.isEmpty()) {
            sql.append(" AND username LIKE ?");
        }
        if (!name.isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (!department.isEmpty()) {
            sql.append(" AND department LIKE ?");
        }

        executePreparedStatement(sql.toString(), pstmt -> {
            int paramIndex = 1;
            if (!username.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + username + "%");
            }
            if (!name.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + name + "%");
            }
            if (!department.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + department + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            studentTable.getItems().clear();

            while (rs.next()) {
                String resultUsername = rs.getString("username");
                String resultName = rs.getString("name");
                int resultAge = rs.getInt("age");
                String resultGender = rs.getString("gender");
                String resultDepartment = rs.getString("department");
                String resultPassword = rs.getString("password");

                studentTable.getItems().add(new Student(resultUsername, resultName, resultAge, resultGender, resultDepartment, resultPassword));
            }
            studentTable.refresh();
            return true;
        });
    }
    /**
     * Deletes the selected student from the database.
     */
    @FXML
    public void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            setMessage(false,"No student selected.");
            return;
        }

        String sql = "DELETE FROM student WHERE username = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, selectedStudent.getUsername());
            pstmt.executeUpdate();
            setMessage(true,"Delete Successful!");

            studentTable.getItems().remove(selectedStudent);
            studentTable.refresh();
            return true;

        });

    }
    /**
     * Adds a new student to the database.
     */
    @FXML
    public void addStudent() {
        String username = usernameField.getText();
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String department = departmentField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || name.isEmpty() || gender == null || age.isEmpty() || department.isEmpty() || password.isEmpty()) {
            setMessage(false, "Error: Please check your inputs.");
            return;
        }
        if (Integer.parseInt(age) < 0) {
            setMessage(false,"Error: Please check your inputs.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM student WHERE username = ?";
        boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.getInt(1) > 0) {
                        setMessage(false,"Error: Username already exists.");
                       return true;
                    }
                    return false;
        });
        if(isDuplicate){
            return;
        }

        String sql = "INSERT INTO student (username, name, gender, age, department, password) VALUES (?, ?, ?, ?, ?, ?)";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, department);
            pstmt.setString(6, password);
            pstmt.executeUpdate();

            setMessage(true,"Add Successful!");
            studentTable.getItems().add(new Student(username, name, Integer.parseInt(age), gender, department, password));
            studentTable.refresh();
            return true;

        });

    }
    /**
     * Updates the selected student in the database.
     */
    @FXML
    public void updateStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            setMessage(false,"No student selected.");
            return;
        }

        String oldUsername = selectedStudent.getUsername();
        String username = usernameField.getText();
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String department = departmentField.getText();
        String password = passwordField.getText();
        boolean isAnyFieldUpdated = false;

        if (!username.isEmpty()) {
            //changing to different name , check if name will be duplicate
            if (!username.equals(selectedStudent.getUsername())) {
                String checkSql = "SELECT COUNT(*) FROM student WHERE username = ?";
                boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.getInt(1) > 0) {
                        setMessage(false,"Error: Duplicate username with other student.");
                        return true;
                    }
                    return false;
                });
                if(isDuplicate){
                    return;
                }
            }

            selectedStudent.setUsername(username);
            isAnyFieldUpdated = true;
        }

        if (!name.isEmpty()) {
            selectedStudent.setName(name);
            isAnyFieldUpdated = true;
        }

        if (!age.isEmpty()) {
            if (Integer.parseInt(age) < 0) {
                setMessage(false,"Error: Please check your inputs.");
                return;
            }

            selectedStudent.setAge(Integer.parseInt(age));
            isAnyFieldUpdated = true;
        }
        if (gender != null) {
            selectedStudent.setGender(gender);
            isAnyFieldUpdated = true;
        }
        if (!department.isEmpty()) {
            selectedStudent.setDepartment(department);
            isAnyFieldUpdated = true;
        }
        if (!password.isEmpty()) {
            selectedStudent.setPassword(password);
            isAnyFieldUpdated = true;
        }

        if (!isAnyFieldUpdated) {
            setMessage(false,"Error: No fields to update.");
            return;
        }


        String sql = "UPDATE student SET username=?, name = ?, age = ?, gender = ?, department = ?, password = ? WHERE username = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, selectedStudent.getUsername());
            pstmt.setString(2, selectedStudent.getName());
            pstmt.setInt(3, selectedStudent.getAge());
            pstmt.setString(4, selectedStudent.getGender());
            pstmt.setString(5, selectedStudent.getDepartment());
            pstmt.setString(6, selectedStudent.getPassword());
            pstmt.setString(7, oldUsername);

            pstmt.executeUpdate();

            setMessage(true,"Update Successful!");
            studentTable.refresh();
            return true;
        });
    }
    /**
     * Loads the students from the database and populates the table.
     */
    void loadStudentsFromDatabase() {
        String sql = "SELECT username, name, age, gender, department, password FROM student";
        executePreparedStatement(sql, pstmt -> {
            ResultSet rs = pstmt.executeQuery();
            studentTable.getItems().clear();
            while (rs.next()) {
                String username = rs.getString("username");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String department = rs.getString("department");
                String password = rs.getString("password");

                studentTable.getItems().add(new Student(username, name, age, gender, department, password));
            }
            return true;
        });

        studentTable.refresh();
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
            setMessage(false,"Error connecting to the database.");
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
                setMessage(false,"Error connecting to the database.");
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
    void setMessage(boolean success, String message){
        errorMessageLbl.setText(message);
        if(success){
            errorMessageLbl.setStyle("-fx-text-fill: green;");
        }else{
            errorMessageLbl.setStyle("-fx-text-fill: red;");
        }
        errorMessageLbl.setVisible(true);
    }


}