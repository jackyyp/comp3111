package comp3111.examsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentExamPageController {
    @FXML
    private Label examNameLabel;

    public void setExamName(String examName) {
        examNameLabel.setText(examName);
    }
}
