package com.email.email.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
* Azure Blob Storage 설정 클래스입니다.
*/
@Configuration
public class AzureBlobConfig {

   @Value("${azure.storage.connection-string}")
   private String connectionString;
   
   @Value("${azure.storage.container.attachments}")
   private String attachmentsContainer;
   
   /**
    * Blob 서비스 클라이언트를 생성합니다.
    *
    * @return Blob 서비스 클라이언트
    */
   @Bean
   public BlobServiceClient blobServiceClient() {
       return new BlobServiceClientBuilder()
               .connectionString(connectionString)
               .buildClient();
   }
   
   /**
    * 컨테이너 이름 맵을 생성합니다.
    *
    * @return 컨테이너 이름 맵
    */
   @Bean
   public Map<String, String> containerNames() {
       Map<String, String> containerNames = new HashMap<>();
       containerNames.put("attachments", attachmentsContainer);
       return containerNames;
   }
}
