package com.email.stats.config;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.email.stats.service.StatsProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
* 이벤트 처리기 클래스입니다.
*/
@Slf4j
@Component
@RequiredArgsConstructor
public class EventProcessor {

   private final StatsProcessingService statsProcessingService;
   private final ObjectMapper objectMapper;
   
   /**
    * 이메일 발송 이벤트를 처리합니다.
    *
    * @param context 메시지 컨텍스트
    */
   public void processEmailDeliveryEvent(ServiceBusReceivedMessageContext context) {
       ServiceBusReceivedMessage message = context.getMessage();
       
       try {
           String messageBody = message.getBody().toString();
           log.info("이메일 발송 이벤트 수신: {}", messageBody);
           
           Map<String, Object> event = objectMapper.readValue(messageBody, Map.class);
           statsProcessingService.processEmailDeliveryEvent(event);
       } catch (Exception e) {
           log.error("이메일 발송 이벤트 처리 오류", e);
       }
   }
   
   /**
    * 이메일 오픈 이벤트를 처리합니다.
    *
    * @param context 메시지 컨텍스트
    */
   public void processEmailOpenEvent(ServiceBusReceivedMessageContext context) {
       ServiceBusReceivedMessage message = context.getMessage();
       
       try {
           String messageBody = message.getBody().toString();
           log.info("이메일 오픈 이벤트 수신: {}", messageBody);
           
           Map<String, Object> event = objectMapper.readValue(messageBody, Map.class);
           statsProcessingService.processEmailOpenEvent(event);
       } catch (Exception e) {
           log.error("이메일 오픈 이벤트 처리 오류", e);
       }
   }
   
   /**
    * 첨부파일 클릭 이벤트를 처리합니다.
    *
    * @param context 메시지 컨텍스트
    */
   public void processAttachmentClickEvent(ServiceBusReceivedMessageContext context) {
       ServiceBusReceivedMessage message = context.getMessage();
       
       try {
           String messageBody = message.getBody().toString();
           log.info("첨부파일 클릭 이벤트 수신: {}", messageBody);
           
           Map<String, Object> event = objectMapper.readValue(messageBody, Map.class);
           statsProcessingService.processAttachmentClickEvent(event);
       } catch (Exception e) {
           log.error("첨부파일 클릭 이벤트 처리 오류", e);
       }
   }
}
