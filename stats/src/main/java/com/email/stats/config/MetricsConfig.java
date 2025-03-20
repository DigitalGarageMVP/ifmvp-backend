package com.email.stats.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer statsQueryTimer(MeterRegistry registry) {
        return Timer.builder("stats_query_time")
                .description("Time taken to query statistics")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(500), Duration.ofMillis(1000), Duration.ofMillis(1500), Duration.ofMillis(3000))
                .register(registry);
    }

    @Bean
    public Counter statsRequestCounter(MeterRegistry registry) {
        return Counter.builder("stats_requests_total")
                .description("Total number of statistics requests")
                .register(registry);
    }

    @Bean
    public Counter emailOpenEventCounter(MeterRegistry registry) {
        return Counter.builder("email_open_events_total")
                .description("Total number of email open events")
                .register(registry);
    }

    @Bean
    public Counter attachmentClickEventCounter(MeterRegistry registry) {
        return Counter.builder("attachment_click_events_total")
                .description("Total number of attachment click events")
                .register(registry);
    }
}