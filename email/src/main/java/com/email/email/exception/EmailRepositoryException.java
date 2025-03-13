package com.email.email.exception;

/**
* 이메일 저장소 예외 클래스입니다.
*/
public class EmailRepositoryException extends RuntimeException {
   
   public EmailRepositoryException(String message) {
       super(message);
   }
   
   public EmailRepositoryException(String message, Throwable cause) {
       super(message, cause);
   }
}
