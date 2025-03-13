package com.email.email.controller;

import com.email.common.dto.ApiResponse;
import com.email.email.dto.EmailHistoryResponse;
import com.email.email.dto.EmailSendRequest;
import com.email.email.dto.EmailSendResponse;
import com.email.email.dto.RecentEmailListResponse;
import com.email.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
* 이메일 API 컨트롤러입니다.
*/
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Tag(name = "이메일 API", description = "이메일 발송 및 조회 API")
public class EmailController {

   private final EmailService emailService;
   
   /**
    * 최근 발송 이메일 목록을 조회합니다.
    *
    * @param userId 사용자 ID
    * @return 최근 발송 이메일 목록
    */
   @GetMapping("/recent")
   @Operation(summary = "최근 발송 이메일 목록 조회", description = "최근 발송한 이메일 목록을 조회합니다.")
   public ResponseEntity<ApiResponse<List<RecentEmailListResponse>>> getRecentEmails(
           @Parameter(description = "사용자 ID", example = "user01")
           @RequestParam String userId) {
       List<RecentEmailListResponse> response = emailService.getRecentEmails(userId);
       return ResponseEntity.ok(ApiResponse.success(response));
   }
   
   /**
    * 이메일 발송 요청을 처리합니다.
    *
    * @param request 이메일 발송 요청 정보
    * @return 이메일 발송 응답 정보
    */
   @PostMapping("/send")
   @Operation(summary = "이메일 발송", description = "이메일을 발송합니다.")
   public ResponseEntity<ApiResponse<EmailSendResponse>> sendEmail(
           @Valid @RequestBody EmailSendRequest request) {
       EmailSendResponse response = emailService.sendEmail(request);
       return ResponseEntity.ok(ApiResponse.success(response));
   }
   
   /**
    * 이메일 발송 이력을 상세 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param senderEmail 발신자 이메일
    * @param status 발송 상태
    * @param recipientEmail 수신자 이메일
    * @return 이메일 발송 이력 목록
    */
   @GetMapping("/history")
   @Operation(summary = "이메일 발송 이력 상세 조회", description = "이메일 발송 이력을 상세 조회합니다.")
   public ResponseEntity<ApiResponse<List<EmailHistoryResponse>>> getEmailHistory(
           @Parameter(description = "시작일", example = "2023-01-01")
           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
           
           @Parameter(description = "종료일", example = "2023-12-31")
           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
           
           @Parameter(description = "발신자 이메일", example = "sender@example.com")
           @RequestParam(required = false) String senderEmail,
           
           @Parameter(description = "발송 상태", example = "SUCCESS")
           @RequestParam(required = false) String status,
           
           @Parameter(description = "수신자 이메일", example = "recipient@example.com")
           @RequestParam(required = false) String recipientEmail) {
       
       List<EmailHistoryResponse> response = emailService.getEmailHistory(
               startDate, endDate, senderEmail, status, recipientEmail);
       return ResponseEntity.ok(ApiResponse.success(response));
   }
}
