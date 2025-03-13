package com.email.auth.controller;

import com.email.auth.dto.LoginRequest;
import com.email.auth.dto.LoginResponse;
import com.email.auth.dto.LogoutRequest;
import com.email.auth.dto.LogoutResponse;
import com.email.auth.service.AuthService;
import com.email.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인 및 로그아웃 API")
public class AuthController {

    private final AuthService authService;
    
    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인 요청 정보
     * @return 로그인 응답 정보 (토큰 포함)
     */
    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "사용자 ID와 비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 사용자 로그아웃을 처리합니다.
     *
     * @param request 로그아웃 요청 정보
     * @return 로그아웃 응답 정보
     */
    @PostMapping("/logout")
    @Operation(summary = "사용자 로그아웃", description = "사용자 토큰을 무효화하여 로그아웃합니다.")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(@Valid @RequestBody LogoutRequest request) {
        LogoutResponse response = authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
