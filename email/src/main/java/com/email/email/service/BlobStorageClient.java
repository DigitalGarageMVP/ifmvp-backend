package com.email.email.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
* Azure Blob Storage 클라이언트 클래스입니다.
*/
@Slf4j
@Component
@RequiredArgsConstructor
public class BlobStorageClient {

   @Value("${azure.storage.connection-string}")
   private String connectionString;
   
   @Value("${azure.storage.container.attachments}")
   private String containerName;
   
   /**
    * SAS 토큰 정보 클래스입니다.
    */
   @Getter
   public static class SasTokenInfo {
       private final String sasToken;
       private final String blobUrl;
       
       public SasTokenInfo(String sasToken, String blobUrl) {
           this.sasToken = sasToken;
           this.blobUrl = blobUrl;
       }
   }
   
   /**
    * SAS 토큰을 생성합니다.
    *
    * @param blobName Blob 이름
    * @param contentType 콘텐츠 타입
    * @return SAS 토큰 정보
    */
   public SasTokenInfo generateSasToken(String blobName, String contentType) {
       log.info("SAS 토큰 생성: blobName={}, contentType={}", blobName, contentType);
       
       BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
               .connectionString(connectionString)
               .buildClient();
       
       BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
       
       // 컨테이너가 존재하지 않으면 생성
       if (!containerClient.exists()) {
           log.info("컨테이너 생성: containerName={}", containerName);
           containerClient.create();
       }
       
       BlobClient blobClient = containerClient.getBlobClient(blobName);
       
       // SAS 토큰 생성 설정
       BlobSasPermission permission = new BlobSasPermission()
               .setReadPermission(true)
               .setWritePermission(true)
               .setCreatePermission(true);
       
       // 7일 유효기간의 SAS 토큰 생성
       OffsetDateTime expiryTime = OffsetDateTime.now().plus(7, ChronoUnit.DAYS);
       
       BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(
               expiryTime, permission);
       
       String sasToken = blobClient.generateSas(sasSignatureValues);
       String blobUrl = blobClient.getBlobUrl();
       
       log.info("SAS 토큰 생성 완료: blobUrl={}", blobUrl);
       
       return new SasTokenInfo(sasToken, blobUrl);
   }
   
   /**
    * Blob URL을 반환합니다.
    *
    * @param blobName Blob 이름
    * @return Blob URL
    */
   public String getUrl(String blobName) {
       BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
               .connectionString(connectionString)
               .buildClient();
       
       BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
       BlobClient blobClient = containerClient.getBlobClient(blobName);
       
       return blobClient.getBlobUrl();
   }
}
