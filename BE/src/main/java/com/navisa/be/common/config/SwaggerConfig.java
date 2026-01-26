package com.navisa.be.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Navisa API 명세서")
                        .description("복잡한 비자 신청의 모든 과정을 스마트하게! 외국인과 전문 행정사를 잇는 맞춤형 매칭 플랫폼, Navisa의 API 문서입니다.")
                        .version("v1.0.0"));
    }
}