// ifmvp-backend\email\src\main\java\com\email\email\service\EmailServiceImpl.java
package com.email.email.service;

import com.email.common.exception.BusinessException;
import com.email.common.exception.ErrorCode;
import com.email.common.util.ValidationUtils;
import com.email.email.domain.*;
import com.email.email.dto.*;
import com.email.email.repository.EmailRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 이메일 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int RECENT_EMAIL_LIMIT = 5;
    private static final int MAX_RECIPIENT_COUNT = 10000;
    private static final int MAX_HISTORY_DAYS = 365;

    private final EmailRepository emailRepository;
    private final AttachmentService attachmentService;
    private final MockDeliveryClient mockDeliveryClient;
    private final MessagePublisher messagePublisher;

    private final Timer emailSendTimer;
    private final Counter emailSendRequestCounter;

    /**
     * 최근 발송 이메일 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 최근 발송 이메일 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<RecentEmailListResponse> getRecentEmails(String userId) {
        log.info("최근 발송 이메일 목록 조회: userId={}", userId);

        List<Email> recentEmails = emailRepository.findRecentByUserId(userId, RECENT_EMAIL_LIMIT);

        if (recentEmails.isEmpty()) {
            log.info("최근 발송 이메일이 없습니다. userId={}", userId);
            return Collections.emptyList();
        }

        return recentEmails.stream()
                .map(email -> {
                    List<EmailRecipient> recipients = emailRepository.findRecipientsByEmailId(email.getId());
                    String recipientEmail = recipients.isEmpty() ? "" : recipients.get(0).getRecipientEmail();

                    return RecentEmailListResponse.builder()
                            .sender(email.getSenderEmail())
                            .subject(email.getSubject())
                            .recipient(recipientEmail)
                            .sentTime(email.getRequestTime().format(DATE_TIME_FORMATTER))
                            .status(email.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 이메일 발송 요청을 처리합니다.
     *
     * @param request 이메일 발송 요청 정보
     * @return 이메일 발송 응답 정보
     */
    @Override
    @Transactional
    public EmailSendResponse sendEmail(EmailSendRequest request) {

        log.info("이메일 발송 요청: senderEmail={}, subject={}", request.getSenderEmail(), request.getSubject());

        emailSendRequestCounter.increment();
        Timer.Sample sample = Timer.start();

        try {
            validateEmailData(request);

            String emailId = UUID.randomUUID().toString();

            // 이메일 메타데이터 저장
            Email email = Email.builder()
                    .id(emailId)
                    .userId(request.getSenderEmail()) // 임시로 발신자 이메일을 userId로 사용
                    .subject(request.getSubject())
                    .senderEmail(request.getSenderEmail())
                    .senderName(request.getSenderName())
                    .content(request.getContent())
                    .requestTime(LocalDateTime.now())
                    .status(EmailStatus.QUEUED)
                    .build();

            log.debug("저장할 이메일 정보: {}", email);

            try {
                Email savedEmail = emailRepository.saveEmail(email);
                log.info("이메일 메타데이터 저장 완료: id={}", savedEmail.getId());

                // 수신자 정보 저장
                for (String recipientEmail : request.getRecipientEmails()) {
                    EmailRecipient recipient = EmailRecipient.builder()
                            .id(UUID.randomUUID().toString())
                            .emailId(emailId)
                            .recipientEmail(recipientEmail)
                            .status(RecipientStatus.PENDING)
                            .build();

                    emailRepository.saveRecipient(recipient);
                }
                log.info("수신자 정보 저장 완료: count={}", request.getRecipientEmails().size());

                // 첨부파일 연결 (있는 경우)
                if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
                    List<AttachmentMetadata> attachments = attachmentService.getAttachmentDetails(request.getAttachmentIds());

                    for (AttachmentMetadata attachment : attachments) {
                        EmailAttachment emailAttachment = EmailAttachment.builder()
                                .emailId(emailId)
                                .attachmentId(attachment.getId())
                                .build();
                        emailRepository.saveEmailAttachment(emailAttachment);
                    }
                    log.info("첨부파일 연결 완료: count={}", attachments.size());
                }

                // 목업 서비스로 발송 요청
                EmailDeliveryRequest deliveryRequest = EmailDeliveryRequest.builder()
                        .emailId(emailId)
                        .senderEmail(request.getSenderEmail())
                        .recipientEmails(request.getRecipientEmails())
                        .subject(request.getSubject())
                        .content(request.getContent())
                        .attachmentIds(request.getAttachmentIds())
                        .build();

                log.info("목업 서비스로 발송 요청 시작");
                MockDeliveryResponse deliveryResponse = mockDeliveryClient.deliverEmail(deliveryRequest);
                log.info("목업 서비스 응답 수신: status={}", deliveryResponse.getDeliveryStatus());

                // 발송 이벤트 발행
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("emailId", emailId);
                eventData.put("status", deliveryResponse.getDeliveryStatus());
                eventData.put("mockEmailId", deliveryResponse.getMockEmailId());

                EmailEvent emailEvent = EmailEvent.builder()
                        .eventType("EMAIL_SENT")
                        .emailData(eventData)
                        .build();

                messagePublisher.publishEmailEvent(emailEvent);
                log.info("이메일 발송 이벤트 발행 완료");

                return EmailSendResponse.builder()
                        .success(deliveryResponse.isSuccess())
                        .messageId(emailId)
                        .status(deliveryResponse.getDeliveryStatus())
                        .build();
            } catch (Exception e) {
                log.error("이메일 발송 처리 중 오류 발생", e);
                // 예외의 자세한 내용 로깅
                log.error("Exception class: {}, Message: {}", e.getClass().getName(), e.getMessage());
                if (e.getCause() != null) {
                    log.error("Cause: {}, Message: {}", e.getCause().getClass().getName(), e.getCause().getMessage());
                }

                // 스택 트레이스 전체 로깅
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                log.error("Stack trace: {}", sw.toString());

                throw e; // 예외 재발생
            }
        } catch (Exception e) {
//            log.error("이메일 발송 처리 중 오류 발생", e);
            // 기존 에러 처리 코드...

            throw e;
        }


    }


    /**
     * 이메일 발송 이력을 상세 조회합니다.
     *
     * @param startDate      시작일
     * @param endDate        종료일
     * @param senderEmail    발신자 이메일
     * @param status         발송 상태
     * @param recipientEmail 수신자 이메일
     * @return 이메일 발송 이력 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmailHistoryResponse> getEmailHistory(
            LocalDate startDate, LocalDate endDate, String senderEmail, String status, String recipientEmail) {
        log.info("이메일 발송 이력 조회: startDate={}, endDate={}, senderEmail={}, status={}, recipientEmail={}",
                startDate, endDate, senderEmail, status, recipientEmail);

        validateDateRange(startDate, endDate);

        EmailHistoryFilter filter = EmailHistoryFilter.builder()
                .startDate(startDate)
                .endDate(endDate)
                .senderEmail(senderEmail)
                .status(status)
                .recipientEmail(recipientEmail)
                .build();

        List<Email> emails = emailRepository.findEmailsByFilters(filter);

        List<EmailHistoryResponse> responses = new ArrayList<>();

        for (Email email : emails) {
            List<EmailRecipient> recipients;

            if (recipientEmail != null && !recipientEmail.isEmpty()) {
                recipients = emailRepository.findRecipientsByEmailIdAndEmail(email.getId(), recipientEmail);
            } else {
                recipients = emailRepository.findRecipientsByEmailId(email.getId());
            }

            // 이메일의 첨부파일 ID 목록 조회
            List<String> attachmentIds = emailRepository.findAttachmentIdsByEmailId(email.getId());

            for (EmailRecipient recipient : recipients) {
                responses.add(EmailHistoryResponse.builder()
                        .senderEmail(email.getSenderEmail())
                        .requestTime(email.getRequestTime().format(DATE_TIME_FORMATTER))
                        .status(email.getStatus().name())
                        .recipientEmail(recipient.getRecipientEmail())
                        .receiveTime(recipient.getReceiveTime() != null
                                ? recipient.getReceiveTime().format(DATE_TIME_FORMATTER)
                                : null)
                        .receiveStatus(recipient.getStatus().name())
                        .failReason(recipient.getFailReason())
                        .subject(email.getSubject())
                        .attachmentIds(attachmentIds) // 첨부파일 ID 목록 추가
                        .build());
            }
        }

        // 최근 날짜순으로 정렬
        responses.sort(Comparator.comparing(EmailHistoryResponse::getRequestTime).reversed());

        return responses;
    }

    /**
     * 이메일 데이터의 유효성을 검사합니다.
     *
     * @param request 이메일 발송 요청 정보
     * @throws BusinessException 유효성 검사 실패 시
     */
    private void validateEmailData(EmailSendRequest request) {
        // 발신자 이메일 유효성 검사
        if (!ValidationUtils.validateEmail(request.getSenderEmail())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "발신자 이메일 형식이 올바르지 않습니다.");
        }

        // 발신자 이름 유효성 검사
        if (request.getSenderName().length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "발신자 이름은 1자 이상이어야 합니다.");
        }

        // 수신자 이메일 유효성 검사
        if (request.getRecipientEmails().size() > MAX_RECIPIENT_COUNT) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    String.format("수신자는 최대 %d명까지 지정할 수 있습니다.", MAX_RECIPIENT_COUNT));
        }

        for (String recipientEmail : request.getRecipientEmails()) {
            if (!ValidationUtils.validateEmail(recipientEmail)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "수신자 이메일 형식이 올바르지 않습니다: " + recipientEmail);
            }
        }

        // 제목 유효성 검사
        if (request.getSubject().length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이메일 제목은 1자 이상이어야 합니다.");
        }

        // 내용 유효성 검사
        if (request.getContent().length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이메일 내용은 1자 이상이어야 합니다.");
        }
    }

    /**
     * 날짜 범위의 유효성을 검사합니다.
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @throws BusinessException 유효성 검사 실패 시
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "시작일은 종료일보다 이전이어야 합니다.");
        }

        if (startDate.plusDays(MAX_HISTORY_DAYS).isBefore(endDate)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    String.format("조회 기간은 최대 %d일로 제한됩니다.", MAX_HISTORY_DAYS));
        }
    }
}