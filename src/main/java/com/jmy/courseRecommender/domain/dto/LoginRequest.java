package com.jmy.courseRecommender.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {
    @Schema(description = "사용자 이름", example = "user1")
    private String username;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
