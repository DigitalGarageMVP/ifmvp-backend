package com.email.stats.repository;

import com.email.stats.domain.AttachmentStats;
import com.email.stats.domain.DailyStats;
import com.email.stats.domain.DeliveryStats;
import com.email.stats.domain.OpenStats;
import com.email.stats.exception.StatsRepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* 통계 저장소 구현 클래스입니다.
*/
@Slf4j
@Repository
public class StatsRepositoryImpl implements StatsRepository {

   private final JdbcTemplate jdbcTemplate;
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   
   public StatsRepositoryImpl(JdbcTemplate jdbcTemplate) {
       this.jdbcTemplate = jdbcTemplate;
   }
   
   /**
    * 발송 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @return 발송 통계 목록
    */
   @Override
   public List<DeliveryStats> getDeliveryStats(LocalDate startDate, LocalDate endDate) {
       String sql = "SELECT date, total_count, success_count, fail_count " +
               "FROM delivery_stats " +
               "WHERE date BETWEEN ? AND ? " +
               "ORDER BY date ASC";
       
       try {
           return jdbcTemplate.query(sql, new DeliveryStatsRowMapper(), startDate, endDate);
       } catch (Exception e) {
           log.error("발송 통계 조회 오류: startDate={}, endDate={}", startDate, endDate, e);
           throw new StatsRepositoryException("발송 통계 조회 실패", e);
       }
   }
   
   /**
    * 오픈 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param emailCategory 이메일 카테고리
    * @return 오픈 통계 목록
    */
   @Override
   public List<OpenStats> getOpenStats(LocalDate startDate, LocalDate endDate, String emailCategory) {
       StringBuilder sql = new StringBuilder(
               "SELECT date, email_category, total_emails, open_count " +
               "FROM open_stats " +
               "WHERE date BETWEEN ? AND ?");
       
       List<Object> params = new ArrayList<>();
       params.add(startDate);
       params.add(endDate);
       
       if (emailCategory != null && !emailCategory.isEmpty()) {
           sql.append(" AND email_category = ?");
           params.add(emailCategory);
       }
       
       sql.append(" ORDER BY date ASC");
       
       try {
           return jdbcTemplate.query(sql.toString(), new OpenStatsRowMapper(), params.toArray());
       } catch (Exception e) {
           log.error("오픈 통계 조회 오류: startDate={}, endDate={}, emailCategory={}", 
                   startDate, endDate, emailCategory, e);
           throw new StatsRepositoryException("오픈 통계 조회 실패", e);
       }
   }
   
   /**
    * 첨부파일 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @param fileType 파일 유형
    * @return 첨부파일 통계 목록
    */
   @Override
   public List<AttachmentStats> getAttachmentStats(LocalDate startDate, LocalDate endDate, String fileType) {
       StringBuilder sql = new StringBuilder(
               "SELECT date, file_type, total_attachments, click_count " +
               "FROM attachment_stats " +
               "WHERE date BETWEEN ? AND ?");
       
       List<Object> params = new ArrayList<>();
       params.add(startDate);
       params.add(endDate);
       
       if (fileType != null && !fileType.isEmpty()) {
           sql.append(" AND file_type = ?");
           params.add(fileType);
       }
       
       sql.append(" ORDER BY date ASC");
       
       try {
           return jdbcTemplate.query(sql.toString(), new AttachmentStatsRowMapper(), params.toArray());
       } catch (Exception e) {
           log.error("첨부파일 통계 조회 오류: startDate={}, endDate={}, fileType={}", 
                   startDate, endDate, fileType, e);
           throw new StatsRepositoryException("첨부파일 통계 조회 실패", e);
       }
   }
   
