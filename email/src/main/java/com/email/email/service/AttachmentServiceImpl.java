package com.email.email.service;

import com.email.common.exception.BusinessException;
import com.email.common.exception.ErrorCode;
import com.email.email.domain.AttachmentMetadata;
import com.email.email.domain.AttachmentStatus;
import com.email.email.dto.AttachmentMetadataRequest;
import com.email.email.dto.AttachmentMetadataResponse;
import com.email.email.dto.SasTokenResponse;
import com.email.email.repository.EmailRepository;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 첨부파일 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long DEFAULT_SAS_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7일

    @Value("${azure.storage.container.attachments}")
    private String containerName;

    private final EmailRepository emailRepository;
    private final BlobStorageClient blobStorageClient;
    private final Timer sasTokenGenerationTimer;
    private final Counter attachmentUploadCounter;
    private final Timer attachmentSizeTimer;

    /**
     * 첨부파일 업로드용 SAS 토큰을 발급합니다.
     *
     * @param request 첨부파일 메타데이터 요청 정보
     * @return SAS 토큰 응답 정보
     */
    @Override
    @Transactional
    public SasTokenResponse generateSasToken(AttachmentMetadataRequest request) {
        log.info("첨부파일 업로드용 SAS 토큰 발급 요청: fileName={}", request.getFileName());

        // 첨부파일 업로드 요청 카운터 증가
        attachmentUploadCounter.increment();

        // 첨부파일 크기 기록 (ms 단위로 변환하여 타이머에 기록)
        // 타이머를 히스토그램으로 활용하기 위한 트릭
        long fileSizeInKb = request.getFileSize() / 1024;
        attachmentSizeTimer.record(Duration.ofMillis(fileSizeInKb));

        // SAS 토큰 생성 시간 측정 시작
        Timer.Sample tokenGenerationSample = Timer.start();

        String blobName = generateUniqueBlobName(request.getFileName());

        try {
            // SAS 토큰 생성
            BlobStorageClient.SasTokenInfo sasTokenInfo =
                    blobStorageClient.generateSasToken(blobName, request.getContentType());

            // 첨부파일 메타데이터 저장
            String attachmentId = UUID.randomUUID().toString();

            String downloadUrl = sasTokenInfo.getBlobUrl() + "?" + sasTokenInfo.getSasToken();

            AttachmentMetadata metadata = AttachmentMetadata.builder()
                    .id(attachmentId)
                    .fileName(request.getFileName())
                    .contentType(request.getContentType())
                    .fileSize(request.getFileSize())
                    .blobName(blobName)
                    .downloadUrl(downloadUrl)
                    .containerName(containerName)
                    .uploadTime(LocalDateTime.now())
                    .status(AttachmentStatus.UPLOADED)
                    .build();

            emailRepository.saveAttachmentMetadata(metadata);
            log.info("첨부파일 메타데이터 저장 완료: attachmentId={}, blobName={}", attachmentId, blobName);

            SasTokenResponse response = SasTokenResponse.builder()
                    .sasToken(sasTokenInfo.getSasToken())
                    .blobUrl(sasTokenInfo.getBlobUrl())
                    .containerName(containerName)
                    .blobName(blobName)
                    .expirationTime(System.currentTimeMillis() + DEFAULT_SAS_TOKEN_VALIDITY)
                    .attachmentId(attachmentId)
                    .build();

            // SAS 토큰 생성 시간 측정 종료
            tokenGenerationSample.stop(sasTokenGenerationTimer);

            return response;

        } catch (Exception e) {
            // 오류 발생 시에도 타이머 측정 종료
            tokenGenerationSample.stop(sasTokenGenerationTimer);
            log.error("첨부파일 메타데이터 저장 실패: fileName={}, error={}", request.getFileName(), e.getMessage(), e);
            throw new RuntimeException("첨부파일 메타데이터 저장 실패", e);
        }
    }

    /**
     * 첨부파일 메타데이터를 조회합니다.
     *
     * @param attachmentId 첨부파일 ID
     * @return 첨부파일 메타데이터 응답 정보
     */
    @Override
    @Transactional(readOnly = true)
    public AttachmentMetadataResponse getAttachmentMetadata(String attachmentId) {
        log.info("첨부파일 메타데이터 조회: attachmentId={}", attachmentId);

        AttachmentMetadata metadata = emailRepository.findAttachmentById(attachmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "첨부파일을 찾을 수 없습니다."));

        return AttachmentMetadataResponse.builder()
                .attachmentId(metadata.getId())
                .fileName(metadata.getFileName())
                .contentType(metadata.getContentType())
                .fileSize(metadata.getFileSize())
                .uploadTime(metadata.getUploadTime().format(DATE_TIME_FORMATTER))
                .status(metadata.getStatus().name())
                .downloadUrl(metadata.getDownloadUrl())
                .build();
    }

    /**
     * 첨부파일 상세 정보를 조회합니다.
     *
     * @param attachmentIds 첨부파일 ID 목록
     * @return 첨부파일 메타데이터 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttachmentMetadata> getAttachmentDetails(List<String> attachmentIds) {
        log.info("첨부파일 상세 정보 조회: attachmentIds={}", attachmentIds);

        List<AttachmentMetadata> attachments = emailRepository.findAttachmentsByIds(attachmentIds);

        // 존재하지 않는 첨부파일이 있는지 확인
        if (attachments.size() != attachmentIds.size()) {
            List<String> foundIds = attachments.stream()
                    .map(AttachmentMetadata::getId)
                    .collect(Collectors.toList());

            List<String> missingIds = attachmentIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            log.warn("존재하지 않는 첨부파일 ID: {}", missingIds);
            throw new BusinessException(ErrorCode.NOT_FOUND, "일부 첨부파일을 찾을 수 없습니다.");
        }

        return attachments;
    }

    /**
     * 고유한 Blob 이름을 생성합니다.
     *
     * @param fileName 파일명
     * @return 생성된 Blob 이름
     */
    private String generateUniqueBlobName(String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex);
        }

        return UUID.randomUUID().toString() + extension;
    }
}
