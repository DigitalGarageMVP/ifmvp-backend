package com.email.mock.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* Swagger 설정 클래스입니다.
*/
@Configuration
public class SwaggerConfig {

   /**
    * Swagger OpenAPI 구성을 제공합니다.
    *
    * @return OpenAPI 인스턴스
    */
   @Bean
   public OpenAPI openAPI() {
       String securitySchemeName = "bearerAuth";
       return new OpenAPI()
               .info(new Info()
                       .title("발송 목업 서비스 API")
                       .description("이메일 발송 시뮬레이션 및 이벤트 발행 API 문서")
                       .version("v1.0.0"))
               .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
               .components(new Components()
                       .addSecuritySchemes(securitySchemeName,
                               new SecurityScheme()
                                       .name(securitySchemeName)
                                       .type(SecurityScheme.Type.HTTP)
                                       .scheme("bearer")
                                       .bearerFormat("JWT")));
   }
}
