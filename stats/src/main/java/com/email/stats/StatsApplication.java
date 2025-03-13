package com.email.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 통계 서비스의 메인 애플리케이션 클래스입니다.
 * 
 * @author 서지
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.email.stats", "com.email.common"})
public class StatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatsApplication.class, args);
    }
}
