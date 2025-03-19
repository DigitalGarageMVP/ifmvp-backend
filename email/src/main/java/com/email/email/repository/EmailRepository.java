package com.email.email.repository;

import com.email.email.domain.*;

import java.util.List;
import java.util.Optional;

/**
* 이메일 저장소 인터페이스입니다.
*/
public interface EmailRepository {
   
   /**
    * 사용자 ID로 최근 이메일을 조회합니다.
    *
    * @param userId 사용자 ID
    * @param limit 조회 개수 제한
    * @return 최근 이메일 목록
    */
   List<Email> findRecentByUserId(String userId, int limit);
   
   /**
    * 이메일을 저장합니다.
    *
    * @param email 이메일 객체
    * @return 저장된 이메일 객체
    */
   Email saveEmail(Email email);
   
   /**
    * 수신자를 저장합니다.
    *
    * @param recipient 수신자 객체
    * @return 저장된 수신자 객체
    */
   EmailRecipient saveRecipient(EmailRecipient recipient);
   
   /**
    * 첨부파일 메타데이터를 저장합니다.
    *
    * @param metadata 첨부파일 메타데이터 객체
    * @return 저장된 첨부파일 메타데이터 객체
    */
   AttachmentMetadata saveAttachmentMetadata(AttachmentMetadata metadata);
   
   /**
    * 이메일-첨부파일 연결 정보를 저장합니다.
    *
    * @param emailAttachment 이메일-첨부파일 연결 객체
    * @return 저장된 이메일-첨부파일 연결 객체
    */
   EmailAttachment saveEmailAttachment(EmailAttachment emailAttachment);
   
   /**
    * 필터 조건에 맞는 이메일을 조회합니다.
    *
    * @param filters 이메일 필터 조건
    * @return 필터 조건에 맞는 이메일 목록
    */
   List<Email> findEmailsByFilters(EmailHistoryFilter filters);
   
   /**
    * 첨부파일 ID로 첨부파일 메타데이터를 조회합니다.
    *
    * @param attachmentId 첨부파일 ID
    * @return 첨부파일 메타데이터
    */
   Optional<AttachmentMetadata> findAttachmentById(String attachmentId);
   
   /**
    * 첨부파일 ID 목록으로 첨부파일 메타데이터를 조회합니다.
    *
    * @param attachmentIds 첨부파일 ID 목록
    * @return 첨부파일 메타데이터 목록
    */
   List<AttachmentMetadata> findAttachmentsByIds(List<String> attachmentIds);
   
   /**
    * 이메일 ID로 수신자를 조회합니다.
    *
    * @param emailId 이메일 ID
    * @return 수신자 목록
    */
   List<EmailRecipient> findRecipientsByEmailId(String emailId);
   
   /**
    * 이메일 ID와 수신자 이메일로 수신자를 조회합니다.
    *
    * @param emailId 이메일 ID
    * @param recipientEmail 수신자 이메일
    * @return 수신자 목록
    */
   List<EmailRecipient> findRecipientsByEmailIdAndEmail(String emailId, String recipientEmail);

   /**
    * 이메일 ID로 첨부파일 목록을 조회합니다.
    *
    * @param emailId 이메일 ID
    * @return 첨부파일 ID 목록
    */
   List<String> findAttachmentIdsByEmailId(String emailId);
}
