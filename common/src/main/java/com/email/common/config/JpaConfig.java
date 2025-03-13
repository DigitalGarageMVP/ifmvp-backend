package com.email.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA 설정 클래스입니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
    /**
     * JPA Auditing을 위한 AuditorAware를 생성합니다.
     *
     * @return AuditorAware 인스턴스
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }
}
