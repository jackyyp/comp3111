package comp3111.examsystem.model;

/**
 * Represents an exam in the exam system.
 * This class contains details about the exam such as its ID, name, publication status, time limit, and associated course.
 * Provides methods to get and set these properties.
 *author:Wong Cheuk Yuen
 * @version 1.0
 */
public class Exam {
    private int id;
    private String name;
    private boolean isPublished;
    private int timeLimit;
    private String course;

    /**
     * Constructs a new Exam with the specified details.
     *
     * @param id the unique identifier of the exam
     * @param name the name of the exam
     * @param isPublished whether the exam is published
     * @param timeLimit the time limit for the exam in minutes
     * @param course the course associated with the exam
     */
    public Exam(int id, String name, boolean isPublished, int timeLimit, String course) {
        this.id = id;
        this.name = name;
        this.isPublished = isPublished;
        this.timeLimit = timeLimit;
        this.course = course;
    }

    /**
     * Gets the unique identifier of the exam.
     *
     * @return the unique identifier of the exam
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the exam.
     *
     * @param id the unique identifier of the exam
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the exam.
     *
     * @return the name of the exam
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the exam.
     *
     * @param name the name of the exam
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the publication status of the exam.
     *
     * @return true if the exam is published, false otherwise
     */
    public boolean getIsPublished() {
        return isPublished;
    }

    /**
     * Sets the publication status of the exam.
     *
     * @param isPublished true if the exam is published, false otherwise
     */
    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    /**
     * Gets the time limit for the exam in minutes.
     *
     * @return the time limit for the exam in minutes
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the time limit for the exam in minutes.
     *
     * @param timeLimit the time limit for the exam in minutes
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Gets the course associated with the exam.
     *
     * @return the course associated with the exam
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the course associated with the exam.
     *
     * @param course the course associated with the exam
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Checks if the exam is published.
     *
     * @return true if the exam is published, false otherwise
     */
    public boolean isPublished() {
        return isPublished;
    }
}