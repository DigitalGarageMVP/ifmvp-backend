package com.email.stats.service;

import com.email.stats.dto.AttachmentClickStatisticsResponse;
import com.email.stats.dto.DashboardSummaryResponse;
import com.email.stats.dto.EmailOpenStatisticsResponse;

import java.time.LocalDate;
import java.util.List;

/**
* 통계 쿼리 서비스 인터페이스입니다.
*/
public interface StatsQueryService {
   
   /**
    * 대시보드 요약 정보를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @return 대시보드 요약 정보
    */
   DashboardSummaryResponse getDashboardSummary(LocalDate startDate, LocalDate endDate);
   
   /**
    * 이메일 오픈 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param emailCategory 이메일 카테고리
    * @return 이메일 오픈 통계 목록
    */
   List<EmailOpenStatisticsResponse> getEmailOpenStatistics(
           LocalDate startDate, LocalDate endDate, String emailCategory);
   
   /**
    * 첨부파일 클릭 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param fileType 파일 유형
    * @return 첨부파일 클릭 통계 목록
    */
   List<AttachmentClickStatisticsResponse> getAttachmentClickStatistics(
           LocalDate startDate, LocalDate endDate, String fileType);
}
