package com.email.auth.service;

import com.email.auth.domain.User;
import com.email.auth.dto.LoginRequest;
import com.email.auth.dto.LoginResponse;
import com.email.auth.dto.LogoutRequest;
import com.email.auth.dto.LogoutResponse;
import com.email.auth.exception.InvalidCredentialsException;
import com.email.auth.repository.UserRepository;
import com.email.common.dto.JwtTokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 인증 관련 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    
    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인 요청 정보
     * @return 로그인 응답 정보 (토큰 포함)
     * @throws InvalidCredentialsException 사용자 인증 실패 시
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 요청: {}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("사용자 ID 또는 비밀번호가 잘못되었습니다."));
        
        validateCredentials(user, request.getPassword());
        
        JwtTokenDTO tokenDto = tokenProvider.createToken(user);
        
        String userRole = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
        
        log.info("로그인 성공: {}", user.getUsername());
        
        return LoginResponse.builder()
                .token(tokenDto.getToken())
                .refreshToken(tokenDto.getRefreshToken())
                .userId(user.getUsername())
                .userRole(userRole)
                .expiryTime(tokenDto.getExpiryDate())
                .build();
    }
    
    /**
     * 사용자 로그아웃을 처리합니다.
     *
     * @param request 로그아웃 요청 정보
     * @return 로그아웃 응답 정보
     */
    @Override
    @Transactional
    public LogoutResponse logout(LogoutRequest request) {
        log.info("로그아웃 요청");
        
        tokenProvider.invalidateToken(request.getToken());
        
        log.info("로그아웃 성공");
        
        return LogoutResponse.builder()
                .success(true)
                .build();
    }
    
    /**
     * 사용자 자격 증명을 검증합니다.
     *
     * @param user 사용자 정보
     * @param password 입력된 비밀번호
     * @throws InvalidCredentialsException 비밀번호가 일치하지 않을 때
     */
    private void validateCredentials(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("비밀번호 불일치: {}", user.getUsername());
            throw new InvalidCredentialsException("사용자 ID 또는 비밀번호가 잘못되었습니다.");
        }
    }
}
