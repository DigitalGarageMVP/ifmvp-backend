package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 최근 발송 이메일 목록 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "최근 발송 이메일 목록 응답")
public class RecentEmailListResponse {
   
   @Schema(description = "발신자", example = "sender@example.com")
   private String sender;
   
   @Schema(description = "제목", example = "안녕하세요, 이메일 제목입니다.")
   private String subject;
   
   @Schema(description = "수신자", example = "recipient@example.com")
   private String recipient;
   
   @Schema(description = "발송시간", example = "2023-06-01 12:34:56")
   private String sentTime;
   
   @Schema(description = "상태", example = "성공")
   private String status;
}
