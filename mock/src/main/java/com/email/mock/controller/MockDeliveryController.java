package com.email.mock.controller;

import com.email.common.dto.ApiResponse;
import com.email.mock.dto.*;
import com.email.mock.service.MessageBus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
* 목업 발송 컨트롤러 클래스입니다.
*/
@Slf4j
@RestController
@RequestMapping("/api/mock")
@RequiredArgsConstructor
@Tag(name = "목업 발송 API", description = "이메일 발송 시뮬레이션 및 이벤트 발행 API")
public class MockDeliveryController {

   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private final MessageBus messageBus;
   
   /**
    * 이메일 발송을 시뮬레이션합니다.
    *
    * @param request 이메일 발송 시뮬레이션 요청 정보
    * @return 이메일 발송 시뮬레이션 응답 정보
    */
   @PostMapping("/deliver")
   @Operation(summary = "이메일 발송 시뮬레이션", description = "이메일 발송을 시뮬레이션합니다.")
   public ResponseEntity<ApiResponse<MockDeliveryResponse>> deliverEmail(
           @Valid @RequestBody EmailDeliveryRequest request) {

       log.info("이메일 발송 시뮬레이션 요청: emailId={}", request.getEmailId());

       String mockEmailId = generateEventId();
       List<DeliveryResult> results = simulateDeliveryStatus(request.getRecipientEmails());

       // 발송 상태 결정
       String deliveryStatus = results.stream().allMatch(r -> "FAILED".equals(r.getStatus()))
               ? "FAILED"
               : (results.stream().anyMatch(r -> "FAILED".equals(r.getStatus()))
               ? "PARTIALLY_DELIVERED"
               : "DELIVERED");

       // 이메일 발송 이벤트 발행
       publishEmailDeliveryEvent(mockEmailId, request, results);

       MockDeliveryResponse response = MockDeliveryResponse.builder()
               .success(!"FAILED".equals(deliveryStatus))
               .mockEmailId(mockEmailId)
               .deliveryStatus(deliveryStatus)
               .results(results)
               .build();

       log.info("이메일 발송 시뮬레이션 완료: mockEmailId={}, status={}", mockEmailId, deliveryStatus);

       return ResponseEntity.ok(ApiResponse.success(response));
   }
   
   /**
    * 이메일 오픈 이벤트를 발행합니다.
    *
    * @param request 이메일 오픈 요청 정보
    * @return 이벤트 응답 정보
    */
   @PostMapping("/open")
   @Operation(summary = "이메일 오픈 이벤트 발행", description = "이메일 오픈 이벤트를 발행합니다.")
   public ResponseEntity<ApiResponse<EventResponse>> trackEmailOpen(
           @Valid @RequestBody EmailOpenRequest request) {
       
       log.info("이메일 오픈 이벤트 요청: emailId={}, recipientEmail={}", 
               request.getEmailId(), request.getRecipientEmail());
       
       String eventId = publishEmailOpenEvent(request);
       
       EventResponse response = EventResponse.builder()
               .success(true)
               .eventId(eventId)
               .eventType("EMAIL_OPENED")
               .build();
       
       log.info("이메일 오픈 이벤트 발행 완료: eventId={}", eventId);
       
       return ResponseEntity.ok(ApiResponse.success(response));
   }
   
   /**
    * 첨부파일 클릭 이벤트를 발행합니다.
    *
    * @param request 첨부파일 클릭 요청 정보
    * @return 이벤트 응답 정보
    */
   @PostMapping("/click")
   @Operation(summary = "첨부파일 클릭 이벤트 발행", description = "첨부파일 클릭 이벤트를 발행합니다.")
   public ResponseEntity<ApiResponse<EventResponse>> trackAttachmentClick(
           @Valid @RequestBody AttachmentClickRequest request) {
       
       log.info("첨부파일 클릭 이벤트 요청: emailId={}, attachmentId={}, recipientEmail={}", 
               request.getEmailId(), request.getAttachmentId(), request.getRecipientEmail());
       
       String eventId = publishAttachmentClickEvent(request);
       
       EventResponse response = EventResponse.builder()
               .success(true)
               .eventId(eventId)
               .eventType("ATTACHMENT_CLICKED")
               .build();
       
       log.info("첨부파일 클릭 이벤트 발행 완료: eventId={}", eventId);
       
       return ResponseEntity.ok(ApiResponse.success(response));
   }
   
