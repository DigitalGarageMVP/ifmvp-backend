package com.email.mock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 이벤트 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이벤트 응답")
public class EventResponse {
   
   @Schema(description = "성공 여부", example = "true")
   private boolean success;
   
   @Schema(description = "이벤트 ID", example = "12345678-1234-1234-1234-123456789012")
   private String eventId;
   
   @Schema(description = "이벤트 유형", example = "EMAIL_OPENED")
   private String eventType;
}
