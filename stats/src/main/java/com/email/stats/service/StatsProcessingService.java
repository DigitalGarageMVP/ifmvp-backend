package com.email.stats.service;

import java.util.Map;

/**
* 통계 처리 서비스 인터페이스입니다.
*/
public interface StatsProcessingService {
   
   /**
    * 이메일 발송 이벤트를 처리합니다.
    *
    * @param event 이벤트 데이터
    */
   void processEmailDeliveryEvent(Map<String, Object> event);
   
   /**
    * 이메일 오픈 이벤트를 처리합니다.
    *
    * @param event 이벤트 데이터
    */
   void processEmailOpenEvent(Map<String, Object> event);
   
   /**
    * 첨부파일 클릭 이벤트를 처리합니다.
    *
    * @param event 이벤트 데이터
    */
   void processAttachmentClickEvent(Map<String, Object> event);
}
