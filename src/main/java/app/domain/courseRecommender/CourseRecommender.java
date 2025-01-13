package app.domain.courseRecommender;

public class CourseRecommender {
    private int id;
    private String courseName;
    private String courseCode;
    private int courseCredit;
    private int courseGrade;
    private String preCourseName;
    private String courseTime;

    public CourseRecommender(String courseName, String courseCode, int courseCredit, int courseGrade, String preCourseName, String courseTime) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseCredit = courseCredit;
        this.courseGrade = courseGrade;
        this.preCourseName = preCourseName;
        this.courseTime = courseTime;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(int courseCredit) {
        this.courseCredit = courseCredit;
    }

    public int getCourseGrade() {
        return courseGrade;
    }

    public void setCourseGrade(int courseGrade) {
        this.courseGrade = courseGrade;
    }

    public String getPreCourseName() {
        return preCourseName;
    }

    public void setPreCourseName(String preCourseName) {
        this.preCourseName = preCourseName;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNew() {
        return this.id == 0;
    }
}
