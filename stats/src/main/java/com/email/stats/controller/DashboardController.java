package com.email.stats.controller;

import com.email.common.dto.ApiResponse;
import com.email.stats.dto.DashboardSummaryResponse;
import com.email.stats.service.StatsQueryService;
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

/**
 * 대시보드 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "통계 대시보드 API", description = "대시보드 정보 조회 API")
public class DashboardController {

    private final StatsQueryService statsQueryService;
    
    /**
     * 대시보드 요약 정보를 조회합니다.
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 대시보드 요약 정보
     */
    @GetMapping("/dashboard")
    @Operation(summary = "대시보드 정보 조회", description = "대시보드 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary(
            @Parameter(description = "시작일", example = "2023-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "종료일", example = "2023-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // 기본값: 최근 30일
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        DashboardSummaryResponse response = statsQueryService.getDashboardSummary(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
