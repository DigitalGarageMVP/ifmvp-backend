package com.email.email.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
* Azure Service Bus 설정 클래스입니다.
*/
@Configuration
public class ServiceBusConfig {

   @Value("${azure.servicebus.connection-string}")
   private String connectionString;
   
   @Value("${azure.servicebus.queue.email-events}")
   private String emailEventsQueue;
   
   /**
    * Service Bus 클라이언트를 생성합니다.
    *
    * @return Service Bus 발신자 클라이언트
    */
   @Bean
   public ServiceBusSenderClient serviceBusSender() {
       return new ServiceBusClientBuilder()
               .connectionString(connectionString)
               .sender()
               .queueName(emailEventsQueue)
               .buildClient();
   }
   
   /**
    * RestTemplate을 생성합니다.
    *
    * @return RestTemplate
    */
   @Bean
   public RestTemplate restTemplate() {
       return new RestTemplate();
   }
   
   /**
    * ObjectMapper를 생성합니다.
    *
    * @return ObjectMapper
    */
   @Bean
   public ObjectMapper objectMapper() {
       return new ObjectMapper();
   }
}
