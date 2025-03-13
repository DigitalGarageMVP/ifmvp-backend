package com.email.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 이메일 오픈 요청 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 오픈 요청")
public class EmailOpenRequest {
   
   @NotBlank(message = "이메일 ID는 필수 입력값입니다.")
   @Schema(description = "이메일 ID", example = "12345678-1234-1234-1234-123456789012")
   private String emailId;
   
   @NotBlank(message = "수신자 이메일은 필수 입력값입니다.")
   @Schema(description = "수신자 이메일", example = "recipient@example.com")
   private String recipientEmail;
   
   @NotBlank(message = "오픈 시간은 필수 입력값입니다.")
   @Schema(description = "오픈 시간", example = "2023-06-01 12:34:56")
   private String openTime;
   
   @Schema(description = "디바이스 정보", example = "iOS 16, iPhone 12")
   private String deviceInfo;
}
