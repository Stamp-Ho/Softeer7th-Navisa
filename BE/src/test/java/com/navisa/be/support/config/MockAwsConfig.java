package com.navisa.be.support.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Profile("test")
@TestConfiguration
public class MockAwsConfig {

    @Bean
    @Primary // 여러 빈이 발견되면 이걸 우선으로 등록
    public S3Presigner s3Presigner() {
        return Mockito.mock(S3Presigner.class);
    }
}