   /**
    * 일별 통계를 조회합니다.
    *
    * @param startDate 시작일
    * @param endDate 종료일
    * @return 일별 통계 목록
    */
   @Override
   public List<DailyStats> getDailyStats(LocalDate startDate, LocalDate endDate) {
       String sql = "SELECT date, sent_count, open_count, click_count " +
               "FROM daily_stats " +
               "WHERE date BETWEEN ? AND ? " +
               "ORDER BY date ASC";
       
       try {
           return jdbcTemplate.query(sql, new DailyStatsRowMapper(), startDate, endDate);
       } catch (Exception e) {
           log.error("일별 통계 조회 오류: startDate={}, endDate={}", startDate, endDate, e);
           throw new StatsRepositoryException("일별 통계 조회 실패", e);
       }
   }
   
   /**
    * 발송 통계를 업데이트합니다.
    *
    * @param deliveryData 발송 이벤트 데이터
    */
   @Override
   public void updateDeliveryStats(Map<String, Object> deliveryData) {
       try {
           LocalDate today = LocalDate.now();
           String status = (String) deliveryData.get("status");
           
           // 일별 통계 업데이트
           String updateDailyStatsSql = 
                   "INSERT INTO daily_stats (date, sent_count, open_count, click_count) " +
                   "VALUES (?, 1, 0, 0) " +
                   "ON CONFLICT (date) " +
                   "DO UPDATE SET sent_count = daily_stats.sent_count + 1";
           
           jdbcTemplate.update(updateDailyStatsSql, today);
           
           // 발송 통계 업데이트
           String updateDeliveryStatsSql = 
                   "INSERT INTO delivery_stats (date, total_count, success_count, fail_count) " +
                   "VALUES (?, 1, ?, ?) " +
                   "ON CONFLICT (date) " +
                   "DO UPDATE SET " +
                   "total_count = delivery_stats.total_count + 1, " +
                   "success_count = delivery_stats.success_count + ?, " +
                   "fail_count = delivery_stats.fail_count + ?";
           
           int isSuccess = "DELIVERED".equals(status) ? 1 : 0;
           int isFailed = "FAILED".equals(status) ? 1 : 0;
           
           jdbcTemplate.update(updateDeliveryStatsSql, today, isSuccess, isFailed, isSuccess, isFailed);
           
           log.info("발송 통계 업데이트 완료: date={}, status={}", today, status);
       } catch (Exception e) {
           log.error("발송 통계 업데이트 오류", e);
           throw new StatsRepositoryException("발송 통계 업데이트 실패", e);
       }
   }
   
   /**
    * 오픈 통계를 업데이트합니다.
    *
    * @param openData 오픈 이벤트 데이터
    */
   @Override
   public void updateOpenStats(Map<String, Object> openData) {
       try {
           LocalDate today = LocalDate.now();
           String emailId = (String) openData.get("emailId");
           
           // 기본 카테고리 설정
           String emailCategory = "GENERAL";
           
           // 이메일 ID를 기반으로 카테고리를 판단할 수 있지만 예제에서는 기본값 사용
           
           // 일별 통계 업데이트
           String updateDailyStatsSql = 
                   "INSERT INTO daily_stats (date, sent_count, open_count, click_count) " +
                   "VALUES (?, 0, 1, 0) " +
                   "ON CONFLICT (date) " +
                   "DO UPDATE SET open_count = daily_stats.open_count + 1";
           
           jdbcTemplate.update(updateDailyStatsSql, today);
           
           // 오픈 통계 업데이트
           String updateOpenStatsSql = 
                   "INSERT INTO open_stats (date, email_category, total_emails, open_count) " +
                   "VALUES (?, ?, 1, 1) " +
                   "ON CONFLICT (date, email_category) " +
                   "DO UPDATE SET open_count = open_stats.open_count + 1";
           
           jdbcTemplate.update(updateOpenStatsSql, today, emailCategory);
           
           log.info("오픈 통계 업데이트 완료: date={}, emailId={}, category={}", today, emailId, emailCategory);
       } catch (Exception e) {
           log.error("오픈 통계 업데이트 오류", e);
           throw new StatsRepositoryException("오픈 통계 업데이트 실패", e);
       }
   }
   
