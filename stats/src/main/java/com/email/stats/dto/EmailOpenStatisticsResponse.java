package com.email.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 이메일 오픈 통계 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 오픈 통계 응답")
public class EmailOpenStatisticsResponse {
   
   @Schema(description = "날짜", example = "2023-06-01")
   private String date;
   
   @Schema(description = "이메일 카테고리", example = "MARKETING")
   private String emailCategory;
   
   @Schema(description = "총 이메일 수", example = "100")
   private int totalEmails;
   
   @Schema(description = "오픈 건수", example = "80")
   private int openCount;
   
   @Schema(description = "오픈률", example = "80.0")
   private double openRate;
}
