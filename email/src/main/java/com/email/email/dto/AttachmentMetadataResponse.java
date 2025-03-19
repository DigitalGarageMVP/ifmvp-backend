package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 첨부파일 메타데이터 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "첨부파일 메타데이터 응답")
public class AttachmentMetadataResponse {
   
   @Schema(description = "첨부파일 ID", example = "12345678-1234-1234-1234-123456789012")
   private String attachmentId;
   
   @Schema(description = "파일명", example = "document.pdf")
   private String fileName;
   
   @Schema(description = "콘텐츠 타입", example = "application/pdf")
   private String contentType;
   
   @Schema(description = "파일 크기", example = "1048576")
   private long fileSize;
   
   @Schema(description = "업로드 시간", example = "2023-06-01 12:34:56")
   private String uploadTime;
   
   @Schema(description = "상태", example = "UPLOADED")
   private String status;

   @Schema(description = "첨부파일 URL", example = "http://localhost/attch")
   private String downloadUrl;
}
