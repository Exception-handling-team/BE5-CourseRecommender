package com.jmy.courseRecommender.domain.controller;


import com.jmy.courseRecommender.domain.entity.Course;
import com.jmy.courseRecommender.domain.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 과목 등록
     */
    @PostMapping
    public ResponseEntity<Course> registerCourse(@RequestBody Course course) {
        Course savedCourse = courseService.registerCourse(course);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    /**
     * 모든 과목 조회 + 페이징 (간단 구현)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<Course> allCourses = courseService.getAllCourses();
        int totalCourses = allCourses.size();
        int totalPages = (int) Math.ceil((double) totalCourses / size);
        int currentPage = Math.max(0, page);
        currentPage = Math.min(currentPage, totalPages - 1);

        // 내림차순 정렬 (id 기준)
        allCourses.sort(Comparator.comparingLong(Course::getId).reversed());

        int startIndex = currentPage * size;
        int endIndex = Math.min(startIndex + size, totalCourses);
        List<Course> pageList = allCourses.subList(startIndex, endIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", currentPage + 1);
        response.put("totalPages", totalPages);
        response.put("pageSize", size);
        response.put("totalCourses", totalCourses);
        response.put("courses", pageList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 과목 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.ok("과목이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 과목이 존재하지 않습니다.");
        }
    }

    /**
     * 과목 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> modifyCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        Optional<Course> modified = courseService.modifyCourse(id, updatedCourse);
        return modified
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 수강신청 추천
     */
    @PostMapping("/recommend")
    public ResponseEntity<?> recommendCourses(
            @RequestParam int currentGrade,
            @RequestParam(required = false) String completedCourses,
            @RequestParam(defaultValue = "9999") int targetCredits) {

        List<String> completedCourseList = new ArrayList<>();
        if (completedCourses != null && !completedCourses.trim().isEmpty()) {
            completedCourseList = Arrays.stream(completedCourses.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        try {
            List<Course> recommendations = courseService.recommendCourses(currentGrade, completedCourseList, targetCredits);
            // 경로 인자를 제거하고, 서비스가 properties 설정한 경로를 사용
            courseService.saveRecommendationsToFile(recommendations);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
