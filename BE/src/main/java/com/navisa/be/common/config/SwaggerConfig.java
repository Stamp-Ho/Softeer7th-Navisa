package com.navisa.be.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        Server prodServer = new Server().url("https://api.navisa.site").description("Production Server");
        Server localServer = new Server().url("http://localhost:8080").description("Local Server");

        return new OpenAPI()
                .info(new Info()
                        .title("Navisa API 명세서")
                        .description("복잡한 비자 신청의 모든 과정을 스마트하게! 외국인과 전문 행정사를 잇는 맞춤형 매칭 플랫폼, Navisa의 API 문서입니다.")
                        .version("v1.0.0"))

                .servers(List.of(prodServer, localServer))
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