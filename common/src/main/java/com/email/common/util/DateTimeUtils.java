package com.email.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
* 날짜/시간 유틸리티 클래스입니다.
*/
public class DateTimeUtils {
   
   private DateTimeUtils() {
       throw new IllegalStateException("Utility class");
   }
   
   /**
    * LocalDateTime을 지정된 패턴의 문자열로 포맷합니다.
    *
    * @param dateTime 날짜/시간
    * @param pattern 패턴
    * @return 포맷된 문자열
    */
   public static String format(LocalDateTime dateTime, String pattern) {
       if (dateTime == null) {
           return null;
       }
       return dateTime.format(DateTimeFormatter.ofPattern(pattern));
   }
   
   /**
    * 문자열을 지정된 패턴으로 파싱하여 LocalDateTime으로 변환합니다.
    *
    * @param dateTimeString 날짜/시간 문자열
    * @param pattern 패턴
    * @return 변환된 LocalDateTime
    */
   public static LocalDateTime parse(String dateTimeString, String pattern) {
       if (dateTimeString == null || dateTimeString.isEmpty()) {
           return null;
       }
       return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern));
   }
   
   /**
    * 현재 날짜/시간을 반환합니다.
    *
    * @return 현재 LocalDateTime
    */
   public static LocalDateTime getCurrentDateTime() {
       return LocalDateTime.now();
   }
   
   /**
    * 두 날짜 사이의 일수를 계산합니다.
    *
    * @param start 시작일
    * @param end 종료일
    * @return 일수
    */
   public static long getDaysBetween(LocalDate start, LocalDate end) {
       return ChronoUnit.DAYS.between(start, end);
   }
   
   /**
    * 해당 날짜의 시작 시간을 반환합니다.
    *
    * @param date 날짜
    * @return 해당 날짜의 00:00:00
    */
   public static LocalDateTime startOfDay(LocalDate date) {
       return date.atStartOfDay();
   }
   
   /**
    * 해당 날짜의 마지막 시간을 반환합니다.
    *
    * @param date 날짜
    * @return 해당 날짜의 23:59:59.999999999
    */
   public static LocalDateTime endOfDay(LocalDate date) {
       return date.plusDays(1).atStartOfDay().minusNanos(1);
   }
}
