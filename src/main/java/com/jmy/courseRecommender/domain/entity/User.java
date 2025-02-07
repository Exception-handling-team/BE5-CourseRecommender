package com.jmy.courseRecommender.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보를 나타내는 엔티티")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "사용자 번호", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "사용자 이름", example = "user1")
    private String username;

    @Column(nullable = false)
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @Schema(description = "학년", example = "3")
    private int grade;

    @Schema(description = "목표 학점", example = "9")
    private int credit;

    @Schema(description = "이수한 과목 (쉼표로 구분)", example = "선형대수,확률과통계")
    private String preCourses;
}
