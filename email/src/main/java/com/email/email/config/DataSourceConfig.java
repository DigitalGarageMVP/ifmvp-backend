package com.email.email.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 데이터 소스 설정 클래스입니다.
 */
@Configuration
public class DataSourceConfig {

    /**
     * 커맨드 데이터 소스를 생성합니다.
     *
     * @return 커맨드 데이터 소스
     */
    @Primary
    @Bean(name = "commandDataSource")
    @ConfigurationProperties(prefix = "spring.datasources.command")
    public DataSource commandDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 쿼리 데이터 소스를 생성합니다.
     *
     * @return 쿼리 데이터 소스
     */
    @Bean(name = "queryDataSource")
    @ConfigurationProperties(prefix = "spring.datasources.query")
    public DataSource queryDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 커맨드 JDBC 템플릿을 생성합니다.
     *
     * @param dataSource 커맨드 데이터 소스
     * @return 커맨드 JDBC 템플릿
     */
    @Primary
    @Bean(name = "commandJdbcTemplate")
    public JdbcTemplate commandJdbcTemplate(@Qualifier("commandDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 쿼리 JDBC 템플릿을 생성합니다.
     *
     * @param dataSource 쿼리 데이터 소스
     * @return 쿼리 JDBC 템플릿
     */
    @Bean(name = "queryJdbcTemplate")
    public JdbcTemplate queryJdbcTemplate(@Qualifier("queryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}