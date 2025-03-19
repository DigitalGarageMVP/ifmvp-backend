package com.email.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
* 첨부파일 메타데이터 도메인 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentMetadata {
   
   private String id;
   private String fileName;
   private String contentType;
   private long fileSize;
   private String blobName;
   private String downloadUrl;
   private String containerName;
   private LocalDateTime uploadTime;
   private AttachmentStatus status;
}
