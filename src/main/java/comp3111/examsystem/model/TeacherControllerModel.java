package comp3111.examsystem.model;

import lombok.Data;

/**
 * Represents the model for the teacher controller.
 * This model holds the data related to the teacher's username and name.
 *author:Wong Cheuk Yuen
 * @version 1.0
 */
@Data
public class TeacherControllerModel {
    /**
     * The username of the teacher.
     */
    public String username;

    /**
     * The name of the teacher.
     */
    public String name;
}