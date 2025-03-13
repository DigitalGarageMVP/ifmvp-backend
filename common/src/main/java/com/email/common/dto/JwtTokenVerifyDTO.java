package com.email.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * JWT 토큰 검증 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenVerifyDTO {
    
    private boolean valid;
    private String username;
    private List<String> roles;
}
