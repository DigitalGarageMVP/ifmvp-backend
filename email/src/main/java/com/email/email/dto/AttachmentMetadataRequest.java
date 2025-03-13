package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* 첨부파일 메타데이터 요청 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "첨부파일 메타데이터 요청")
public class AttachmentMetadataRequest {
   
   @NotBlank(message = "파일명은 필수 입력값입니다.")
   @Schema(description = "파일명", example = "document.pdf")
   private String fileName;
   
   @NotBlank(message = "콘텐츠 타입은 필수 입력값입니다.")
   @Schema(description = "콘텐츠 타입", example = "application/pdf")
   private String contentType;
   
   @Positive(message = "파일 크기는 양수여야 합니다.")
   @Schema(description = "파일 크기", example = "1048576")
   private long fileSize;
}
