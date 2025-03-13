package com.email.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
* 발송 통계 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryStats {
   
   private LocalDate date;
   private int totalCount;
   private int successCount;
   private int failCount;
}
