package com.navisa.be.support.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;

@Profile("test")
@TestConfiguration
public class MockAwsConfig {

    @Bean
    @Primary // 여러 빈이 발견되면 이걸 우선으로 등록
    public S3Presigner s3Presigner() {
        S3Presigner mock = Mockito.mock(S3Presigner.class);

        // Stub presignGetObject
        PresignedGetObjectRequest getObjectResponse = Mockito.mock(PresignedGetObjectRequest.class);
        try {
            Mockito.when(getObjectResponse.url()).thenReturn(new java.net.URL("https://example.com/presigned-get"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Mockito.when(mock.presignGetObject(Mockito.any(GetObjectPresignRequest.class))).thenReturn(getObjectResponse);

        // Stub presignPutObject
        PresignedPutObjectRequest putObjectResponse = Mockito.mock(PresignedPutObjectRequest.class);
        try {
            Mockito.when(putObjectResponse.url()).thenReturn(new java.net.URL("https://example.com/presigned-put"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Mockito.when(mock.presignPutObject(Mockito.any(PutObjectPresignRequest.class))).thenReturn(putObjectResponse);

        return mock;
    }
}
