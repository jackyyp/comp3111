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
        filterStudents();
    }

    @FXML
    private void filterStudents() {
        // Implement filtering logic here
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {

        }
    }

    @FXML
    private void refreshStudents() {
        // Implement refresh logic here
    }

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
                errorMessageLbl.setVisible(true);
                return;
            }
        } catch (NumberFormatException exception) {
            errorMessageLbl.setText("Error: Please check your inputs.");
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
                errorMessageLbl.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorMessageLbl.setText("Error connecting to the database.");
            errorMessageLbl.setVisible(true);
        }
    }

    @FXML
    private void updateStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            selectedStudent.setUsername(usernameField.getText());
            selectedStudent.setName(nameField.getText());
            selectedStudent.setAge(Integer.parseInt(ageField.getText()));
            selectedStudent.setGender(genderComboBox.getValue());
            selectedStudent.setDepartment(departmentField.getText());
            selectedStudent.setPassword(passwordField.getText());
        }
        //update query

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