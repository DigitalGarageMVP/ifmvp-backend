package com.email.email.service;

import com.email.email.dto.EmailHistoryResponse;
import com.email.email.dto.EmailSendRequest;
import com.email.email.dto.EmailSendResponse;
import com.email.email.dto.RecentEmailListResponse;

import java.time.LocalDate;
import java.util.List;

/**
* 이메일 서비스 인터페이스입니다.
*/
public interface EmailService {
   
   /**
    * 최근 발송 이메일 목록을 조회합니다.
    *
    * @param userId 사용자 ID
    * @return 최근 발송 이메일 목록
    */
   List<RecentEmailListResponse> getRecentEmails(String userId);
   
   /**
    * 이메일 발송 요청을 처리합니다.
    *
    * @param request 이메일 발송 요청 정보
    * @return 이메일 발송 응답 정보
    */
   EmailSendResponse sendEmail(EmailSendRequest request);
   
   /**
    * 이메일 발송 이력을 상세 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param senderEmail 발신자 이메일
    * @param status 발송 상태
    * @param recipientEmail 수신자 이메일
    * @return 이메일 발송 이력 목록
    */
   List<EmailHistoryResponse> getEmailHistory(
           LocalDate startDate, LocalDate endDate, String senderEmail, String status, String recipientEmail);
}
