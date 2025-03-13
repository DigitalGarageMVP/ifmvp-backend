package com.email.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 차트 데이터 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "차트 데이터")
public class ChartData {
   
   @Schema(description = "날짜", example = "2023-06-01")
   private String date;
   
   @Schema(description = "발송 건수", example = "100")
   private int sentCount;
   
   @Schema(description = "오픈 건수", example = "80")
   private int openCount;
   
   @Schema(description = "클릭 건수", example = "30")
   private int clickCount;
}
