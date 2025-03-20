package com.email.email.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer emailSendTimer(MeterRegistry registry) {
        return Timer.builder("email_send_time")
                .description("Time taken to process email sending requests")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(500), Duration.ofMillis(1000), Duration.ofMillis(1500), Duration.ofMillis(3000))
                .register(registry);
    }

    @Bean
    public Counter emailSendRequestCounter(MeterRegistry registry) {
        return Counter.builder("email_send_requests_total")
                .description("Total number of email send requests")
                .register(registry);
    }
}