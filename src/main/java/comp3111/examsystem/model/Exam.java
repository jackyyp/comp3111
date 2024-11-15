package comp3111.examsystem.model;

public class Exam {
    private int id;
    private String name;
    private boolean isPublished;
    private int timeLimit;
    private String course;

    // Constructor, getters, and setters

    public Exam(int id, String name, boolean isPublished, int timeLimit, String course) {
        this.id = id;
        this.name = name;
        this.isPublished = isPublished;
        this.timeLimit = timeLimit;
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}