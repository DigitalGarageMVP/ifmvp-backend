package com.email.common.exception;

import com.email.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
* 전역 예외 핸들러 클래스입니다.
*/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
   
   /**
    * 비즈니스 예외를 처리합니다.
    *
    * @param e 비즈니스 예외
    * @return API 응답
    */
   @ExceptionHandler(BusinessException.class)
   public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
       log.error("Business exception: {}", e.getMessage());
       return ResponseEntity
               .status(e.getErrorCode().getCode())
               .body(ApiResponse.error(e.getErrorCode().getCode(), e.getMessage()));
   }
   
   /**
    * 인프라 예외를 처리합니다.
    *
    * @param e 인프라 예외
    * @return API 응답
    */
   @ExceptionHandler(InfraException.class)
   public ResponseEntity<ApiResponse<Void>> handleInfraException(InfraException e) {
       log.error("Infrastructure exception: {}", e.getMessage());
       return ResponseEntity
               .status(e.getCode())
               .body(ApiResponse.error(e.getCode(), e.getMessage()));
   }
   
   /**
    * 유효성 검증 예외를 처리합니다.
    *
    * @param e 유효성 검증 예외
    * @return API 응답
    */
   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
       log.error("Validation exception: {}", e.getMessage());
       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), 
                       "입력값 검증에 실패했습니다: " + e.getBindingResult().getFieldError().getDefaultMessage()));
   }
   
   /**
    * 제약 조건 위반 예외를 처리합니다.
    *
    * @param e 제약 조건 위반 예외
    * @return API 응답
    */
   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
       log.error("Constraint violation exception: {}", e.getMessage());
       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), 
                       "입력값 검증에 실패했습니다: " + e.getMessage()));
   }
   
   /**
    * 메서드 인자 타입 불일치 예외를 처리합니다.
    *
    * @param e 메서드 인자 타입 불일치 예외
    * @return API 응답
    */
   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
           MethodArgumentTypeMismatchException e) {
       log.error("Type mismatch exception: {}", e.getMessage());
       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), 
                       "잘못된 파라미터 형식입니다: " + e.getName()));
   }
   
   /**
    * 일반 예외를 처리합니다.
    *
    * @param e 일반 예외
    * @return API 응답
    */
   @ExceptionHandler(Exception.class)
   public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
       log.error("Unhandled exception: {}", e.getMessage(), e);
       return ResponseEntity
               .status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                       "서버 내부 오류가 발생했습니다."));
   }
}
