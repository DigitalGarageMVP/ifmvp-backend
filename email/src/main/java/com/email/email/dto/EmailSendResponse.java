package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 이메일 발송 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 발송 응답")
public class EmailSendResponse {
   
   @Schema(description = "성공 여부", example = "true")
   private boolean success;
   
   @Schema(description = "메시지 ID", example = "12345678-1234-1234-1234-123456789012")
   private String messageId;
   
   @Schema(description = "상태", example = "QUEUED")
   private String status;
}
