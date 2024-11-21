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

    @FXML
    public void resetFilter() {
        usernameFilter.clear();
        nameFilter.clear();
        departmentFilter.clear();
        loadTeachersFromDatabase();
    }

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
            return null;
        });
    }

    @FXML
    public void deleteTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher != null) {
            String sql = "DELETE FROM teacher WHERE username = ?";

            executePreparedStatement(sql, pstmt -> {
                pstmt.setString(1, selectedTeacher.getUsername());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    teacherTable.getItems().remove(selectedTeacher);
                } else {
                    errorMessageLbl.setText("Error: Could not delete teacher.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
                }
                return null;
            });
        } else {
            errorMessageLbl.setText("No teacher selected.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

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
                errorMessageLbl.setText("Error: Please check your inputs.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }
        } catch (NumberFormatException exception) {
            errorMessageLbl.setText("Error: Please check your inputs.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM teacher WHERE username = ?";
        executePreparedStatement(checkSql, checkStmt -> {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                errorMessageLbl.setText("Error: Username already exists.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return null;
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
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    teacherTable.getItems().add(new Teacher(username, name, gender, Integer.parseInt(age), position, department, password));
                } else {
                    errorMessageLbl.setText("Error: Could not add teacher.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
                }
                return null;
            });
            return null;
        });
    }

    @FXML
    public void updateTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher != null) {
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
                    executePreparedStatement(checkSql, checkStmt -> {
                        checkStmt.setString(1, username);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            errorMessageLbl.setText("Error: Username already exists.");
                            errorMessageLbl.setStyle("-fx-text-fill: red;");
                            errorMessageLbl.setVisible(true);
                            return null;
                        }
                        return null;
                    });
                    selectedTeacher.setUsername(username);
                    isAnyFieldUpdated = true;
                }
            }
            if (!name.isEmpty()) {
                selectedTeacher.setName(name);
                isAnyFieldUpdated = true;
            }
            if (!age.isEmpty()) {
                if (Integer.parseInt(age) < 0) {
                    errorMessageLbl.setText("Error: Age cannot be negative.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
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
                errorMessageLbl.setText("Error: No fields to update.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }

            String sql = "UPDATE teacher SET username=?, name = ?, gender = ?, age = ?, position = ?, department = ?, password = ? WHERE username = ?";
            executePreparedStatement(sql, pstmt -> {
                pstmt.setString(1, selectedTeacher.getUsername());
                pstmt.setString(2, selectedTeacher.getName());
                pstmt.setString(3, selectedTeacher.getGender());
                pstmt.setInt(4, selectedTeacher.getAge());
                pstmt.setString(5, selectedTeacher.getPosition());
                pstmt.setString(6, selectedTeacher.getDepartment());
                pstmt.setString(7, selectedTeacher.getPassword());
                pstmt.setString(8, oldUsername);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    teacherTable.refresh();
                } else {
                    errorMessageLbl.setText("Error: Could not update teacher.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
                }
                return null;
            });
        } else {
            errorMessageLbl.setText("No teacher selected.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

    private void loadTeachersFromDatabase() {
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
            return null;
        });
    }

    private <T> T executeDatabaseOperation(DatabaseOperation<T> operation) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return operation.execute(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
            return null;
        }
    }

    private <T> T executePreparedStatement(String sql, PreparedStatementOperation<T> operation) {
        return executeDatabaseOperation(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                return operation.execute(pstmt);
            }
        });
    }

    @FunctionalInterface
    private interface DatabaseOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    private interface PreparedStatementOperation<T> {
        T execute(PreparedStatement pstmt) throws SQLException;
    }
}