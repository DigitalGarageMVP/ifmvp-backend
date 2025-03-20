package com.email.email.controller;

import com.email.common.dto.ApiResponse;
import com.email.email.dto.AttachmentMetadataRequest;
import com.email.email.dto.AttachmentMetadataResponse;
import com.email.email.dto.SasTokenResponse;
import com.email.email.service.AttachmentService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 첨부파일 API 컨트롤러입니다.
 * 메트릭 수집을 위한 @Timed 어노테이션이 추가되었습니다.
 */
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Tag(name = "첨부파일 API", description = "첨부파일 토큰 발급 및 메타데이터 조회 API")
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 첨부파일 업로드용 SAS 토큰을 발급합니다.
     *
     * @param request 첨부파일 메타데이터 요청 정보
     * @return SAS 토큰 응답 정보
     */
    @PostMapping("/token")
    @Operation(summary = "첨부파일 업로드용 SAS 토큰 발급", description = "첨부파일 업로드를 위한 SAS 토큰을 발급합니다.")
    @Timed(value = "attachment_token_api_time", description = "첨부파일 토큰 API 응답 시간")
    public ResponseEntity<ApiResponse<SasTokenResponse>> generateSasToken(
            @Valid @RequestBody AttachmentMetadataRequest request) {
        SasTokenResponse response = attachmentService.generateSasToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 첨부파일 메타데이터를 조회합니다.
     *
     * @param attachmentId 첨부파일 ID
     * @return 첨부파일 메타데이터 응답 정보
     */
    @GetMapping("/{attachmentId}")
    @Operation(summary = "첨부파일 메타데이터 조회", description = "첨부파일의 메타데이터를 조회합니다.")
    @Timed(value = "attachment_metadata_api_time", description = "첨부파일 메타데이터 API 응답 시간")
    public ResponseEntity<ApiResponse<AttachmentMetadataResponse>> getAttachmentMetadata(
            @Parameter(description = "첨부파일 ID", example = "12345678-1234-1234-1234-123456789012")
            @PathVariable String attachmentId) {
        AttachmentMetadataResponse response = attachmentService.getAttachmentMetadata(attachmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}