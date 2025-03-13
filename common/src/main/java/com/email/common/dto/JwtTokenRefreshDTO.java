package com.email.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 갱신 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenRefreshDTO {
    
    private String token;
    private String refreshToken;
}
