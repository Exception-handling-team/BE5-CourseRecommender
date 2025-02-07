package com.jmy.courseRecommender.domain.service;

import com.jmy.courseRecommender.domain.entity.Course;
import com.jmy.courseRecommender.global.exception.ServiceException;
import com.jmy.courseRecommender.domain.repository.CourseRepository;
import com.jmy.courseRecommender.domain.recommendation.CourseRecommendationEngine;
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

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRecommendationEngine recommendationEngine = new CourseRecommendationEngine();

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
        throw new ServiceException("404-2", "해당 과목이 존재하지 않습니다!", 404);
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
     * 수강신청 추천 알고리즘을 실행하는 메서드
     * @param currentGrade     현재 학년
     * @param completedCourses 이수한 과목명 리스트
     * @param targetCredits    목표 학점
     * @return 추천 과목 목록
     */
    public List<Course> recommendCourses(int currentGrade, List<String> completedCourses, int targetCredits) {
        List<Course> recommendations = recommendationEngine.recommend(
                courseRepository.findAll(), currentGrade, completedCourses, targetCredits);
        if (recommendations.isEmpty()) {
            throw new ServiceException("400-1", "추천 가능한 과목이 없습니다!", 400);
        }
        return recommendations;
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
            throw new ServiceException("500-1", "추천 결과를 파일에 저장하는 중 오류가 발생했습니다: " + e.getMessage(), 500);
        }
    }
}
