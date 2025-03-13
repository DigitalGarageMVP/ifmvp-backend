package com.email.email.service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.email.email.domain.EmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.azure.messaging.servicebus.ServiceBusMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Azure Service Bus를 사용하여 이메일 이벤트를 발행하는 서비스 구현체
 */
@Slf4j  // 추가
@Service
public class MessagePublisherImpl implements MessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisherImpl.class);
    private final ServiceBusSenderClient serviceBusSender;
    private final ObjectMapper objectMapper;

    public MessagePublisherImpl(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.queue-name}") String queueName,
            ObjectMapper objectMapper) {
        this.serviceBusSender = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();
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

            // 문자열을 ServiceBusMessage 객체로 변환하여 전송
            ServiceBusMessage message = new ServiceBusMessage(eventJson);
            serviceBusSender.sendMessage(message);
            log.info("이메일 이벤트 발행 성공: eventType={}", event.getEventType());
        } catch (JsonProcessingException e) {
            log.error("이메일 이벤트 JSON 변환 오류", e);
            throw new RuntimeException("이메일 이벤트 발행 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("이메일 이벤트 발행 오류", e);
            throw new RuntimeException("이메일 이벤트 발행 오류: " + e.getMessage(), e);
        }
    }
}