package com.email.email.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 이메일 서비스의 메트릭 설정 클래스입니다.
 * 검증 지표에 필요한 메트릭을 정의합니다.
 */
@Configuration
public class MetricsConfig {

    /**
     * 이메일 발송 API 응답 시간 측정용 타이머
     *
     * @param registry 메트릭 레지스트리
     * @return 이메일 발송 API 응답 시간 타이머
     */
    @Bean
    public Timer emailSendTimer(MeterRegistry registry) {
        return Timer.builder("email_send_time")
                .description("이메일 발송 API 응답 시간")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(100),
                        Duration.ofMillis(300),
                        Duration.ofMillis(500),
                        Duration.ofMillis(1000)
                )
                .register(registry);
    }

    /**
     * 첨부파일 SAS 토큰 발급 시간 측정용 타이머
     *
     * @param registry 메트릭 레지스트리
     * @return SAS 토큰 발급 타이머
     */
    @Bean
    public Timer sasTokenGenerationTimer(MeterRegistry registry) {
        return Timer.builder("sas_token_generation_time")
                .description("SAS 토큰 발급 시간")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(500)
                )
                .register(registry);
    }

    /**
     * 이메일 요청 카운터
     *
     * @param registry 메트릭 레지스트리
     * @return 이메일 요청 카운터
     */
    @Bean
    public Counter emailRequestCounter(MeterRegistry registry) {
        return Counter.builder("email_requests_total")
                .description("총 이메일 요청 수")
                .register(registry);
    }

    /**
     * 이메일 발송 오류 카운터
     *
     * @param registry 메트릭 레지스트리
     * @return 이메일 발송 오류 카운터
     */
    @Bean
    public Counter emailErrorCounter(MeterRegistry registry) {
        return Counter.builder("email_errors_total")
                .description("이메일 발송 오류 수")
                .register(registry);
    }

    /**
     * 첨부파일 크기 히스토그램
     *
     * @param registry 메트릭 레지스트리
     * @return 첨부파일 크기 히스토그램
     */
    @Bean
    public Timer attachmentSizeTimer(MeterRegistry registry) {
        return Timer.builder("attachment_size")
                .description("첨부파일 크기 분포")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(1), // 1KB
                        Duration.ofMillis(1024), // 1MB
                        Duration.ofMillis(5 * 1024), // 5MB
                        Duration.ofMillis(10 * 1024) // 10MB
                )
                .register(registry);
    }

    /**
     * 첨부파일 업로드 요청 카운터
     *
     * @param registry 메트릭 레지스트리
     * @return 첨부파일 업로드 요청 카운터
     */
    @Bean
    public Counter attachmentUploadCounter(MeterRegistry registry) {
        return Counter.builder("attachment_uploads_total")
                .description("첨부파일 업로드 요청 수")
                .register(registry);
    }
}