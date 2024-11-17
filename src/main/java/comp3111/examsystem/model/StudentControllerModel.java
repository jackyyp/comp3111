package comp3111.examsystem.model;

import lombok.Data;

@Data
/**
 * The model for the student's information.
 *
 * This class is used to store the student's information between different controllers.
 *
 * @author WANG Shao Fu
 */
public class StudentControllerModel {

    /**
     * The username of the student.
     */
    private String username;

    /**
     * The ID of the exam.
     */
    private Integer examId;
}
