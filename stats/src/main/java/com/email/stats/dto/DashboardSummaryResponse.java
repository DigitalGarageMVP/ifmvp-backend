package com.email.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* 대시보드 요약 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "대시보드 요약 응답")
public class DashboardSummaryResponse {
   
   @Schema(description = "총 발송 건수", example = "1000")
   private int totalSentCount;
   
   @Schema(description = "성공 건수", example = "950")
   private int successCount;
   
   @Schema(description = "실패 건수", example = "50")
   private int failCount;
   
   @Schema(description = "총 오픈 건수", example = "800")
   private int totalOpens;
   
   @Schema(description = "총 첨부파일 클릭 건수", example = "300")
   private int totalAttachmentClicks;
   
   @Schema(description = "일별 통계")
   private List<ChartData> dailyStats;
}
