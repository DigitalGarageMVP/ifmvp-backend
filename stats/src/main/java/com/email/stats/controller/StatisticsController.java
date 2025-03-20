package com.email.stats.controller;

import com.email.common.dto.ApiResponse;
import com.email.stats.dto.AttachmentClickStatisticsResponse;
import com.email.stats.dto.EmailOpenStatisticsResponse;
import com.email.stats.service.StatsQueryService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
* 통계 컨트롤러 클래스입니다.
*/
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "통계 API", description = "이메일 오픈 및 첨부파일 클릭 통계 조회 API")
public class StatisticsController {

   private final StatsQueryService statsQueryService;
   
   /**
    * 이메일 오픈 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param emailCategory 이메일 카테고리
    * @return 이메일 오픈 통계 목록
    */
   @GetMapping("/opens")
   @Timed(value = "stats.email.open.metrics",
           description = "이메일 오픈 통계 조회 시간",
           percentiles = {0.5, 0.95, 0.99})
   @Operation(summary = "이메일 오픈 통계 조회", description = "이메일 오픈 통계를 조회합니다.")
   public ResponseEntity<ApiResponse<List<EmailOpenStatisticsResponse>>> getEmailOpenStatistics(
           @Parameter(description = "시작일", example = "2023-01-01")
           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
           
           @Parameter(description = "종료일", example = "2023-12-31")
           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
           
           @Parameter(description = "이메일 카테고리", example = "MARKETING")
           @RequestParam(required = false) String emailCategory) {
       
       // 기본값: 최근 30일
       if (startDate == null) {
           startDate = LocalDate.now().minusDays(30);
       }
       
       if (endDate == null) {
           endDate = LocalDate.now();
       }
       
       List<EmailOpenStatisticsResponse> response = 
               statsQueryService.getEmailOpenStatistics(startDate, endDate, emailCategory);
       return ResponseEntity.ok(ApiResponse.success(response));
   }
   
   /**
    * 첨부파일 클릭 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param fileType 파일 유형
    * @return 첨부파일 클릭 통계 목록
    */
   @GetMapping("/attachments")
   @Timed(value = "stats.attachment.click.metrics",
           description = "첨부파일 클릭 통계 조회 시간",
           percentiles = {0.5, 0.95, 0.99})
   @Operation(summary = "첨부파일 클릭 통계 조회", description = "첨부파일 클릭 통계를 조회합니다.")
   public ResponseEntity<ApiResponse<List<AttachmentClickStatisticsResponse>>> getAttachmentClickStatistics(
           @Parameter(description = "시작일", example = "2023-01-01")
           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
           
           @Parameter(description = "종료일", example = "2023-12-31")
           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
           
           @Parameter(description = "파일 유형", example = "PDF")
           @RequestParam(required = false) String fileType) {
       
       // 기본값: 최근 30일
       if (startDate == null) {
           startDate = LocalDate.now().minusDays(30);
       }
       
       if (endDate == null) {
           endDate = LocalDate.now();
       }
       
       List<AttachmentClickStatisticsResponse> response = 
               statsQueryService.getAttachmentClickStatistics(startDate, endDate, fileType);
       return ResponseEntity.ok(ApiResponse.success(response));
   }
}
