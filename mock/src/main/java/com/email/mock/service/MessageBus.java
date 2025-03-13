package com.email.mock.service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.azure.messaging.servicebus.ServiceBusMessage;

import java.util.Map;

/**
* 메시지 버스 클래스입니다.
*/
@Slf4j
@Component
public class MessageBus {

   private final String connectionString;
   private final String emailEventsQueue;
   private final String emailOpenEventsQueue;
   private final String attachmentClickEventsQueue;
   private final ObjectMapper objectMapper;
   
   public MessageBus(
           @Value("${azure.servicebus.connection-string}") String connectionString,
           @Value("${azure.servicebus.queue.email-events}") String emailEventsQueue,
           @Value("${azure.servicebus.queue.email-open-events}") String emailOpenEventsQueue,
           @Value("${azure.servicebus.queue.attachment-click-events}") String attachmentClickEventsQueue) {
       this.connectionString = connectionString;
       this.emailEventsQueue = emailEventsQueue;
       this.emailOpenEventsQueue = emailOpenEventsQueue;
       this.attachmentClickEventsQueue = attachmentClickEventsQueue;
       this.objectMapper = new ObjectMapper();
   }
   
   /**
    * 이메일 발송 이벤트를 발행합니다.
    *
    * @param event 이벤트 데이터
    */
   public void publishEmailDeliveryEvent(Map<String, Object> event) {
       publish(emailEventsQueue, event);
   }
   
   /**
    * 이메일 오픈 이벤트를 발행합니다.
    *
    * @param event 이벤트 데이터
    */
   public void publishEmailOpenEvent(Map<String, Object> event) {
       publish(emailOpenEventsQueue, event);
   }
   
   /**
    * 첨부파일 클릭 이벤트를 발행합니다.
    *
    * @param event 이벤트 데이터
    */
   public void publishAttachmentClickEvent(Map<String, Object> event) {
       publish(attachmentClickEventsQueue, event);
   }
   

    /**
     * 지정된 큐에 이벤트를 발행합니다.
     *
     * @param queueName 큐 이름
     * @param event 이벤트 데이터
     */
    private void publish(String queueName, Map<String, Object> event) {
        try {
            ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                    .connectionString(connectionString)
                    .sender()
                    .queueName(queueName)
                    .buildClient();

            String eventJson = objectMapper.writeValueAsString(event);

            // 문자열을 ServiceBusMessage 객체로 변환하여 전송
            ServiceBusMessage message = new ServiceBusMessage(eventJson);
            sender.sendMessage(message);

            sender.close();

            log.info("이벤트 발행 성공: queue={}, eventType={}", queueName, event.get("eventType"));
        } catch (Exception e) {
            log.error("이벤트 발행 오류: queue={}", queueName, e);
            throw new RuntimeException("이벤트 발행 오류: " + e.getMessage(), e);
        }
    }
}
