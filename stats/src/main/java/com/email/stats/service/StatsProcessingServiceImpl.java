package com.email.stats.service;

import com.email.stats.repository.StatsRepository;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
* 통계 처리 서비스 구현 클래스입니다.
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsProcessingServiceImpl implements StatsProcessingService {

   private final StatsRepository statsRepository;
    private final Counter emailOpenEventCounter;
    private final Counter attachmentClickEventCounter;
   
   /**
    * 이메일 발송 이벤트를 처리합니다.
    *
    * @param event 이벤트 데이터
    */
   @Override
   @Transactional
   public void processEmailDeliveryEvent(Map<String, Object> event) {
       log.info("이메일 발송 이벤트 처리: mockEmailId={}", event.get("mockEmailId"));

       try {
           statsRepository.updateDeliveryStats(event);
           log.info("이메일 발송 통계 업데이트 완료");
       } catch (Exception e) {
           log.error("이메일 발송 통계 업데이트 오류", e);
           throw new RuntimeException("이메일 발송 통계 업데이트 오류: " + e.getMessage(), e);
       }
   }
   
   /**
    * 이메일 오픈 이벤트를 처리합니다.
    *
    * @param event 이벤트 데이터
    */
   @Override
   @Transactional
   public void processEmailOpenEvent(Map<String, Object> event) {
       log.info("이메일 오픈 이벤트 처리: eventId={}", event.get("eventId"));

       emailOpenEventCounter.increment();

       try {
           statsRepository.updateOpenStats(event);
           log.info("이메일 오픈 통계 업데이트 완료");
       } catch (Exception e) {
           log.error("이메일 오픈 통계 업데이트 오류", e);
           throw new RuntimeException("이메일 오픈 통계 업데이트 오류: " + e.getMessage(), e);
       }
   }
   
   /**
    * 첨부파일 클릭 이벤트를 처리합니다.
    *
    * @param event 이벤트 데이터
    */
   @Override
   @Transactional
   public void processAttachmentClickEvent(Map<String, Object> event) {
       log.info("첨부파일 클릭 이벤트 처리: eventId={}", event.get("eventId"));

       attachmentClickEventCounter.increment();

       try {
           statsRepository.updateAttachmentStats(event);
           log.info("첨부파일 클릭 통계 업데이트 완료");
       } catch (Exception e) {
           log.error("첨부파일 클릭 통계 업데이트 오류", e);
           throw new RuntimeException("첨부파일 클릭 통계 업데이트 오류: " + e.getMessage(), e);
       }
   }
}
