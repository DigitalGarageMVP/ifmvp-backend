package com.email.email.service;

import com.email.email.domain.AttachmentMetadata;
import com.email.email.dto.AttachmentMetadataRequest;
import com.email.email.dto.AttachmentMetadataResponse;
import com.email.email.dto.SasTokenResponse;

import java.util.List;

/**
* 첨부파일 서비스 인터페이스입니다.
*/
public interface AttachmentService {
   
   /**
    * 첨부파일 업로드용 SAS 토큰을 발급합니다.
    *
    * @param request 첨부파일 메타데이터 요청 정보
    * @return SAS 토큰 응답 정보
    */
   SasTokenResponse generateSasToken(AttachmentMetadataRequest request);
   
   /**
    * 첨부파일 메타데이터를 조회합니다.
    *
    * @param attachmentId 첨부파일 ID
    * @return 첨부파일 메타데이터 응답 정보
    */
   AttachmentMetadataResponse getAttachmentMetadata(String attachmentId);
   
   /**
    * 첨부파일 상세 정보를 조회합니다.
    *
    * @param attachmentIds 첨부파일 ID 목록
    * @return 첨부파일 메타데이터 목록
    */
   List<AttachmentMetadata> getAttachmentDetails(List<String> attachmentIds);
}
