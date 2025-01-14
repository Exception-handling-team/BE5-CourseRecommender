package app.domain.courseRecommender;

import app.domain.courseRecommender.repository.FileCourseRecommenderRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CourseRecommenderService {

    private final CourseRecommenderRepository courseRecommenderRepository;
    private static final Logger logger = Logger.getLogger(CourseRecommenderService.class.getName());

    // 기본 생성자: 파일 경로를 "data/courses.txt"로 설정하여 FileCourseRecommenderRepository 사용
    public CourseRecommenderService() {
        this.courseRecommenderRepository =  new FileCourseRecommenderRepository("data/courses.txt");
    }

    // 의존성 주입을 위한 생성자
    public CourseRecommenderService(CourseRecommenderRepository repository) {
        this.courseRecommenderRepository = repository;
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


    public List<CourseRecommender> recommendCourses(int currentGrade, List<String> completedCourses, int targetCredits) {
        List<CourseRecommender> allCourses = courseRecommenderRepository.findAll();

        // 1. 이수한 과목 제외 및 학년 필터링
        List<CourseRecommender> sameYear = new ArrayList<>();
        List<CourseRecommender> diffYear = new ArrayList<>();

        for (CourseRecommender course : allCourses) {
            if (completedCourses.contains(course.getCourseName())) continue; // 이수한 과목 제외
            if (course.getCourseGrade() > currentGrade) continue; // 높은 학년 과목 제외

            if (course.getCourseGrade() == currentGrade) {
                sameYear.add(course);
            } else {
                diffYear.add(course);
            }
        }

        // 2. 선수 과목 충족 여부에 따른 분류
        List<CourseRecommender> primary = new ArrayList<>();
        List<CourseRecommender> secondary = new ArrayList<>();

        for (CourseRecommender course : sameYear) {
            if (course.checkPrerequisite(completedCourses)) {
                primary.add(course);
            } else {
                secondary.add(course);
            }
        }

        List<CourseRecommender> otherPrimary = new ArrayList<>();
        List<CourseRecommender> otherSecondary = new ArrayList<>();

        for (CourseRecommender course : diffYear) {
            if (course.checkPrerequisite(completedCourses)) {
                otherPrimary.add(course);
            } else {
                otherSecondary.add(course);
            }
        }

        // 3. 추천 후보군 구성 (Primary, Other Primary, Secondary, Other Secondary 순으로 우선순위 설정)
        List<CourseRecommender> candidates = new ArrayList<>();
        candidates.addAll(primary);
        candidates.addAll(otherPrimary);
        candidates.addAll(secondary);
        candidates.addAll(otherSecondary);

        // 4. 시간 충돌 및 학점 제한을 고려하여 추천 과목 선택
        List<CourseRecommender> result = new ArrayList<>();
        int accumulatedCredits = 0;
        Set<String> scheduledTimes = new HashSet<>();

        for (CourseRecommender course : candidates) {
            if (accumulatedCredits + course.getCourseCredit() > targetCredits) {
                continue; // 학점 초과 시 건너뜀
            }

            boolean conflict = false;
            for (CourseRecommender chosen : result) {
                if (CourseRecommender.timeConflict(chosen, course)) {
                    conflict = true;
                    break;
                }
            }

            if (!conflict) {
                result.add(course);
                accumulatedCredits += course.getCourseCredit();
                // 시간 슬롯을 추가하여 추후 충돌 검사에 활용
                scheduledTimes.addAll(CourseRecommender.parseCourseTime(course.getCourseTime()));
            }
        }

        // 5. 추천 가능한 과목이 없을 경우 예외 발생
        if (result.isEmpty()) {
            throw new RuntimeException("추천 가능한 과목이 없습니다!");
        }

        return result;
    }


    public List<CourseRecommender> recommendCourses(int currentGrade, List<String> completedCourses) {
        return recommendCourses(currentGrade, completedCourses, Integer.MAX_VALUE);
    }

    public void saveRecommendations(List<CourseRecommender> recommendations, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("추천 과목 목록:\n");
            for (CourseRecommender course : recommendations) {
                writer.write(String.format("%d / %s / %s / %d / %d / %s / %s\n",
                        course.getId(),
                        course.getCourseName(),
                        course.getCourseCode(),
                        course.getCourseCredit(),
                        course.getCourseGrade(),
                        course.getPreCourseName(),
                        course.getCourseTime()));
            }
            logger.info("추천 과목 목록이 " + filePath + "에 성공적으로 저장되었습니다.");
        } catch (IOException e) {
            logger.severe("추천 결과를 파일에 저장하는 중 오류가 발생했습니다: " + e.getMessage());
            throw new RuntimeException("추천 결과를 파일에 저장하는 중 오류가 발생했습니다.", e);
        }
    }
}
