package comp3111.examsystem.model;

import lombok.Data;

@Data
public class StudentControllerModel {   // This class is used to store the student's information between different controllers
    private String username;
    private Integer examId;
}
