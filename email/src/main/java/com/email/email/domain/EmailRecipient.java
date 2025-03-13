package com.email.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
* 이메일 수신자 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRecipient {
   
   private String id;
   private String emailId;
   private String recipientEmail;
   private RecipientStatus status;
   private LocalDateTime receiveTime;
   private String failReason;
}
