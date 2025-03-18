package com.email.auth.service;

import com.email.auth.domain.User;
import com.email.common.dto.JwtTokenDTO;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스입니다.
 */
@Slf4j
@Component
public class TokenProvider {

    @Getter
    @Value("${jwt.access-token-validity}")
    private long tokenValidityInMilliseconds;
    
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidityInMilliseconds;
    
    private final SecretKey key;
    private final JwtParser jwtParser;
    private final Set<String> blacklistedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        // 입력된 비밀 키가 최소 길이 요구사항을 충족하는지 확인
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        // 키가 32바이트(256비트) 미만인 경우 해시 함수를 사용하여 확장
        if (keyBytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
            }
        }

        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }
    
    /**
     * 사용자 정보를 기반으로 JWT 토큰을 생성합니다.
     *
     * @param user 사용자 정보
     * @return 생성된 JWT 토큰 정보
     */
    public JwtTokenDTO createToken(User user) {
        Date now = new Date();
        Date accessTokenValidity = new Date(now.getTime() + tokenValidityInMilliseconds);
        Date refreshTokenValidity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        
        String accessToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(now)
                .setExpiration(accessTokenValidity)
                .signWith(key)
                .compact();
        
        String refreshToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(refreshTokenValidity)
                .signWith(key)
                .compact();
        
        LocalDateTime expiryDateTime = LocalDateTime.ofInstant(
                accessTokenValidity.toInstant(), ZoneId.systemDefault());
        
        return JwtTokenDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiryDate(expiryDateTime)
                .build();
    }
    
    /**
     * JWT 토큰의 유효성을 검사합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰 유효 여부
     */
    public boolean validateToken(String token) {
        if (blacklistedTokens.contains(token)) {
            log.warn("블랙리스트에 등록된 토큰 사용 시도");
            return false;
        }
        
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰", e);
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰", e);
            return false;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰", e);
            return false;
        } catch (SignatureException e) {
            log.warn("잘못된 JWT 서명", e);
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰", e);
            return false;
        }
    }
    
    /**
     * JWT 토큰으로부터 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUsernameFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }
    
    /**
     * 토큰을 무효화합니다(로그아웃).
     *
     * @param token 무효화할 토큰
     */
    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
        log.info("토큰 블랙리스트 추가: {}", token.substring(0, 10) + "...");
    }
}
