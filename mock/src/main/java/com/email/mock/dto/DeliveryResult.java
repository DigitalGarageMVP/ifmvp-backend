package com.email.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 발송 결과 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "발송 결과")
public class DeliveryResult {
   
   @Schema(description = "수신자 이메일", example = "recipient@example.com")
   private String recipientEmail;
   
   @Schema(description = "상태", example = "DELIVERED")
   private String status;
   
   @Schema(description = "타임스탬프", example = "2023-06-01 12:34:56")
   private String timestamp;
}
