package comp3111.examsystem.model;

import lombok.Data;

/**
 * The model for the student's information.
 *
 * This class is used to store the student's information between different controllers.
 *
 * @author WANG Shao Fu
 */
@Data
public class StudentControllerModel {

    /**
     * The username of the student.
     */
    private String username;

    /**
     * The ID of the exam.
     */
    private Integer examId;

    /**
     * The ID of the question.
     *
     * @return the ID of the exam
     */
    public int getId(){
        if (examId==null){
            return 0;
        }
        return examId;
    }
}

