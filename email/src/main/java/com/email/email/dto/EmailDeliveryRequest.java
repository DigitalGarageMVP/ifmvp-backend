package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* 이메일 발송 시뮬레이션 요청 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 발송 시뮬레이션 요청")
public class EmailDeliveryRequest {
   
   @Schema(description = "이메일 ID", example = "12345678-1234-1234-1234-123456789012")
   private String emailId;
   
   @Schema(description = "발신자 이메일", example = "sender@example.com")
   private String senderEmail;
   
   @Schema(description = "수신자 이메일 목록", example = "[\"recipient1@example.com\", \"recipient2@example.com\"]")
   private List<String> recipientEmails;
   
   @Schema(description = "제목", example = "안녕하세요, 이메일 제목입니다.")
   private String subject;
   
   @Schema(description = "내용", example = "안녕하세요, 이메일 내용입니다.")
   private String content;
   
   @Schema(description = "첨부파일 ID 목록", example = "[\"12345678-1234-1234-1234-123456789012\"]")
   private List<String> attachmentIds;
}
