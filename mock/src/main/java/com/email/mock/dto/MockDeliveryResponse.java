package com.email.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* 이메일 발송 시뮬레이션 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 발송 시뮬레이션 응답")
public class MockDeliveryResponse {
   
   @Schema(description = "성공 여부", example = "true")
   private boolean success;
   
   @Schema(description = "모의 이메일 ID", example = "12345678-1234-1234-1234-123456789012")
   private String mockEmailId;
   
   @Schema(description = "발송 상태", example = "DELIVERED")
   private String deliveryStatus;
   
   @Schema(description = "발송 결과 목록")
   private List<DeliveryResult> results;
}
