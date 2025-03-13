package com.email.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 첨부파일 클릭 통계 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "첨부파일 클릭 통계 응답")
public class AttachmentClickStatisticsResponse {
   
   @Schema(description = "날짜", example = "2023-06-01")
   private String date;
   
   @Schema(description = "파일 유형", example = "PDF")
   private String fileType;
   
   @Schema(description = "총 첨부파일 수", example = "100")
   private int totalAttachments;
   
   @Schema(description = "클릭 건수", example = "30")
   private int clickCount;
   
   @Schema(description = "클릭률", example = "30.0")
   private double clickRate;
}
