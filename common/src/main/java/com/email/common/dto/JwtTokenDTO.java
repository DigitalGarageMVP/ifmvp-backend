package com.email.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JWT 토큰 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenDTO {
    
    private String token;
    private String refreshToken;
    private LocalDateTime expiryDate;
}
