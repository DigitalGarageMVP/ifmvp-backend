package com.email.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
* 오픈 통계 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenStats {
   
   private LocalDate date;
   private String emailCategory;
   private int totalEmails;
   private int openCount;
}
