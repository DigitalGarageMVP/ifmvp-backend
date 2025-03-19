// ifmvp-backend\email\src\main\java\com\email\email\repository\EmailRepositoryImpl.java
package com.email.email.repository;

import com.email.email.domain.*;
import com.email.email.exception.EmailRepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 이메일 저장소 구현 클래스입니다.
 */
@Slf4j
@Repository
public class EmailRepositoryImpl implements EmailRepository {

    private final JdbcTemplate commandDb;
    private final JdbcTemplate queryDb;

    private static final String INSERT_EMAIL_SQL =
            "INSERT INTO emails (id, user_id, subject, sender_email, sender_name, content, request_time, status, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_RECIPIENT_SQL =
            "INSERT INTO email_recipients (id, email_id, recipient_email, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_ATTACHMENT_METADATA_SQL =
            "INSERT INTO attachment_metadata (id, file_name, content_type, file_size, blob_name, downloadUrl, container_name, upload_time, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_EMAIL_ATTACHMENT_SQL =
            "INSERT INTO email_attachments (email_id, attachment_id, created_at) " +
                    "VALUES (?, ?, ?)";

    public EmailRepositoryImpl(@Qualifier("commandJdbcTemplate") JdbcTemplate commandDb,
                               @Qualifier("queryJdbcTemplate") JdbcTemplate queryDb) {
        this.commandDb = commandDb;
        this.queryDb = queryDb;
    }

