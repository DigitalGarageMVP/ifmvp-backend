package com.email.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그아웃 응답 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그아웃 응답")
public class LogoutResponse {
    
    @Schema(description = "로그아웃 성공 여부", example = "true")
    private boolean success;
}
