package com.email.email.domain;

/**
* 이메일 상태를 정의하는 열거형입니다.
*/
public enum EmailStatus {
   QUEUED,
   PROCESSING,
   SENT,
   FAILED
}
