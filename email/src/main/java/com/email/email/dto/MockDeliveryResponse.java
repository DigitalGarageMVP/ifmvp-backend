// ifmvp-back\email\src\main\java\com\email\email\dto\MockDeliveryResponse.java
package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
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

   /**
    * toString 메소드를 오버라이드하여 로그 출력을 개선합니다.
    *
    * @return 객체의 문자열 표현
    */
   @Override
   public String toString() {
      return "MockDeliveryResponse{" +
              "success=" + success +
              ", mockEmailId='" + mockEmailId + '\'' +
              ", deliveryStatus='" + deliveryStatus + '\'' +
              ", results=" + (results != null ? results.size() : "null") +
              '}';
   }

   /**
    * 발송 상태를 안전하게 반환합니다.
    * null인 경우 기본값을 반환합니다.
    *
    * @return 발송 상태
    */
   public String getDeliveryStatus() {
      return deliveryStatus != null ? deliveryStatus : "UNKNOWN";
   }

   /**
    * 모의 이메일 ID를 안전하게 반환합니다.
    * null인 경우 빈 문자열을 반환합니다.
    *
    * @return 모의 이메일 ID
    */
   public String getMockEmailId() {
      return mockEmailId != null ? mockEmailId : "";
   }

   /**
    * 발송 결과 목록을 안전하게 반환합니다.
    * null인 경우 빈 목록을 반환합니다.
    *
    * @return 발송 결과 목록
    */
   public List<DeliveryResult> getResults() {
      return results != null ? results : Collections.emptyList();
   }
}