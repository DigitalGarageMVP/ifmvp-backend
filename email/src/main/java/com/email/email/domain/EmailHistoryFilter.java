package com.email.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
* 이메일 발송 이력 필터 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailHistoryFilter {
   
   private LocalDate startDate;
   private LocalDate endDate;
   private String senderEmail;
   private String status;
   private String recipientEmail;
}
