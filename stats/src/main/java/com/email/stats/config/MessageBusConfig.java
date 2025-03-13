package com.email.stats.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* Azure Service Bus 설정 클래스입니다.
*/
@Configuration
public class MessageBusConfig {

   @Value("${azure.servicebus.connection-string}")
   private String connectionString;
   
   @Value("${azure.servicebus.queue.email-events}")
   private String emailEventsQueue;
   
   @Value("${azure.servicebus.queue.email-open-events}")
   private String emailOpenEventsQueue;
   
   @Value("${azure.servicebus.queue.attachment-click-events}")
   private String attachmentClickEventsQueue;
   
   /**
    * ObjectMapper 빈을 생성합니다.
    *
    * @return ObjectMapper
    */
   @Bean
   public ObjectMapper objectMapper() {
       return new ObjectMapper();
   }
   
   /**
    * 이메일 이벤트 프로세서를 생성합니다.
    *
    * @param eventProcessor 이벤트 처리기
    * @return 이메일 이벤트 프로세서
    */
   @Bean
   public ServiceBusProcessorClient emailEventsProcessor(EventProcessor eventProcessor) {
       ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
               .connectionString(connectionString)
               .processor()
               .queueName(emailEventsQueue)
               .processMessage(eventProcessor::processEmailDeliveryEvent)
               .processError(context -> {
                   System.err.println("Error occurred in email events processor: " + context.getException());
               })
               .buildProcessorClient();
       
       processorClient.start();
       return processorClient;
   }
   
   /**
    * 이메일 오픈 이벤트 프로세서를 생성합니다.
    *
    * @param eventProcessor 이벤트 처리기
    * @return 이메일 오픈 이벤트 프로세서
    */
   @Bean
   public ServiceBusProcessorClient emailOpenEventsProcessor(EventProcessor eventProcessor) {
       ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
               .connectionString(connectionString)
               .processor()
               .queueName(emailOpenEventsQueue)
               .processMessage(eventProcessor::processEmailOpenEvent)
               .processError(context -> {
                   System.err.println("Error occurred in email open events processor: " + context.getException());
               })
               .buildProcessorClient();
       
       processorClient.start();
       return processorClient;
   }
   
   /**
    * 첨부파일 클릭 이벤트 프로세서를 생성합니다.
    *
    * @param eventProcessor 이벤트 처리기
    * @return 첨부파일 클릭 이벤트 프로세서
    */
   @Bean
   public ServiceBusProcessorClient attachmentClickEventsProcessor(EventProcessor eventProcessor) {
       ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
               .connectionString(connectionString)
               .processor()
               .queueName(attachmentClickEventsQueue)
               .processMessage(eventProcessor::processAttachmentClickEvent)
               .processError(context -> {
                   System.err.println("Error occurred in attachment click events processor: " + context.getException());
               })
               .buildProcessorClient();
       
       processorClient.start();
       return processorClient;
   }
}
