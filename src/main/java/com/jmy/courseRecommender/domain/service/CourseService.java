package com.jmy.courseRecommender.domain.service;

import com.jmy.courseRecommender.domain.entity.Course;
import com.jmy.courseRecommender.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    // application.properties에서 주입: 예: recommendations.file.path=./data/recommended.txt
    @Value("${recommendations.file.path}")
    private String recommendedFilePath;

    // 과목 등록
    public Course registerCourse(Course course) {
        return courseRepository.save(course);
    }

    // 페이징을 위한 모든 과목 조회
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    // 전체 과목 조회 (비페이징)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // 과목 삭제
    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 과목 수정
    public Optional<Course> modifyCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id).map(course -> {
            course.setCourseName(updatedCourse.getCourseName());
            course.setCourseCode(updatedCourse.getCourseCode());
            course.setCourseCredit(updatedCourse.getCourseCredit());
            course.setCourseGrade(updatedCourse.getCourseGrade());
            course.setPreCourseName(updatedCourse.getPreCourseName());
            course.setCourseTime(updatedCourse.getCourseTime());
            return courseRepository.save(course);
        });
    }

    /**
     * 수강신청 추천 알고리즘
     * - currentGrade: 현재 학년
     * - completedCourses: 이수한 과목명 리스트
     * - targetCredits: 목표 학점
     * @return 추천 과목 목록
     */
    public List<Course> recommendCourses(int currentGrade, List<String> completedCourses, int targetCredits) {
        List<Course> allCourses = courseRepository.findAll();

        // 1. 이수한 과목과 현재 학년보다 높은 과목 제외
        List<Course> sameYear = new ArrayList<>();
        List<Course> diffYear = new ArrayList<>();
        for (Course course : allCourses) {
            if (completedCourses.contains(course.getCourseName())) continue;
            if (course.getCourseGrade() > currentGrade) continue;
            if (course.getCourseGrade() == currentGrade) {
                sameYear.add(course);
            } else {
                diffYear.add(course);
            }
        }

        // 2. 선수 과목 충족 여부에 따른 분류
        List<Course> primary = sameYear.stream()
                .filter(course -> checkPrerequisite(course, completedCourses))
                .collect(Collectors.toList());
        List<Course> secondary = sameYear.stream()
                .filter(course -> !checkPrerequisite(course, completedCourses))
                .collect(Collectors.toList());

        List<Course> otherPrimary = diffYear.stream()
                .filter(course -> checkPrerequisite(course, completedCourses))
                .collect(Collectors.toList());
        List<Course> otherSecondary = diffYear.stream()
                .filter(course -> !checkPrerequisite(course, completedCourses))
                .collect(Collectors.toList());

        // 3. 후보군 합치기 (우선순위: primary, otherPrimary, secondary, otherSecondary)
        List<Course> candidates = new ArrayList<>();
        candidates.addAll(primary);
        candidates.addAll(otherPrimary);
        candidates.addAll(secondary);
        candidates.addAll(otherSecondary);

        // 4. 시간 충돌 및 학점 제한 고려하여 추천 과목 선택
        List<Course> result = new ArrayList<>();
        int accumulatedCredits = 0;
        Set<String> scheduledTimes = new HashSet<>();

        for (Course course : candidates) {
            if (accumulatedCredits + course.getCourseCredit() > targetCredits) {
                log.info("학점 초과로 제외된 과목: {}", course.getCourseName());
                continue;
            }
            boolean conflict = false;
            for (Course chosen : result) {
                if (timeConflict(chosen, course)) {
                    conflict = true;
                    break;
                }
            }
            if (!conflict) {
                result.add(course);
                accumulatedCredits += course.getCourseCredit();
                scheduledTimes.addAll(parseCourseTime(course.getCourseTime()));
                log.info("추천 과목 추가: {}", course.getCourseName());
            } else {
                log.info("시간 충돌로 제외된 과목: {}", course.getCourseName());
            }
        }
        if (result.isEmpty()) {
            throw new RuntimeException("추천 가능한 과목이 없습니다!");
        }
        return result;
    }

    // 추천 결과를 파일에 저장 (application.properties에 설정된 경로 사용)
    public void saveRecommendationsToFile(List<Course> recommendations) {
        saveRecommendationsToFile(recommendations, recommendedFilePath);
    }

    private void saveRecommendationsToFile(List<Course> recommendations, String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("추천 과목 목록:\n");
            for (Course course : recommendations) {
                writer.write(String.format("%d / %s / %s / %d / %d / %s / %s\n",
                        course.getId(),
                        course.getCourseName(),
                        course.getCourseCode(),
                        course.getCourseCredit(),
                        course.getCourseGrade(),
                        course.getPreCourseName(),
                        course.getCourseTime()));
            }
            log.info("추천 과목 목록이 {}에 성공적으로 저장되었습니다.", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("추천 결과를 파일에 저장하는 중 오류가 발생했습니다: {}", e.getMessage());
            throw new RuntimeException("추천 결과를 파일에 저장하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 선수 과목 충족 여부 확인
    private boolean checkPrerequisite(Course course, List<String> completedCourses) {
        if ("없음".equalsIgnoreCase(course.getPreCourseName())) {
            return true;
        }
        String[] prerequisites = course.getPreCourseName().split(",");
        for (String pre : prerequisites) {
            if (!completedCourses.contains(pre.trim())) {
                return false;
            }
        }
        return true;
    }

    // 두 과목의 시간 충돌 확인
    private boolean timeConflict(Course c1, Course c2) {
        Set<String> times1 = parseCourseTime(c1.getCourseTime());
        Set<String> times2 = parseCourseTime(c2.getCourseTime());
        for (String t : times1) {
            if (times2.contains(t)) {
                return true;
            }
        }
        return false;
    }

    // 시간 문자열 파싱 (예: "화 5 6 목 7" -> ["화5", "화6", "목7"])
    private Set<String> parseCourseTime(String courseTime) {
        Set<String> parsed = new HashSet<>();
        String[] tokens = courseTime.split(" ");
        String currentDay = "";
        for (String token : tokens) {
            if (isDay(token)) {
                currentDay = token;
            } else {
                parsed.add(currentDay + token);
            }
        }
        return parsed;
    }

    private boolean isDay(String token) {
        return token.equals("월") || token.equals("화") || token.equals("수") ||
                token.equals("목") || token.equals("금") || token.equals("토") ||
                token.equals("일");
    }
}
