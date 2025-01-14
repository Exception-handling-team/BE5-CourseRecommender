package app.domain.courseRecommender;


import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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

    public boolean isNew() {
        return this.id == 0;
    }

    public boolean checkPrerequisite(List<String> completedCourses) {
        if (preCourseName.equalsIgnoreCase("없음")) {
            return true;
        }

        String[] prerequisites = preCourseName.split(",");
        for (String pre : prerequisites) {
            if (!completedCourses.contains(pre.trim())) {
                return false;
            }
        }
        return true;
    }


    public static boolean timeConflict(CourseRecommender c1, CourseRecommender c2) {
        Set<String> times1 = parseCourseTime(c1.getCourseTime());
        Set<String> times2 = parseCourseTime(c2.getCourseTime());

        for (String time : times1) {
            if (times2.contains(time)) {
                return true;
            }
        }
        return false;
    }


    public static Set<String> parseCourseTime(String courseTime) {
        Set<String> parsedTimes = new HashSet<>();
        String[] tokens = courseTime.split(" ");

        String currentDay = "";
        for (String token : tokens) {
            if (isDay(token)) {
                currentDay = token;
            } else {
                parsedTimes.add(currentDay + token);
            }
        }

        return parsedTimes;
    }


    private static boolean isDay(String token) {
        return token.equals("월") || token.equals("화") || token.equals("수") ||
                token.equals("목") || token.equals("금") || token.equals("토") ||
                token.equals("일");
    }
}
