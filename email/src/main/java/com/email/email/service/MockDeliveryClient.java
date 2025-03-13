package com.email.email.service;

import com.email.email.dto.EmailDeliveryRequest;
import com.email.email.dto.MockDeliveryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
* 목업 발송 서비스 클라이언트 클래스입니다.
*/
@Slf4j
@Component
public class MockDeliveryClient {

   private final RestTemplate restTemplate;
   private final String mockServiceUrl;
   
   public MockDeliveryClient(RestTemplate restTemplate, 
                           @Value("${mock.delivery.service-url}") String mockServiceUrl) {
       this.restTemplate = restTemplate;
       this.mockServiceUrl = mockServiceUrl;
   }
   
   /**
    * 이메일 발송을 시뮬레이션합니다.
    *
    * @param request 이메일 발송 시뮬레이션 요청 정보
    * @return 이메일 발송 시뮬레이션 응답 정보
    */
   public MockDeliveryResponse deliverEmail(EmailDeliveryRequest request) {
       log.info("이메일 발송 시뮬레이션 요청: emailId={}", request.getEmailId());
       
       String url = mockServiceUrl + "/api/mock/deliver";
       
       try {
           ResponseEntity<MockDeliveryResponse> response = 
                   restTemplate.postForEntity(url, request, MockDeliveryResponse.class);
           
           if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
               log.info("이메일 발송 시뮬레이션 성공: mockEmailId={}", response.getBody().getMockEmailId());
               return response.getBody();
           } else {
               log.error("이메일 발송 시뮬레이션 실패: statusCode={}", response.getStatusCode());
               throw new RuntimeException("이메일 발송 시뮬레이션 실패");
           }
       } catch (Exception e) {
           log.error("이메일 발송 시뮬레이션 오류", e);
           throw new RuntimeException("이메일 발송 시뮬레이션 오류: " + e.getMessage(), e);
       }
   }
}
