package com.email.common.exception;

import lombok.Getter;

/**
* 인프라 관련 예외 클래스입니다.
*/
@Getter
public class InfraException extends RuntimeException {
   
   private final ErrorCode errorCode;
   private final int code;
   
   public InfraException(ErrorCode errorCode) {
       super(errorCode.getMessage());
       this.errorCode = errorCode;
       this.code = errorCode.getCode();
   }
   
   public InfraException(ErrorCode errorCode, String message) {
       super(message);
       this.errorCode = errorCode;
       this.code = errorCode.getCode();
   }
   
   public InfraException(int code, String message) {
       super(message);
       this.errorCode = null;
       this.code = code;
   }
   
   public InfraException(int code, String message, Throwable cause) {
       super(message, cause);
       this.errorCode = null;
       this.code = code;
   }
}
