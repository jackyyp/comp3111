package comp3111.examsystem.controller;

import comp3111.examsystem.database.DatabaseConnection;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class StudentManagementController {

    @Data
    @AllArgsConstructor
    public static class Student {
        private  String username;
        private  String name ;
        private  int  age;
        private  String gender ;
        private  String department ;
        private  String password ;
    }

    @FXML
    private TextField usernameFilter;
    @FXML
    private TextField nameFilter;
    @FXML
    private TextField departmentFilter;

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> usernameColumn;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, Integer> ageColumn;
    @FXML
    private TableColumn<Student, String> genderColumn;
    @FXML
    private TableColumn<Student, String> departmentColumn;
    @FXML
    private TableColumn<Student, String> passwordColumn;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorMessageLbl;


    @FXML
    public void initialize() {
        // Add a TextFormatter to restrict input to numbers only
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

    @FXML
    private void resetFilter() {
        usernameFilter.clear();
        nameFilter.clear();
        departmentFilter.clear();
        loadStudentsFromDatabase();
    }

    @FXML
    private void filterStudents() {
        String username = usernameFilter.getText();
        String name = nameFilter.getText();
        String department = departmentFilter.getText();

        StringBuilder sql = new StringBuilder("SELECT username, name, age, gender, department, password FROM student WHERE 1=1");
        if (username != null && !username.isEmpty()) {
            sql.append(" AND username LIKE ?");
        }
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (department != null && !department.isEmpty()) {
            sql.append(" AND department LIKE ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (username != null && !username.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + username + "%");
            }
            if (name != null && !name.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + name + "%");
            }
            if (department != null && !department.isEmpty()) {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        System.out.println(selectedStudent);

        if (selectedStudent != null) {
            String sql = "DELETE FROM student WHERE username = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, selectedStudent.getUsername());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    errorMessageLbl.setText("Delete Successful!");
                    errorMessageLbl.setStyle("-fx-text-fill: green;");
                    errorMessageLbl.setVisible(true);
                    studentTable.getItems().remove(selectedStudent);
                } else {
                    errorMessageLbl.setText("Failed to delete student.");
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
            errorMessageLbl.setText("No student selected.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

    // unneeded, dynamic update on ops
//    @FXML
//    private void refreshStudents() {
//        // Implement refresh logic here
//    }

    @FXML
    private void addStudent() {
        String username = usernameField.getText();
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String department = departmentField.getText();
        String password = passwordField.getText();

        try {
            if (username.isEmpty() || name.isEmpty() || gender == null || age.isEmpty() || Integer.parseInt(age) < 0 || department.isEmpty() || password.isEmpty() ) {
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

        //if exist, we cant add , we should update
        String checkSql = "SELECT COUNT(*) FROM student WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                errorMessageLbl.setText("Error: Username already exists.");
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

        String sql = "INSERT INTO student (username, name, gender, age, department, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, gender);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, department);
            pstmt.setString(6, password);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                errorMessageLbl.setText("Add Successful!");
                errorMessageLbl.setStyle("-fx-text-fill: green;");
                errorMessageLbl.setVisible(true);
                studentTable.getItems().add(new Student(username, name, Integer.parseInt(age), gender, department, password));
            } else {
                errorMessageLbl.setText("Failed to add student.");
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
    private void updateStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            String oldUsername = selectedStudent.getUsername();
            String username = usernameField.getText();
            String name = nameField.getText();
            String age = ageField.getText();
            String gender = genderComboBox.getValue();
            String department = departmentField.getText();
            String password = passwordField.getText();

            boolean isAnyFieldUpdated = false;
            if (username != null && !username.isEmpty()) {
                //if exist more than 1 (including this), then fail for pk property
                if(!username.equals(selectedStudent.getUsername())) {
                    String checkSql = "SELECT COUNT(*) FROM student WHERE username = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                        checkStmt.setString(1, username);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            errorMessageLbl.setText("Error: Duplicate username with other student.");
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
                selectedStudent.setUsername(username);
                isAnyFieldUpdated = true;
            }
            if (name != null && !name.isEmpty()) {
                selectedStudent.setName(name);
                isAnyFieldUpdated = true;
            }
            if (age != null && !age.isEmpty()) {
                if (Integer.parseInt(age) < 0) {
                    errorMessageLbl.setText("Error: Please check your inputs.");
                    errorMessageLbl.setStyle("-fx-text-fill: red;");
                    errorMessageLbl.setVisible(true);
                    return;
                }
                selectedStudent.setAge(Integer.parseInt(age));
                isAnyFieldUpdated = true;
            }
            if (gender != null && !gender.isEmpty()) {
                selectedStudent.setGender(gender);
                isAnyFieldUpdated = true;
            }
            if (department != null && !department.isEmpty()) {
                selectedStudent.setDepartment(department);
                isAnyFieldUpdated = true;
            }
            if (password != null && !password.isEmpty()) {
                selectedStudent.setPassword(password);
                isAnyFieldUpdated = true;
            }
            if (!isAnyFieldUpdated) {
                errorMessageLbl.setText("Error: No fields to update.");
                errorMessageLbl.setStyle("-fx-text-fill: red;");
                errorMessageLbl.setVisible(true);
                return;
            }



            // Update query
            String sql = "UPDATE student SET username=?, name = ?, age = ?, gender = ?, department = ?, password = ? WHERE username = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, selectedStudent.getUsername());
                pstmt.setString(2, selectedStudent.getName());
                pstmt.setInt(3, selectedStudent.getAge());
                pstmt.setString(4, selectedStudent.getGender());
                pstmt.setString(5, selectedStudent.getDepartment());
                pstmt.setString(6, selectedStudent.getPassword());
                pstmt.setString(7, oldUsername);

                System.out.println(pstmt);
                System.out.println(selectedStudent.getUsername());
                System.out.println(oldUsername);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    errorMessageLbl.setText("Update Successful!");
                    errorMessageLbl.setStyle("-fx-text-fill: green;");
                    errorMessageLbl.setVisible(true);
                    studentTable.refresh();
                } else {
                    errorMessageLbl.setText("Failed to update student.");
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
        else{
            errorMessageLbl.setText("No student selected.");
            errorMessageLbl.setStyle("-fx-text-fill: red;");
            errorMessageLbl.setVisible(true);
        }
    }

    private void loadStudentsFromDatabase() {
        String sql = "SELECT username, name, age, gender, department, password FROM student";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}