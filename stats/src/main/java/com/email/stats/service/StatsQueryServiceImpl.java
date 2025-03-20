package com.email.stats.service;

import com.email.stats.domain.*;
import com.email.stats.dto.AttachmentClickStatisticsResponse;
import com.email.stats.dto.ChartData;
import com.email.stats.dto.DashboardSummaryResponse;
import com.email.stats.dto.EmailOpenStatisticsResponse;
import com.email.stats.repository.StatsRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
* 통계 쿼리 서비스 구현 클래스입니다.
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsQueryServiceImpl implements StatsQueryService {

   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private final StatsRepository statsRepository;
    private final Timer statsQueryTimer;
    private final Counter statsRequestCounter;
   
   /**
    * 대시보드 요약 정보를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @return 대시보드 요약 정보
    */
   @Override
   @Transactional(readOnly = true)
   public DashboardSummaryResponse getDashboardSummary(LocalDate startDate, LocalDate endDate) {
       log.info("대시보드 요약 정보 조회: startDate={}, endDate={}", startDate, endDate);

       statsRequestCounter.increment();
       Timer.Sample sample = Timer.start();

       try {
           // 기존 메서드 코드 유지...

           // Existing code...

           log.info("대시보드 요약 정보 조회: startDate={}, endDate={}", startDate, endDate);

           // 발송 통계 조회
           List<DeliveryStats> deliveryStats = statsRepository.getDeliveryStats(startDate, endDate);

           // 발송 총계 계산
           int totalSentCount = deliveryStats.stream().mapToInt(DeliveryStats::getTotalCount).sum();
           int successCount = deliveryStats.stream().mapToInt(DeliveryStats::getSuccessCount).sum();
           int failCount = deliveryStats.stream().mapToInt(DeliveryStats::getFailCount).sum();

           // 오픈 통계 조회
           List<OpenStats> openStats = statsRepository.getOpenStats(startDate, endDate, null);
           int totalOpens = openStats.stream().mapToInt(OpenStats::getOpenCount).sum();

           // 첨부파일 클릭 통계 조회
           List<AttachmentStats> attachmentStats = statsRepository.getAttachmentStats(startDate, endDate, null);
           int totalAttachmentClicks = attachmentStats.stream().mapToInt(AttachmentStats::getClickCount).sum();

           // 일별 통계 조회
           List<DailyStats> dailyStats = statsRepository.getDailyStats(startDate, endDate);
           List<ChartData> chartData = processChartData(dailyStats);

           return DashboardSummaryResponse.builder()
                   .totalSentCount(totalSentCount)
                   .successCount(successCount)
                   .failCount(failCount)
                   .totalOpens(totalOpens)
                   .totalAttachmentClicks(totalAttachmentClicks)
                   .dailyStats(chartData)
                   .build();
       } catch (Exception e) {
           log.error("대시보드 요약 정보 조회 중 오류 발생", e);
           throw e;
       }


   }
   
   /**
    * 이메일 오픈 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param emailCategory 이메일 카테고리
    * @return 이메일 오픈 통계 목록
    */
   @Override
   @Transactional(readOnly = true)
   public List<EmailOpenStatisticsResponse> getEmailOpenStatistics(
           LocalDate startDate, LocalDate endDate, String emailCategory) {

       log.info("이메일 오픈 통계 조회: startDate={}, endDate={}, emailCategory={}",
               startDate, endDate, emailCategory);

       statsRequestCounter.increment();
       Timer.Sample sample = Timer.start();

       try {
           // 기존 메서드 코드 유지...

           // Existing code...
           List<OpenStats> openStats = statsRepository.getOpenStats(startDate, endDate, emailCategory);
           sample.stop(statsQueryTimer);
           return openStats.stream()
                   .map(stat -> {
                       double openRate = stat.getTotalEmails() > 0
                               ? (double) stat.getOpenCount() / stat.getTotalEmails() * 100
                               : 0;

                       return EmailOpenStatisticsResponse.builder()
                               .date(stat.getDate().format(DATE_FORMATTER))
                               .emailCategory(stat.getEmailCategory())
                               .totalEmails(stat.getTotalEmails())
                               .openCount(stat.getOpenCount())
                               .openRate(Math.round(openRate * 100) / 100.0) // 소수점 둘째자리까지 반올림
                               .build();
                   })
                   .collect(Collectors.toList());
       } catch (Exception e) {
           log.error("이메일 오픈 통계 조회 중 오류 발생", e);
           throw e;
       }




   }
   
   /**
    * 첨부파일 클릭 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param fileType 파일 유형
    * @return 첨부파일 클릭 통계 목록
    */
   @Override
   @Transactional(readOnly = true)
   public List<AttachmentClickStatisticsResponse> getAttachmentClickStatistics(
           LocalDate startDate, LocalDate endDate, String fileType) {

       log.info("첨부파일 클릭 통계 조회: startDate={}, endDate={}, fileType={}",
               startDate, endDate, fileType);

       statsRequestCounter.increment();
       Timer.Sample sample = Timer.start();

       try {
           // 기존 메서드 코드 유지...

           // Existing code...
           List<AttachmentStats> attachmentStats = statsRepository.getAttachmentStats(startDate, endDate, fileType);
           sample.stop(statsQueryTimer);
           return attachmentStats.stream()
                   .map(stat -> {
                       double clickRate = stat.getTotalAttachments() > 0
                               ? (double) stat.getClickCount() / stat.getTotalAttachments() * 100
                               : 0;

                       return AttachmentClickStatisticsResponse.builder()
                               .date(stat.getDate().format(DATE_FORMATTER))
                               .fileType(stat.getFileType())
                               .totalAttachments(stat.getTotalAttachments())
                               .clickCount(stat.getClickCount())
                               .clickRate(Math.round(clickRate * 100) / 100.0) // 소수점 둘째자리까지 반올림
                               .build();
                   })
                   .collect(Collectors.toList());
       } catch (Exception e) {
           log.error("첨부파일 클릭 통계 조회 중 오류 발생", e);
           throw e;
       }


   }
   
   /**
    * 일별 통계를 차트 데이터로 가공합니다.
    *
    * @param dailyStats 일별 통계 목록
    * @return 차트 데이터 목록
    */
   private List<ChartData> processChartData(List<DailyStats> dailyStats) {
       return dailyStats.stream()
               .map(stat -> ChartData.builder()
                       .date(stat.getDate().format(DATE_FORMATTER))
                       .sentCount(stat.getSentCount())
                       .openCount(stat.getOpenCount())
                       .clickCount(stat.getClickCount())
                       .build())
               .collect(Collectors.toList());
   }
}
