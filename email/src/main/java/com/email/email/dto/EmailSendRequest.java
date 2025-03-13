package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* 이메일 발송 요청 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 발송 요청")
public class EmailSendRequest {
   
   @NotBlank(message = "제목은 필수 입력값입니다.")
   @Schema(description = "제목", example = "안녕하세요, 이메일 제목입니다.")
   private String subject;
   
   @NotBlank(message = "발신자 이메일은 필수 입력값입니다.")
   @Schema(description = "발신자 이메일", example = "sender@example.com")
   private String senderEmail;
   
   @NotBlank(message = "발신자 이름은 필수 입력값입니다.")
   @Schema(description = "발신자 이름", example = "홍길동")
   private String senderName;
   
   @NotEmpty(message = "수신자 이메일은 필수 입력값입니다.")
   @Schema(description = "수신자 이메일 목록", example = "[\"recipient1@example.com\", \"recipient2@example.com\"]")
   private List<String> recipientEmails;
   
   @NotBlank(message = "내용은 필수 입력값입니다.")
   @Schema(description = "내용", example = "안녕하세요, 이메일 내용입니다.")
   private String content;
   
   @Schema(description = "첨부파일 ID 목록", example = "[\"12345678-1234-1234-1234-123456789012\"]")
   private List<String> attachmentIds;
}
