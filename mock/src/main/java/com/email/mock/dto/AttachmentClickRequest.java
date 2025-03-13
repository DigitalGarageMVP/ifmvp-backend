package com.email.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 첨부파일 클릭 요청 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "첨부파일 클릭 요청")
public class AttachmentClickRequest {
   
   @NotBlank(message = "이메일 ID는 필수 입력값입니다.")
   @Schema(description = "이메일 ID", example = "12345678-1234-1234-1234-123456789012")
   private String emailId;
   
   @NotBlank(message = "첨부파일 ID는 필수 입력값입니다.")
   @Schema(description = "첨부파일 ID", example = "12345678-1234-1234-1234-123456789012")
   private String attachmentId;
   
   @NotBlank(message = "수신자 이메일은 필수 입력값입니다.")
   @Schema(description = "수신자 이메일", example = "recipient@example.com")
   private String recipientEmail;
   
   @NotBlank(message = "클릭 시간은 필수 입력값입니다.")
   @Schema(description = "클릭 시간", example = "2023-06-01 12:34:56")
   private String clickTime;
   
   @Schema(description = "디바이스 정보", example = "iOS 16, iPhone 12")
   private String deviceInfo;
}
