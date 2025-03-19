package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* 이메일 발송 이력 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 발송 이력 응답")
public class EmailHistoryResponse {
   
   @Schema(description = "발신자 이메일", example = "sender@example.com")
   private String senderEmail;
   
   @Schema(description = "요청시간", example = "2023-06-01 12:34:56")
   private String requestTime;
   
   @Schema(description = "상태", example = "SUCCESS")
   private String status;
   
   @Schema(description = "수신자 이메일", example = "recipient@example.com")
   private String recipientEmail;
   
   @Schema(description = "수신시간", example = "2023-06-01 12:35:00")
   private String receiveTime;
   
   @Schema(description = "수신상태", example = "DELIVERED")
   private String receiveStatus;
   
   @Schema(description = "실패사유", example = "수신자 메일박스 용량 초과")
   private String failReason;
   
   @Schema(description = "제목", example = "안녕하세요, 이메일 제목입니다.")
   private String subject;

   @Schema(description = "첨부파일 ID 목록", example = "[\"12345678-1234-1234-1234-123456789012\"]")
   private List<String> attachmentIds;
}
