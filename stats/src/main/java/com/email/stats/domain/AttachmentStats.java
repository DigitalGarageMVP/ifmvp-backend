package com.email.stats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
* 첨부파일 통계 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentStats {
   
   private LocalDate date;
   private String fileType;
   private int totalAttachments;
   private int clickCount;
}
