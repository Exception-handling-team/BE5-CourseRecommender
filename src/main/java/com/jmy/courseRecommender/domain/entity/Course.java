package com.jmy.courseRecommender.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "과목 번호", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "과목명", example = "선형대수")
    private String courseName;

    @Schema(description = "과목코드", example = "T031086")
    private String courseCode;

    @Schema(description = "학점", example = "3")
    private int courseCredit;

    @Schema(description = "개설 학년", example = "1")
    private int courseGrade;

    @Schema(description = "선수 과목명 (없으면 '없음')", example = "없음")
    private String preCourseName;

    @Schema(description = "수업 교시 정보", example = "화 5 6 목 7")
    private String courseTime;
}
