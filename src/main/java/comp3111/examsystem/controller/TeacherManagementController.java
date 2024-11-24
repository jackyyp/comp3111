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
 * Controller class for managing the teacher functionality.
 *
 * This class handles the UI and operations for managing teachers.
 * It includes methods for navigating to different sections and performing various tasks.
 *
 * @author Poon Chin Hung
 * @version 1.0
 */
public class TeacherManagementController {

    @Data
    @AllArgsConstructor
    public static class Teacher {
        private String username;
        private String name;
        private String gender;
        private int age;
        private String position;
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
    public TableView<Teacher> teacherTable;
    @FXML
    TableColumn<Teacher, String> usernameColumn;
    @FXML
    TableColumn<Teacher, String> nameColumn;
    @FXML
    TableColumn<Teacher, String> genderColumn;
    @FXML
    TableColumn<Teacher, Integer> ageColumn;
    @FXML
    TableColumn<Teacher, String> positionColumn;
    @FXML
    TableColumn<Teacher, String> departmentColumn;
    @FXML
    TableColumn<Teacher, String> passwordColumn;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField ageField;
    @FXML
    public TextField departmentField;
    @FXML
    public TextField passwordField;
    @FXML
    public ComboBox<String> genderComboBox;
    @FXML
    public ComboBox<String> positionComboBox;
    @FXML
    public Label errorMessageLbl;

    private ObservableList<Teacher> teacherList = FXCollections.observableArrayList();
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

        teacherTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double columnWidth = tableWidth / 7;

