package com.email.common.exception;

import lombok.Getter;

/**
* 비즈니스 로직 예외 클래스입니다.
*/
@Getter
public class BusinessException extends RuntimeException {
   
   private final ErrorCode errorCode;
   
   public BusinessException(ErrorCode errorCode) {
       super(errorCode.getMessage());
       this.errorCode = errorCode;
   }
   
   public BusinessException(ErrorCode errorCode, String message) {
       super(message);
       this.errorCode = errorCode;
   }
   
   public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
       super(message, cause);
       this.errorCode = errorCode;
   }
}