   /**
    * 고유한 이벤트 ID를 생성합니다.
    *
    * @return 생성된 이벤트 ID
    */
   private String generateEventId() {
       return UUID.randomUUID().toString();
   }
   
   /**
    * 수신자 목록에 대한 발송 상태를 시뮬레이션합니다.
    *
    * @param recipientEmails 수신자 이메일 목록
    * @return 발송 결과 목록
    */
   private List<DeliveryResult> simulateDeliveryStatus(List<String> recipientEmails) {
       Random random = new Random();
       LocalDateTime now = LocalDateTime.now();

       // 상태 문자열 목록 명시적 정의
       String[] statusOptions = {"DELIVERED", "FAILED"};

       return recipientEmails.stream()
               .map(email -> {
                   // 90% 확률로 성공, 10% 확률로 실패 (더 명시적으로 구현)
                   String status = random.nextDouble() < 0.9 ? statusOptions[0] : statusOptions[1];

                   // 로그 추가
                   log.debug("생성된 상태 값: {}, 수신자: {}", status, email);

                   return DeliveryResult.builder()
                           .recipientEmail(email)
                           .status(status)
                           .timestamp(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                           .build();
               })
               .collect(Collectors.toList());
   }
   
   /**
    * 이메일 발송 이벤트를 발행합니다.
    *
    * @param mockEmailId 모의 이메일 ID
    * @param request 이메일 발송 시뮬레이션 요청 정보
    * @param results 발송 결과 목록
    */
   private void publishEmailDeliveryEvent(String mockEmailId, EmailDeliveryRequest request, List<DeliveryResult> results) {
       Map<String, Object> eventData = new HashMap<>();
       eventData.put("mockEmailId", mockEmailId);
       eventData.put("emailId", request.getEmailId());
       eventData.put("senderEmail", request.getSenderEmail());
       eventData.put("subject", request.getSubject());
       eventData.put("recipientEmails", request.getRecipientEmails());
       eventData.put("results", results);
       eventData.put("timestamp", LocalDateTime.now().format(DATE_TIME_FORMATTER));
       
       messageBus.publishEmailDeliveryEvent(eventData);
   }
   
   /**
    * 이메일 오픈 이벤트를 발행합니다.
    *
    * @param request 이메일 오픈 요청 정보
    * @return 생성된 이벤트 ID
    */
   private String publishEmailOpenEvent(EmailOpenRequest request) {
       String eventId = generateEventId();
       
       Map<String, Object> eventData = new HashMap<>();
       eventData.put("eventId", eventId);
       eventData.put("emailId", request.getEmailId());
       eventData.put("recipientEmail", request.getRecipientEmail());
       eventData.put("openTime", request.getOpenTime());
       eventData.put("deviceInfo", request.getDeviceInfo());
       eventData.put("timestamp", LocalDateTime.now().format(DATE_TIME_FORMATTER));
       
       messageBus.publishEmailOpenEvent(eventData);
       
       return eventId;
   }
   
   /**
    * 첨부파일 클릭 이벤트를 발행합니다.
    *
    * @param request 첨부파일 클릭 요청 정보
    * @return 생성된 이벤트 ID
    */
   private String publishAttachmentClickEvent(AttachmentClickRequest request) {
       String eventId = generateEventId();
       
       Map<String, Object> eventData = new HashMap<>();
       eventData.put("eventId", eventId);
       eventData.put("emailId", request.getEmailId());
       eventData.put("attachmentId", request.getAttachmentId());
       eventData.put("recipientEmail", request.getRecipientEmail());
       eventData.put("clickTime", request.getClickTime());
       eventData.put("deviceInfo", request.getDeviceInfo());
       eventData.put("timestamp", LocalDateTime.now().format(DATE_TIME_FORMATTER));
       
       messageBus.publishAttachmentClickEvent(eventData);
       
       return eventId;
   }
}
