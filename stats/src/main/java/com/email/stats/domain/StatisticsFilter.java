package com.email.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
* 통계 필터 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsFilter {
   
   private LocalDate startDate;
   private LocalDate endDate;
   private String category;
   private String fileType;
}
