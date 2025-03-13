package com.email.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
* 일별 통계 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStats {
   
   private LocalDate date;
   private int sentCount;
   private int openCount;
   private int clickCount;
}
