package com.email.stats.repository;

import com.email.stats.domain.AttachmentStats;
import com.email.stats.domain.DailyStats;
import com.email.stats.domain.DeliveryStats;
import com.email.stats.domain.OpenStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
* 통계 저장소 인터페이스입니다.
*/
public interface StatsRepository {
   
   /**
    * 발송 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @return 발송 통계 목록
    */
   List<DeliveryStats> getDeliveryStats(LocalDate startDate, LocalDate endDate);
   
   /**
    * 오픈 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param emailCategory 이메일 카테고리
    * @return 오픈 통계 목록
    */
   List<OpenStats> getOpenStats(LocalDate startDate, LocalDate endDate, String emailCategory);
   
   /**
    * 첨부파일 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param fileType 파일 유형
    * @return 첨부파일 통계 목록
    */
   List<AttachmentStats> getAttachmentStats(LocalDate startDate, LocalDate endDate, String fileType);
   
   /**
    * 일별 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @return 일별 통계 목록
    */
   List<DailyStats> getDailyStats(LocalDate startDate, LocalDate endDate);
   
   /**
    * 발송 통계를 업데이트합니다.
    *
    * @param deliveryData 발송 이벤트 데이터
    */
   void updateDeliveryStats(Map<String, Object> deliveryData);
   
   /**
    * 오픈 통계를 업데이트합니다.
    *
    * @param openData 오픈 이벤트 데이터
    */
   void updateOpenStats(Map<String, Object> openData);
   
   /**
    * 첨부파일 통계를 업데이트합니다.
    *
    * @param clickData 클릭 이벤트 데이터
    */
   void updateAttachmentStats(Map<String, Object> clickData);
}
