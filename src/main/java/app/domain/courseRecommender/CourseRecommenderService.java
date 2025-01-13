package app.domain.courseRecommender;

import java.util.List;
import java.util.Optional;

public class CourseRecommenderService {

    private final CourseRecommenderRepository courseRecommenderRepository;

    public CourseRecommenderService() {

        courseRecommenderRepository = new CourseRecommenderRepository();

    }

    public CourseRecommender write(String courseName, String courseCode, int courseCredit, int courseGrade, String preCourseName, String courseTime) {
        CourseRecommender courseRecommender = new CourseRecommender(courseName, courseCode, courseCredit, courseGrade, preCourseName, courseTime);
        return courseRecommenderRepository.save(courseRecommender);
    }

    public List<CourseRecommender> getAllItems() {
        return courseRecommenderRepository.findAll();
    }

    public boolean delete(int id) {
        return courseRecommenderRepository.deleteById(id);
    }

    public Optional<CourseRecommender> getItems(int id) {
        return courseRecommenderRepository.findById(id);
    }

    public void modify(CourseRecommender c, String newCourseName, String newCourseCode, int newCourseCredit, int newCourseGrade, String newPreCourseName, String newCourseTime) {
        c.setCourseName(newCourseName);
        c.setCourseCode(newCourseCode);
        c.setCourseCredit(newCourseCredit);
        c.setCourseGrade(newCourseGrade);
        c.setPreCourseName(newPreCourseName);
        c.setCourseTime(newCourseTime);
        courseRecommenderRepository.save(c);
    }
}
