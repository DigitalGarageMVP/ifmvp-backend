// ifmvp-back\email\src\main\java\com\email\email\service\MockDeliveryClient.java
package com.email.email.service;

import com.email.common.dto.ApiResponse;
import com.email.email.dto.DeliveryResult;
import com.email.email.dto.EmailDeliveryRequest;
import com.email.email.dto.MockDeliveryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 목업 발송 서비스와 통신하는 클라이언트 클래스입니다.
 */
@Slf4j
@Component
public class MockDeliveryClient {

    private final RestTemplate restTemplate;
    private final String mockServiceUrl;

    public MockDeliveryClient(
            RestTemplate restTemplate,
            @org.springframework.beans.factory.annotation.Value("${mock.delivery.service-url:http://localhost:8082}") String mockServiceUrl) {
        this.restTemplate = restTemplate;
        this.mockServiceUrl = mockServiceUrl;
        log.info("MockDeliveryClient 초기화: URL={}", mockServiceUrl);
    }

    /**
     * 이메일 발송 요청을 목업 서비스에 전달합니다.
     *
     * @param request 이메일 발송 요청 정보
     * @return 발송 결과 응답
     */
    public MockDeliveryResponse deliverEmail(EmailDeliveryRequest request) {
        String url = mockServiceUrl + "/api/mock/deliver";
        log.info("이메일 발송 요청: URL={}, 요청={}", url, request);

        try {
            // ApiResponse 타입으로 응답을 받도록 수정
            ResponseEntity<ApiResponse<MockDeliveryResponse>> responseEntity =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            new HttpEntity<>(request),
                            new ParameterizedTypeReference<ApiResponse<MockDeliveryResponse>>() {}
                    );

            // ApiResponse에서 실제 데이터 추출
            ApiResponse<MockDeliveryResponse> apiResponse = responseEntity.getBody();
            MockDeliveryResponse response = apiResponse != null ? apiResponse.getData() : null;

            log.info("이메일 발송 응답: 상태 코드={}, 원본 응답={}, 추출 응답={}",
                    responseEntity.getStatusCode(), apiResponse, response);

            // null 체크 및 기본값 설정
            if (response == null) {
                log.warn("목업 서비스에서 null 응답이 반환됨");
                return createDefaultResponse();
            }

            // 상태값이 null인 경우 기본값 설정
            if (response.getDeliveryStatus() == null) {
                log.warn("목업 서비스에서 상태값이 null로 반환됨");
                return MockDeliveryResponse.builder()
                        .success(response.isSuccess())
                        .mockEmailId(response.getMockEmailId() != null ? response.getMockEmailId() : UUID.randomUUID().toString())
                        .deliveryStatus("UNKNOWN")  // 기본값 설정
                        .results(response.getResults())
                        .build();
            }

            return response;
        } catch (Exception e) {
            log.error("이메일 발송 요청 처리 중 오류: {}", e.getMessage(), e);
            return createDefaultResponse();
        }
    }

    /**
     * 기본 응답을 생성합니다.
     *
     * @return 기본 MockDeliveryResponse 객체
     */
    private MockDeliveryResponse createDefaultResponse() {
        // 간단한 더미 결과 생성
        List<DeliveryResult> dummyResults = Collections.singletonList(
                DeliveryResult.builder()
                        .recipientEmail("unknown@example.com")
                        .status("ERROR")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build()
        );

        return MockDeliveryResponse.builder()
                .success(false)
                .mockEmailId(UUID.randomUUID().toString())
                .deliveryStatus("ERROR")
                .results(dummyResults)
                .build();
    }
}