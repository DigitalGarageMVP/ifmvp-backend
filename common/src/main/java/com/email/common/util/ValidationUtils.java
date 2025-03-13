package com.email.common.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
* 유효성 검증 유틸리티 클래스입니다.
*/
public class ValidationUtils {
   
   private static final Pattern EMAIL_PATTERN = 
           Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
   
   private ValidationUtils() {
       throw new IllegalStateException("Utility class");
   }
   
   /**
    * 이메일 형식을 검증합니다.
    *
    * @param email 이메일
    * @return 유효성 여부
    */
   public static boolean validateEmail(String email) {
       if (email == null || email.isEmpty()) {
           return false;
       }
       return EMAIL_PATTERN.matcher(email).matches();
   }
   
   /**
    * 문자열이 비어있지 않은지 검증합니다.
    *
    * @param value 문자열
    * @return 유효성 여부
    */
   public static boolean validateNotEmpty(String value) {
       return value != null && !value.trim().isEmpty();
   }
   
   /**
    * 문자열의 최대 길이를 검증합니다.
    *
    * @param value 문자열
    * @param maxLength 최대 길이
    * @return 유효성 여부
    */
   public static boolean validateMaxLength(String value, int maxLength) {
       if (value == null) {
           return true;
       }
       return value.length() <= maxLength;
   }
   
   /**
    * 날짜 범위를 검증합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param maxDays 최대 일수
    * @return 유효성 여부
    */
   public static boolean validateDateRange(LocalDate startDate, LocalDate endDate, int maxDays) {
       if (startDate == null || endDate == null) {
           return false;
       }
       
       if (startDate.isAfter(endDate)) {
           return false;
       }
       
       long days = DateTimeUtils.getDaysBetween(startDate, endDate);
       return days <= maxDays;
   }
}
