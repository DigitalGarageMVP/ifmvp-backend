// com.email.common.dto.ApiResponse
package com.email.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * API 응답 공통 DTO 클래스입니다.
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "API 응답")
public class ApiResponse<T> {

    @Schema(description = "상태 코드", example = "200")
    private int status;

    @Schema(description = "메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    /**
     * 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param <T> 응답 데이터 타입
     * @return API 응답 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "요청이 성공적으로 처리되었습니다.", data);
    }

    /**
     * 오류 응답을 생성합니다.
     *
     * @param status 상태 코드
     * @param message 오류 메시지
     * @param <T> 응답 데이터 타입
     * @return API 응답 객체
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }

    /**
     * 응답 데이터를 안전하게 반환합니다.
     * data가 null인 경우 null을 반환합니다.
     *
     * @return 응답 데이터
     */
    public T getData() {
        return data;
    }
}