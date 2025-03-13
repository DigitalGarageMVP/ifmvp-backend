package com.email.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
* 메서드 호출 로깅을 위한 AOP 클래스입니다.
*/
@Slf4j
@Aspect
@Component
public class LoggingAspect {
   
   /**
    * 메서드 호출 전 로깅을 수행합니다.
    *
    * @param joinPoint 조인 포인트
    */
   @Before("execution(* com.email.*.service.*.*(..))")
   public void logMethodCall(JoinPoint joinPoint) {
       String className = joinPoint.getSignature().getDeclaringTypeName();
       String methodName = joinPoint.getSignature().getName();
       log.debug("Started: {}.{}", className, methodName);
   }
   
   /**
    * 메서드 정상 반환 후 로깅을 수행합니다.
    *
    * @param joinPoint 조인 포인트
    * @param result 반환값
    */
   @AfterReturning(pointcut = "execution(* com.email.*.service.*.*(..))", returning = "result")
   public void logMethodReturn(JoinPoint joinPoint, Object result) {
       String className = joinPoint.getSignature().getDeclaringTypeName();
       String methodName = joinPoint.getSignature().getName();
       log.debug("Completed: {}.{}", className, methodName);
   }
   
   /**
    * 메서드 예외 발생 시 로깅을 수행합니다.
    *
    * @param joinPoint 조인 포인트
    * @param exception 발생한 예외
    */
   @AfterThrowing(pointcut = "execution(* com.email.*.service.*.*(..))", throwing = "exception")
   public void logMethodException(JoinPoint joinPoint, Exception exception) {
       String className = joinPoint.getSignature().getDeclaringTypeName();
       String methodName = joinPoint.getSignature().getName();
       log.error("Exception in {}.{}: {}", className, methodName, exception.getMessage());
   }
}
