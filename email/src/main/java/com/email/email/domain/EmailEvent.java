package com.email.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
* 이메일 이벤트 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailEvent {
   
   private String eventType;
   private Map<String, Object> emailData;
}
