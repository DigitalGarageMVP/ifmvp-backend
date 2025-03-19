/*
// ifmvp-back\email\src\main\java\com\email\email\config\DataInitializer.java
package com.email.email.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile({"dev", "test"})
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final JdbcTemplate commandJdbcTemplate;
    private final JdbcTemplate queryJdbcTemplate;

    @Autowired
    public DataInitializer(
            @Qualifier("commandJdbcTemplate") JdbcTemplate commandJdbcTemplate,
            @Qualifier("queryJdbcTemplate") JdbcTemplate queryJdbcTemplate) {
        this.commandJdbcTemplate = commandJdbcTemplate;
        this.queryJdbcTemplate = queryJdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing database schema for Email Service...");

        try {
            // 테이블이 존재하는지 확인하고 없으면 생성만 수행 (DROP 없이)
            createTablesIfNotExist();

            // 테스트 데이터 생성
            createTestData();

            logger.info("Email Service database schema initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database schema for Email Service", e);
            // 예외를 throw하지 않고 로깅만 - 애플리케이션 실행은 계속 진행
        }
    }

    private void createTablesIfNotExist() {
        logger.info("Creating tables if they don't exist...");

        try {
            // Command DB 테이블 생성
            commandJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS emails (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "user_id VARCHAR(100), " +
                    "subject VARCHAR(255), " +
                    "sender_email VARCHAR(100), " +
                    "sender_name VARCHAR(100), " +
                    "content TEXT, " +
                    "request_time TIMESTAMP, " +
                    "status VARCHAR(20), " +
                    "created_at TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            commandJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS email_recipients (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "email_id VARCHAR(36), " +
                    "recipient_email VARCHAR(100), " +
                    "status VARCHAR(20), " +
                    "receive_time TIMESTAMP, " +
                    "fail_reason VARCHAR(255), " +
                    "created_at TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            commandJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS attachment_metadata (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "file_name VARCHAR(255), " +
                    "content_type VARCHAR(100), " +
                    "file_size BIGINT, " +
                    "blob_name VARCHAR(255), " +
                    "container_name VARCHAR(100), " +
                    "upload_time TIMESTAMP, " +
                    "status VARCHAR(20), " +
                    "created_at TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            commandJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS email_attachments (" +
                    "email_id VARCHAR(36), " +
                    "attachment_id VARCHAR(36), " +
                    "created_at TIMESTAMP, " +
                    "PRIMARY KEY (email_id, attachment_id)" +
                    ")");
        } catch (Exception e) {
            logger.error("Error creating Command DB tables", e);
        }

        try {
            // Query DB 테이블 생성
            queryJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS emails (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "user_id VARCHAR(100), " +
                    "subject VARCHAR(255), " +
                    "sender_email VARCHAR(100), " +
                    "sender_name VARCHAR(100), " +
                    "content TEXT, " +
                    "request_time TIMESTAMP, " +
                    "status VARCHAR(20), " +
                    "created_at TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            queryJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS email_recipients (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "email_id VARCHAR(36), " +
                    "recipient_email VARCHAR(100), " +
                    "status VARCHAR(20), " +
                    "receive_time TIMESTAMP, " +
                    "fail_reason VARCHAR(255), " +
                    "created_at TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            queryJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS attachment_metadata (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "file_name VARCHAR(255), " +
                    "content_type VARCHAR(100), " +
                    "file_size BIGINT, " +
                    "blob_name VARCHAR(255), " +
                    "container_name VARCHAR(100), " +
                    "upload_time TIMESTAMP, " +
                    "status VARCHAR(20), " +
                    "created_at TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            queryJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS email_attachments (" +
                    "email_id VARCHAR(36), " +
                    "attachment_id VARCHAR(36), " +
                    "created_at TIMESTAMP, " +
                    "PRIMARY KEY (email_id, attachment_id)" +
                    ")");
        } catch (Exception e) {
            logger.error("Error creating Query DB tables", e);
        }
    }

    private void createTestData() {
        logger.info("Creating test data...");

        try {
            // 테스트 데이터가 이미 있는지 확인
            Integer count = commandJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM emails", Integer.class);

            if (count == null || count == 0) {
                String emailId = UUID.randomUUID().toString();
                String recipientId = UUID.randomUUID().toString();
                LocalDateTime now = LocalDateTime.now();

                // Command DB에 테스트 데이터 삽입
                commandJdbcTemplate.update(
                        "INSERT INTO emails (id, user_id, subject, sender_email, sender_name, content, request_time, status, created_at, updated_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        emailId,
                        "user01",
                        "테스트 이메일 제목",
                        "sender@example.com",
                        "발신자 이름",
                        "테스트 이메일 내용입니다.",
                        now,
                        "QUEUED",
                        now,
                        now
                );

                commandJdbcTemplate.update(
                        "INSERT INTO email_recipients (id, email_id, recipient_email, status, created_at) " +
                                "VALUES (?, ?, ?, ?, ?)",
                        recipientId,
                        emailId,
                        "recipient@example.com",
                        "PENDING",
                        now
                );

                logger.info("Test data created in Command DB successfully");
            } else {
                logger.info("Test data already exists in Command DB");
            }
        } catch (Exception e) {
            logger.error("Error creating test data in Command DB", e);
        }

        try {
            // Query DB에 테스트 데이터가 이미 있는지 확인
            Integer count = queryJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM emails", Integer.class);

            if (count == null || count == 0) {
                String emailId = UUID.randomUUID().toString();
                String recipientId = UUID.randomUUID().toString();
                LocalDateTime now = LocalDateTime.now();

                // Query DB에 테스트 데이터 삽입
                queryJdbcTemplate.update(
                        "INSERT INTO emails (id, user_id, subject, sender_email, sender_name, content, request_time, status, created_at, updated_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        emailId,
                        "user01",
                        "테스트 이메일 제목",
                        "sender@example.com",
                        "발신자 이름",
                        "테스트 이메일 내용입니다.",
                        now,
                        "QUEUED",
                        now,
                        now
                );

                queryJdbcTemplate.update(
                        "INSERT INTO email_recipients (id, email_id, recipient_email, status, created_at) " +
                                "VALUES (?, ?, ?, ?, ?)",
                        recipientId,
                        emailId,
                        "recipient@example.com",
                        "PENDING",
                        now
                );

                logger.info("Test data created in Query DB successfully");
            } else {
                logger.info("Test data already exists in Query DB");
            }
        } catch (Exception e) {
            logger.error("Error creating test data in Query DB", e);
        }
    }
}*/
