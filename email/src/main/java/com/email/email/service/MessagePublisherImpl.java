// email/src/main/java/com/email/email/service/MessagePublisherImpl.java
package com.email.email.service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.email.email.domain.EmailEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 메시지 발행 구현 클래스입니다.
 */
@Slf4j
@Component
public class MessagePublisherImpl implements MessagePublisher {

    // 필드 주입 방식으로 변경하여 생성자에서 NullPointerException 회피
    @Value("${azure.servicebus.connection-string:}")
    private String connectionString;

    @Value("${azure.servicebus.queue.email-events:email-events}")
    private String emailEventsQueue;

    private final ObjectMapper objectMapper;

    public MessagePublisherImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 이메일 이벤트를 발행합니다.
     *
     * @param event 이메일 이벤트
     */
    @Override
    public void publishEmailEvent(EmailEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);

            // 개발 환경에서는 로그만 출력
            log.info("이메일 이벤트 발행 시뮬레이션: {}", eventJson);

            // 실제 서비스에서만 ServiceBus 사용 (설정이 있는 경우)
            if (connectionString != null && !connectionString.isEmpty()) {
                try {
                    ServiceBusSenderClient serviceBusSender = new ServiceBusClientBuilder()
                            .connectionString(connectionString)
                            .sender()
                            .queueName(emailEventsQueue)
                            .buildClient();

                    ServiceBusMessage serviceBusMessage = new ServiceBusMessage(eventJson);
                    serviceBusSender.sendMessage(serviceBusMessage);
                    log.info("이메일 이벤트 발행 성공: eventType={}", event.getEventType());
                } catch (Exception e) {
                    log.error("ServiceBus 메시지 발행 실패", e);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("이메일 이벤트 JSON 변환 오류", e);
            throw new RuntimeException("이메일 이벤트 발행 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("이메일 이벤트 발행 오류", e);
            throw new RuntimeException("이메일 이벤트 발행 오류: " + e.getMessage(), e);
        }
    }
}