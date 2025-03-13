package com.email.stats.exception;

/**
* 통계 저장소 예외 클래스입니다.
*/
public class StatsRepositoryException extends RuntimeException {
   
   public StatsRepositoryException(String message) {
       super(message);
   }
   
   public StatsRepositoryException(String message, Throwable cause) {
       super(message, cause);
   }
}