    /**
     * 사용자 ID로 최근 이메일을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param limit 조회 개수 제한
     * @return 최근 이메일 목록
     */
    @Override
    public List<Email> findRecentByUserId(String userId, int limit) {
        log.debug("최근 이메일 조회: userId={}, limit={}", userId, limit);

        String sql = "SELECT * FROM emails WHERE user_id = ? ORDER BY request_time DESC LIMIT ?";

        try {
            // limit 파라미터도 함께 전달해야 합니다
            return queryDb.query(sql, new EmailRowMapper(), userId, limit);
        } catch (Exception e) {
            log.error("최근 이메일 조회 오류: userId={}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 이메일을 저장합니다.
     *
     * @param email 이메일 객체
     * @return 저장된 이메일 객체
     */
    @Override
    public Email saveEmail(Email email) {
        LocalDateTime now = LocalDateTime.now();
        log.debug("이메일 저장 시작: id={}, subject={}", email.getId(), email.getSubject());

        try {
            int rowsAffected = commandDb.update(INSERT_EMAIL_SQL,
                    email.getId(),
                    email.getUserId(),
                    email.getSubject(),
                    email.getSenderEmail(),
                    email.getSenderName(),
                    email.getContent(),
                    email.getRequestTime(),
                    email.getStatus().name(),
                    now,
                    now);

            log.debug("이메일 저장 완료: id={}, rowsAffected={}", email.getId(), rowsAffected);

            return email;
        } catch (Exception e) {
            log.error("이메일 저장 오류: emailId={}", email.getId(), e);
            throw new EmailRepositoryException("이메일 저장 실패", e);
        }
    }

    /**
     * 수신자를 저장합니다.
     *
     * @param recipient 수신자 객체
     * @return 저장된 수신자 객체
     */
    @Override
    public EmailRecipient saveRecipient(EmailRecipient recipient) {
        LocalDateTime now = LocalDateTime.now();
        log.debug("수신자 저장 시작: id={}, emailId={}, recipientEmail={}",
                recipient.getId(), recipient.getEmailId(), recipient.getRecipientEmail());

        try {
            int rowsAffected = commandDb.update(INSERT_RECIPIENT_SQL,
                    recipient.getId(),
                    recipient.getEmailId(),
                    recipient.getRecipientEmail(),
                    recipient.getStatus().name(),
                    now);

            log.debug("수신자 저장 완료: id={}, rowsAffected={}", recipient.getId(), rowsAffected);

            return recipient;
        } catch (Exception e) {
            log.error("수신자 저장 오류: recipientId={}", recipient.getId(), e);
            throw new EmailRepositoryException("수신자 저장 실패", e);
        }
    }

    /**
     * 첨부파일 메타데이터를 저장합니다.
     *
     * @param metadata 첨부파일 메타데이터 객체
     * @return 저장된 첨부파일 메타데이터 객체
     */
    @Override
    public AttachmentMetadata saveAttachmentMetadata(AttachmentMetadata metadata) {
        LocalDateTime now = LocalDateTime.now();
        log.debug("첨부파일 메타데이터 저장 시작: id={}, fileName={}", metadata.getId(), metadata.getFileName());

        try {
            // Command DB에 저장 - 10개 파라미터만 사용
            int rowsAffected = commandDb.update(INSERT_ATTACHMENT_METADATA_SQL,
                    metadata.getId(),
                    metadata.getFileName(),
                    metadata.getContentType(),
                    metadata.getFileSize(),
                    metadata.getBlobName(),
                    metadata.getDownloadUrl(),
                    metadata.getContainerName(),
                    metadata.getUploadTime(),
                    metadata.getStatus().name(),
                    now);  // created_at만 사용

            log.debug("첨부파일 메타데이터 저장 완료: id={}, rowsAffected={}", metadata.getId(), rowsAffected);

            return metadata;
        } catch (Exception e) {
            log.error("첨부파일 메타데이터 저장 오류: attachmentId={}", metadata.getId(), e);
            throw new EmailRepositoryException("첨부파일 메타데이터 저장 실패", e);
        }
    }

    /**
     * 이메일-첨부파일 연결 정보를 저장합니다.
     *
     * @param emailAttachment 이메일-첨부파일 연결 객체
     * @return 저장된 이메일-첨부파일 연결 객체
     */
    @Override
    public EmailAttachment saveEmailAttachment(EmailAttachment emailAttachment) {
        LocalDateTime now = LocalDateTime.now();
        log.debug("이메일-첨부파일 연결 저장 시작: emailId={}, attachmentId={}",
                emailAttachment.getEmailId(), emailAttachment.getAttachmentId());

        try {
            int rowsAffected = commandDb.update(INSERT_EMAIL_ATTACHMENT_SQL,
                    emailAttachment.getEmailId(),
                    emailAttachment.getAttachmentId(),
                    now);

            log.debug("이메일-첨부파일 연결 저장 완료: emailId={}, attachmentId={}, rowsAffected={}",
                    emailAttachment.getEmailId(), emailAttachment.getAttachmentId(), rowsAffected);

            return emailAttachment;
        } catch (Exception e) {
            log.error("이메일-첨부파일 연결 저장 오류: emailId={}, attachmentId={}",
                    emailAttachment.getEmailId(), emailAttachment.getAttachmentId(), e);
            throw new EmailRepositoryException("이메일-첨부파일 연결 저장 실패", e);
        }
    }

    /**
     * 필터 조건에 맞는 이메일을 조회합니다.
     *
     * @param filters 이메일 필터 조건
     * @return 필터 조건에 맞는 이메일 목록
     */
    @Override
    public List<Email> findEmailsByFilters(EmailHistoryFilter filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM emails WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // 시작일, 종료일 필터
        if (filters.getStartDate() != null && filters.getEndDate() != null) {
            sql.append(" AND request_time BETWEEN ? AND ?");
            params.add(filters.getStartDate().atStartOfDay());
            params.add(filters.getEndDate().plusDays(1).atStartOfDay());
        }

        // 발신자 이메일 필터
        if (filters.getSenderEmail() != null && !filters.getSenderEmail().isEmpty()) {
            sql.append(" AND sender_email = ?");
            params.add(filters.getSenderEmail());
        }

        // 상태 필터
        if (filters.getStatus() != null && !filters.getStatus().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(filters.getStatus());
        }

        // 최근 순으로 정렬
        sql.append(" ORDER BY request_time DESC");

        try {
            return queryDb.query(sql.toString(), new EmailRowMapper(), params.toArray());
        } catch (Exception e) {
            log.error("이메일 필터 조회 오류: filters={}", filters, e);
            return Collections.emptyList();
        }
    }

    /**
     * 첨부파일 ID로 첨부파일 메타데이터를 조회합니다.
     *
     * @param attachmentId 첨부파일 ID
     * @return 첨부파일 메타데이터
     */
    @Override
    public Optional<AttachmentMetadata> findAttachmentById(String attachmentId) {
        String sql = "SELECT * FROM attachment_metadata WHERE id = ?";

        try {
            // 먼저 Command DB에서 조회
            List<AttachmentMetadata> resultsFromCommand = commandDb.query(sql, new AttachmentMetadataRowMapper(), attachmentId);
            if (!resultsFromCommand.isEmpty()) {
                return Optional.of(resultsFromCommand.get(0));
            }

            // Command DB에 없으면 Query DB에서 조회
            List<AttachmentMetadata> resultsFromQuery = queryDb.query(sql, new AttachmentMetadataRowMapper(), attachmentId);
            return resultsFromQuery.isEmpty() ? Optional.empty() : Optional.of(resultsFromQuery.get(0));
        } catch (Exception e) {
            log.error("첨부파일 조회 오류: attachmentId={}", attachmentId, e);
            return Optional.empty();
        }
    }

    /**
     * 첨부파일 ID 목록으로 첨부파일 메타데이터를 조회합니다.
     *
     * @param attachmentIds 첨부파일 ID 목록
     * @return 첨부파일 메타데이터 목록
     */
    @Override
    public List<AttachmentMetadata> findAttachmentsByIds(List<String> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < attachmentIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT * FROM attachment_metadata WHERE id IN (" + placeholders.toString() + ")";

        try {
            return queryDb.query(sql, new AttachmentMetadataRowMapper(), attachmentIds.toArray());
        } catch (Exception e) {
            log.error("첨부파일 목록 조회 오류: attachmentIds={}", attachmentIds, e);
            return Collections.emptyList();
        }
    }

    /**
     * 이메일 ID로 수신자를 조회합니다.
     *
     * @param emailId 이메일 ID
     * @return 수신자 목록
     */
    @Override
    public List<EmailRecipient> findRecipientsByEmailId(String emailId) {
        String sql = "SELECT * FROM email_recipients WHERE email_id = ?";

        try {
            return queryDb.query(sql, new EmailRecipientRowMapper(), emailId);
        } catch (Exception e) {
            log.error("수신자 조회 오류: emailId={}", emailId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 이메일 ID와 수신자 이메일로 수신자를 조회합니다.
     *
     * @param emailId 이메일 ID
     * @param recipientEmail 수신자 이메일
     * @return 수신자 목록
     */
    @Override
    public List<EmailRecipient> findRecipientsByEmailIdAndEmail(String emailId, String recipientEmail) {
        String sql = "SELECT * FROM email_recipients WHERE email_id = ? AND recipient_email = ?";

        try {
            return queryDb.query(sql, new EmailRecipientRowMapper(), emailId, recipientEmail);
        } catch (Exception e) {
            log.error("수신자 조회 오류: emailId={}, recipientEmail={}", emailId, recipientEmail, e);
            return Collections.emptyList();
        }
    }

    /**
     * 이메일 로우 매퍼 클래스입니다.
     */
    private static class EmailRowMapper implements RowMapper<Email> {
        @Override
        public Email mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Email.builder()
                    .id(rs.getString("id"))
                    .userId(rs.getString("user_id"))
                    .subject(rs.getString("subject"))
                    .senderEmail(rs.getString("sender_email"))
                    .senderName(rs.getString("sender_name"))
                    .content(rs.getString("content"))
                    .requestTime(rs.getTimestamp("request_time").toLocalDateTime())
                    .status(EmailStatus.valueOf(rs.getString("status")))
                    .build();
        }
    }

    /**
     * 이메일 수신자 로우 매퍼 클래스입니다.
     */
    private static class EmailRecipientRowMapper implements RowMapper<EmailRecipient> {
        @Override
        public EmailRecipient mapRow(ResultSet rs, int rowNum) throws SQLException {
            EmailRecipient.EmailRecipientBuilder builder = EmailRecipient.builder()
                    .id(rs.getString("id"))
                    .emailId(rs.getString("email_id"))
                    .recipientEmail(rs.getString("recipient_email"))
                    .status(RecipientStatus.valueOf(rs.getString("status")));

            if (rs.getTimestamp("receive_time") != null) {
                builder.receiveTime(rs.getTimestamp("receive_time").toLocalDateTime());
            }

            if (rs.getString("fail_reason") != null) {
                builder.failReason(rs.getString("fail_reason"));
            }

            return builder.build();
        }
    }

    /**
     * 첨부파일 메타데이터 로우 매퍼 클래스입니다.
     */
    private static class AttachmentMetadataRowMapper implements RowMapper<AttachmentMetadata> {
        @Override
        public AttachmentMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
            return AttachmentMetadata.builder()
                    .id(rs.getString("id"))
                    .fileName(rs.getString("file_name"))
                    .contentType(rs.getString("content_type"))
                    .fileSize(rs.getLong("file_size"))
                    .blobName(rs.getString("blob_name"))
                    .containerName(rs.getString("container_name"))
                    .uploadTime(rs.getTimestamp("upload_time").toLocalDateTime())
                    .status(AttachmentStatus.valueOf(rs.getString("status")))
                    .downloadUrl(rs.getString("downloadurl"))
                    .build();
        }
    }

    // File: ifmvp-backend\email\src\main\java\com\email\email\repository\EmailRepositoryImpl.java
    @Override
    public List<String> findAttachmentIdsByEmailId(String emailId) {
        log.debug("이메일 첨부파일 ID 조회: emailId={}", emailId);

        String sql = "SELECT attachment_id FROM email_attachments WHERE email_id = ?";

        try {
            return queryDb.queryForList(sql, String.class, emailId);
        } catch (Exception e) {
            log.error("이메일 첨부파일 ID 조회 오류: emailId={}", emailId, e);
            return Collections.emptyList();
        }
    }
}