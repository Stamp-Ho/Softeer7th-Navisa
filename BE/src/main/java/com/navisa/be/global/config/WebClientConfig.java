package com.navisa.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        // 공통 설정 (타임아웃, 로깅 등)을 여기에 추가할 수 있습니다.
        return WebClient.builder();
    }
}