   /**
    * 첨부파일 통계를 업데이트합니다.
    *
    * @param clickData 클릭 이벤트 데이터
    */
   @Override
   public void updateAttachmentStats(Map<String, Object> clickData) {
       try {
           LocalDate today = LocalDate.now();
           String attachmentId = (String) clickData.get("attachmentId");
           
           // 기본 파일 유형 설정
           String fileType = "PDF";
           
           // 첨부파일 ID를 기반으로 파일 유형을 조회할 수 있지만 예제에서는 기본값 사용
           
           // 일별 통계 업데이트
           String updateDailyStatsSql = 
                   "INSERT INTO daily_stats (date, sent_count, open_count, click_count) " +
                   "VALUES (?, 0, 0, 1) " +
                   "ON CONFLICT (date) " +
                   "DO UPDATE SET click_count = daily_stats.click_count + 1";
           
           jdbcTemplate.update(updateDailyStatsSql, today);
           
           // 첨부파일 통계 업데이트
           String updateAttachmentStatsSql = 
                   "INSERT INTO attachment_stats (date, file_type, total_attachments, click_count) " +
                   "VALUES (?, ?, 1, 1) " +
                   "ON CONFLICT (date, file_type) " +
                   "DO UPDATE SET click_count = attachment_stats.click_count + 1";
           
           jdbcTemplate.update(updateAttachmentStatsSql, today, fileType);
           
           log.info("첨부파일 통계 업데이트 완료: date={}, attachmentId={}, fileType={}", 
                   today, attachmentId, fileType);
       } catch (Exception e) {
           log.error("첨부파일 통계 업데이트 오류", e);
           throw new StatsRepositoryException("첨부파일 통계 업데이트 실패", e);
       }
   }
   
   /**
    * 발송 통계 로우 매퍼 클래스입니다.
    */
   private static class DeliveryStatsRowMapper implements RowMapper<DeliveryStats> {
       @Override
       public DeliveryStats mapRow(ResultSet rs, int rowNum) throws SQLException {
           return DeliveryStats.builder()
                   .date(rs.getDate("date").toLocalDate())
                   .totalCount(rs.getInt("total_count"))
                   .successCount(rs.getInt("success_count"))
                   .failCount(rs.getInt("fail_count"))
                   .build();
       }
   }
   
   /**
    * 오픈 통계 로우 매퍼 클래스입니다.
    */
   private static class OpenStatsRowMapper implements RowMapper<OpenStats> {
       @Override
       public OpenStats mapRow(ResultSet rs, int rowNum) throws SQLException {
           return OpenStats.builder()
                   .date(rs.getDate("date").toLocalDate())
                   .emailCategory(rs.getString("email_category"))
                   .totalEmails(rs.getInt("total_emails"))
                   .openCount(rs.getInt("open_count"))
                   .build();
       }
   }
   
   /**
    * 첨부파일 통계 로우 매퍼 클래스입니다.
    */
   private static class AttachmentStatsRowMapper implements RowMapper<AttachmentStats> {
       @Override
       public AttachmentStats mapRow(ResultSet rs, int rowNum) throws SQLException {
           return AttachmentStats.builder()
                   .date(rs.getDate("date").toLocalDate())
                   .fileType(rs.getString("file_type"))
                   .totalAttachments(rs.getInt("total_attachments"))
                   .clickCount(rs.getInt("click_count"))
                   .build();
       }
   }
   
   /**
    * 일별 통계 로우 매퍼 클래스입니다.
    */
   private static class DailyStatsRowMapper implements RowMapper<DailyStats> {
       @Override
       public DailyStats mapRow(ResultSet rs, int rowNum) throws SQLException {
           return DailyStats.builder()
                   .date(rs.getDate("date").toLocalDate())
                   .sentCount(rs.getInt("sent_count"))
                   .openCount(rs.getInt("open_count"))
                   .clickCount(rs.getInt("click_count"))
                   .build();
       }
   }
}
