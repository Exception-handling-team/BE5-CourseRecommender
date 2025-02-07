package com.jmy.courseRecommender.domain.controller;

import com.jmy.courseRecommender.domain.entity.Course;
import com.jmy.courseRecommender.domain.entity.User;
import com.jmy.courseRecommender.domain.service.CourseService;
import com.jmy.courseRecommender.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CourseService courseService;
    private final UserService userService;

    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록한다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User registered = userService.registerUser(user);
            return new ResponseEntity<>(registered, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "사용자 로그인", description = "사용자 이름과 비밀번호로 로그인한다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginRequest) {
        try {
            User user = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiResponse(responseCode = "200", description = "추천 과목 목록 반환 성공")
    @PostMapping("/courses/recommend/{username}")
    public ResponseEntity<?> recommendCoursesForUser(@PathVariable String username) {
        try {
            // UserService를 통해 사용자 정보를 조회
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            // 사용자 정보를 기반으로 추천 알고리즘 파라미터 구성
            int currentGrade = user.getGrade();
            int targetCredits = user.getCredit();
            List<String> completedCourseList = new ArrayList<>();
            if (user.getPreCourses() != null && !user.getPreCourses().trim().isEmpty()) {
                completedCourseList = Arrays.stream(user.getPreCourses().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            // 추천 알고리즘 실행
            List<Course> recommendations = courseService.recommendCourses(currentGrade, completedCourseList, targetCredits);

            // 추천 결과를 파일에 저장 (application.properties에 설정한 경로 사용)
            courseService.saveRecommendationsToFile(recommendations);

            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
