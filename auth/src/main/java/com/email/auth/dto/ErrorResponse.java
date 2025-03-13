package com.email.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 에러 응답 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "에러 응답")
public class ErrorResponse {
    
    @Schema(description = "에러 코드", example = "401")
    private int errorCode;
    
    @Schema(description = "에러 메시지", example = "사용자 ID 또는 비밀번호가 잘못되었습니다.")
    private String errorMessage;
    
    @Schema(description = "해결 방안", example = "사용자 ID와 비밀번호를 확인하세요.")
    private String solution;
}
