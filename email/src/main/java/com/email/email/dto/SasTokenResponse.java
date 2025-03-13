package com.email.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* SAS 토큰 응답 DTO 클래스입니다.
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SAS 토큰 응답")
public class SasTokenResponse {
   
   @Schema(description = "SAS 토큰", example = "se=2023-06-01T12%3A00%3A00Z&sp=rw&sv=2021-06-08&sr=b&sig=...")
   private String sasToken;
   
   @Schema(description = "Blob URL", example = "https://storage.blob.core.windows.net/attachments/12345678-1234-1234-1234-123456789012")
   private String blobUrl;
   
   @Schema(description = "컨테이너 이름", example = "attachments")
   private String containerName;
   
   @Schema(description = "Blob 이름", example = "12345678-1234-1234-1234-123456789012")
   private String blobName;
   
   @Schema(description = "만료 시간", example = "1685620800000")
   private long expirationTime;
}
