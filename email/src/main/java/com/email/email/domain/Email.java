package com.email.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
* 이메일 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email {
   
   private String id;
   private String userId;
   private String subject;
   private String senderEmail;
   private String senderName;
   private String content;
   private LocalDateTime requestTime;
   private EmailStatus status;
}
