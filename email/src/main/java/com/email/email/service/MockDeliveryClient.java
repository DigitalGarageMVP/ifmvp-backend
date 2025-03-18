package com.email.email.service;

import com.email.email.dto.EmailDeliveryRequest;
import com.email.email.dto.MockDeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 목업 발송 서비스와 통신하는 클라이언트 클래스입니다.
 */
@Component
public class MockDeliveryClient {

    private static final Logger logger = LoggerFactory.getLogger(MockDeliveryClient.class);

    private final RestTemplate restTemplate;
    private final String mockServiceUrl;

    public MockDeliveryClient(
            RestTemplate restTemplate,
            @Value("${mock.delivery.service.url:http://localhost:8082}") String mockServiceUrl) {
        this.restTemplate = restTemplate;
        this.mockServiceUrl = mockServiceUrl;
        logger.info("MockDeliveryClient 초기화: URL={}", mockServiceUrl);
    }

    /**
     * 이메일 발송 요청을 목업 서비스에 전달합니다.
     *
     * @param request 이메일 발송 요청 정보
     * @return 발송 결과 응답
     */
    public MockDeliveryResponse deliverEmail(EmailDeliveryRequest request) {
        String url = mockServiceUrl + "/api/mock/deliver";
        logger.info("이메일 발송 요청: URL={}, 요청={}", url, request);

        ResponseEntity<MockDeliveryResponse> responseEntity =
                restTemplate.postForEntity(url, request, MockDeliveryResponse.class);

        MockDeliveryResponse response = responseEntity.getBody();
        logger.info("이메일 발송 응답: 상태 코드={}, 응답={}",
                responseEntity.getStatusCode(), response);

        return response;
    }
}