            usernameColumn.setPrefWidth(columnWidth);
            nameColumn.setPrefWidth(columnWidth);
            genderColumn.setPrefWidth(columnWidth);
            ageColumn.setPrefWidth(columnWidth);
            positionColumn.setPrefWidth(columnWidth);
            departmentColumn.setPrefWidth(columnWidth);
            passwordColumn.setPrefWidth(columnWidth);
        });

        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        positionComboBox.setItems(FXCollections.observableArrayList("Junior", "Senior", "Parttime"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        teacherTable.setItems(teacherList);
        loadTeachersFromDatabase();
    }
    /**
     * Resets the filter fields and reloads the teachers from the database.
     */
    @FXML
    public void resetFilter() {
        usernameFilter.clear();
        nameFilter.clear();
        departmentFilter.clear();
        loadTeachersFromDatabase();
    }
    /**
     * Filters the teachers based on the filter fields.
     */
    @FXML
    public void filterTeachers() {
        String username = usernameFilter.getText();
        String name = nameFilter.getText();
        String department = departmentFilter.getText();

        StringBuilder sql = new StringBuilder("SELECT username, name, gender, age, position, department, password FROM teacher WHERE 1=1");
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
            teacherTable.getItems().clear();

            while (rs.next()) {
                String resultUsername = rs.getString("username");
                String resultName = rs.getString("name");
                String resultGender = rs.getString("gender");
                int resultAge = rs.getInt("age");
                String resultPosition = rs.getString("position");
                String resultDepartment = rs.getString("department");
                String resultPassword = rs.getString("password");

                teacherTable.getItems().add(new Teacher(resultUsername, resultName, resultGender, resultAge, resultPosition, resultDepartment, resultPassword));
            }
            return true;
        });
    }
    /**
     * Deletes the selected teacher from the database.
     */
    @FXML
    public void deleteTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            setMessage(false, "No teacher selected.");
            return;
        }

        String sql = "DELETE FROM teacher WHERE username = ?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, selectedTeacher.getUsername());
            pstmt.executeUpdate();
            setMessage(true, "Delete Successful!");

            teacherTable.getItems().remove(selectedTeacher);
            teacherTable.refresh();
            return true;
        });
    }
    /**
     * Adds a new teacher to the database.
     */
    @FXML
    public void addTeacher() {
        String username = usernameField.getText();
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String position = positionComboBox.getValue();
        String department = departmentField.getText();
        String password = passwordField.getText();

        try {
            if (username.isEmpty() || name.isEmpty() || gender == null || age.isEmpty() || Integer.parseInt(age) < 0 || position == null || department.isEmpty() || password.isEmpty()) {
                setMessage(false, "Error: Please check your inputs.");
                return;
            }
        } catch (NumberFormatException exception) {
            setMessage(false, "Error: Please check your inputs.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM teacher WHERE username = ?";
        boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.getInt(1) > 0) {
                setMessage(false, "Error: Username already exists.");
                return true;
            }
            return false;
        });
        if (isDuplicate) {
            return;
        }

        String sql = "INSERT INTO teacher (username, name, gender, age, position, department, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, position);
            pstmt.setString(6, department);
            pstmt.setString(7, password);

            pstmt.executeUpdate();
            teacherTable.getItems().add(new Teacher(username, name, gender, Integer.parseInt(age), position, department, password));
            setMessage(true, "Add Successful!");

            return true;
        });
    }
    /**
     * Updates the selected teacher in the database.
     */
    @FXML
    public void updateTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            setMessage(false, "No teacher selected.");
            return;
        }

        String oldUsername = selectedTeacher.getUsername();
        String username = usernameField.getText();
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String position = positionComboBox.getValue();
        String department = departmentField.getText();
        String password = passwordField.getText();
        boolean isAnyFieldUpdated = false;

        if (!username.isEmpty()) {
            if (!username.equals(selectedTeacher.getUsername())) {
                String checkSql = "SELECT COUNT(*) FROM teacher WHERE username = ?";
                boolean isDuplicate = executePreparedStatement(checkSql, checkStmt -> {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.getInt(1) > 0) {
                        setMessage(false, "Error: Duplicate username with other teacher.");
                        return true;
                    }
                    return false;
                });
                if (isDuplicate) {
                    return;
                }
            }
            selectedTeacher.setUsername(username);
            isAnyFieldUpdated = true;
        }

        if (!name.isEmpty()) {
            selectedTeacher.setName(name);
            isAnyFieldUpdated = true;
        }

        if (!age.isEmpty()) {
            if (Integer.parseInt(age) < 0) {
                setMessage(false, "Error: Please check your inputs.");
                return;
            }
            selectedTeacher.setAge(Integer.parseInt(age));
            isAnyFieldUpdated = true;
        }

        if (gender != null) {
            selectedTeacher.setGender(gender);
            isAnyFieldUpdated = true;
        }

        if (position != null) {
            selectedTeacher.setPosition(position);
            isAnyFieldUpdated = true;
        }

        if (!department.isEmpty()) {
            selectedTeacher.setDepartment(department);
            isAnyFieldUpdated = true;
        }

        if (!password.isEmpty()) {
            selectedTeacher.setPassword(password);
            isAnyFieldUpdated = true;
        }

        if (!isAnyFieldUpdated) {
            setMessage(false, "Error: No fields to update.");
            return;
        }

        String sql = "UPDATE teacher SET username=?, name=?, gender=?, age=?, position=?, department=?, password=? WHERE username=?";
        executePreparedStatement(sql, pstmt -> {
            pstmt.setString(1, selectedTeacher.getUsername());
            pstmt.setString(2, selectedTeacher.getName());
            pstmt.setString(3, selectedTeacher.getGender());
            pstmt.setInt(4, selectedTeacher.getAge());
            pstmt.setString(5, selectedTeacher.getPosition());
            pstmt.setString(6, selectedTeacher.getDepartment());
            pstmt.setString(7, selectedTeacher.getPassword());
            pstmt.setString(8, oldUsername);

            pstmt.executeUpdate();
            setMessage(true, "Update Successful!");
            teacherTable.refresh();
            return true;
        });
    }
    /**
     * Loads the teachers from the database and populates the table.
     */
    void loadTeachersFromDatabase() {
        String sql = "SELECT username, name, gender, age, position, department, password FROM teacher";
        executePreparedStatement(sql, pstmt -> {
            ResultSet rs = pstmt.executeQuery();
            teacherTable.getItems().clear();
            while (rs.next()) {
                String username = rs.getString("username");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                int age = rs.getInt("age");
                String position = rs.getString("position");
                String department = rs.getString("department");
                String password = rs.getString("password");

                teacherTable.getItems().add(new Teacher(username, name, gender, age, position, department, password));
            }
            return true;
        });
        teacherTable.refresh();
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
    boolean executePreparedStatement(String sql, PreparedStatementOperation operation) {
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
    public interface DatabaseOperation {
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
    public interface PreparedStatementOperation {
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