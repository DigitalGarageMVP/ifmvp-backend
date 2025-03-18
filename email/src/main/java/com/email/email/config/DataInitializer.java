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
            // Command DB 테이블 생성
            initializeCommandDB();

            // Query DB 테이블 생성
            initializeQueryDB();

            // 테스트 데이터 생성
            createTestData();

            logger.info("Email Service database schema initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database schema for Email Service", e);
            throw e;
        }
    }

    private void initializeCommandDB() {
        logger.info("Initializing Command DB schema...");

        // 테이블 삭제 (필요한 경우)
        commandJdbcTemplate.execute("DROP TABLE IF EXISTS email_attachments CASCADE");
        commandJdbcTemplate.execute("DROP TABLE IF EXISTS email_recipients CASCADE");
        commandJdbcTemplate.execute("DROP TABLE IF EXISTS emails CASCADE");
        commandJdbcTemplate.execute("DROP TABLE IF EXISTS attachment_metadata CASCADE");

        // 테이블 생성
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

        logger.info("Command DB schema initialized successfully");
    }

    private void initializeQueryDB() {
        logger.info("Initializing Query DB schema...");

        // 테이블 삭제 (필요한 경우)
        queryJdbcTemplate.execute("DROP TABLE IF EXISTS email_attachments CASCADE");
        queryJdbcTemplate.execute("DROP TABLE IF EXISTS email_recipients CASCADE");
        queryJdbcTemplate.execute("DROP TABLE IF EXISTS emails CASCADE");
        queryJdbcTemplate.execute("DROP TABLE IF EXISTS attachment_metadata CASCADE");

        // 테이블 생성
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

        logger.info("Query DB schema initialized successfully");
    }

    private void createTestData() {
        logger.info("Creating test data...");

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

        // Query DB에도 동일한 테스트 데이터 삽입
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

        logger.info("Test data created successfully");
    }
}