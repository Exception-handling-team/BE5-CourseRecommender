package com.jmy.courseRecommender.domain.controller;

import com.jmy.courseRecommender.domain.entity.Course;
import com.jmy.courseRecommender.domain.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 과목 등록
    @Operation(summary = "과목 등록", description = "새로운 과목을 등록한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "과목 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/course")
    public ResponseEntity<Course> registerCourse(@RequestBody Course course) {
        Course savedCourse = courseService.registerCourse(course);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    // 다중 등록
    @Operation(summary = "다중 과목 등록", description = "여러 개의 과목을 한 번에 등록한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "과목 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/courses")
    public ResponseEntity<List<Course>> registerCourses(@RequestBody List<Course> courseList) {
        List<Course> savedCourses = new ArrayList<>();
        for (Course c : courseList) {
            Course saved = courseService.registerCourse(c);
            savedCourses.add(saved);
        }
        return new ResponseEntity<>(savedCourses, HttpStatus.CREATED);
    }

    // 모든 과목 조회(페이징)
    @Operation(summary = "과목 목록 조회", description = "등록된 과목 목록을 페이징하여 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과목 목록 조회 성공")
    })
    @GetMapping("/search")
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

    // 과목 삭제
    @Operation(summary = "과목 삭제", description = "특정 ID의 과목을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과목 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 과목을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.ok("과목이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 과목이 존재하지 않습니다.");
        }
    }

    // 과목 수정
    @Operation(summary = "과목 수정", description = "특정 ID의 과목 정보를 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과목 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 과목을 찾을 수 없음")
    })
    @PutMapping("/modify/{id}")
    public ResponseEntity<Course> modifyCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        Optional<Course> modified = courseService.modifyCourse(id, updatedCourse);
        return modified
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 수강신청 추천
    @Operation(summary = "수강신청 추천", description = "현재 학년, 이수한 과목, 목표 학점을 기반으로 추천 과목을 반환한다. 추천 결과는 파일에도 저장된다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 과목 목록 반환 성공"),
            @ApiResponse(responseCode = "400", description = "추천 가능한 과목이 없습니다.")
    })
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
            courseService.saveRecommendationsToFile(recommendations);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
