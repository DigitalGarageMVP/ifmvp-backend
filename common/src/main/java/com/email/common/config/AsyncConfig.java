package com.email.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정 클래스입니다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 비동기 처리를 위한 TaskExecutor를 생성합니다.
     *
     * @return TaskExecutor 인스턴스
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Email-Async-");
        executor.initialize();
        return executor;
    }
}
