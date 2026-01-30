package com.navisa.be.storage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.time.LocalDate;

import com.navisa.be.storage.constant.StorageLocation;
import com.navisa.be.storage.dto.request.IssuePresignedUrlRequest;
import com.navisa.be.storage.dto.response.IssuePresignedUrlResponse;
import com.navisa.be.storage.exception.StorageDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class AwsS3StorageServiceTest {

    private final String BUCKET_NAME = "test-bucket";

    private AwsS3StorageService awsS3StorageService;

    @Mock
    private S3Presigner s3Presigner;

    @BeforeEach
    void setUp() {
        awsS3StorageService = new AwsS3StorageService(s3Presigner);

        // @Value 필드 주입
        ReflectionTestUtils.setField(awsS3StorageService, "bucketName", BUCKET_NAME);
    }

    @Test
    @DisplayName("지원하는 콘텐트 타입이면 Presigned URL 발급에 성공한다")
    void issuePresignedUrl_shouldReturnUrl_whenSupportsContentType() throws Exception {
        // given
        String contentType = "image/jpeg";
        String fileUsage = "agent-profile";
        String url = "https://test-bucket.s3.amazonaws.com/test-path";
        String objectKeyRoot = "origin";
        String directory = StorageLocation.from(fileUsage).getDirectory();

        IssuePresignedUrlRequest request = new IssuePresignedUrlRequest(contentType, fileUsage);

        PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(new URL(url));
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

        // when
        IssuePresignedUrlResponse response = awsS3StorageService.issuePresignedUrl(request);
        // then
        assertThat(response.url()).contains(url);
        assertThat(response.objectKey()).startsWith(objectKeyRoot + "/" + directory + "/" + LocalDate.now().getYear());
        verify(s3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    @DisplayName("지원하지 않는 콘텐트 타입이면 StorageDomainException이 발생한다")
    void issuePresignedUrl_shouldThrowException_whenNotSupportContentType() {
        // given
        String invalidContentType = "application/pdf"; // 지원하지 않는 타입
        String fileUsage = "agent-profile";
        IssuePresignedUrlRequest request = new IssuePresignedUrlRequest(invalidContentType, fileUsage);

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageDomainException.class);

        // Presigner가 호출되지 않았는지 확인
        verify(s3Presigner, never()).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    @DisplayName("지원하지 않는 파일 용도이면 StorageDomainException이 발생한다")
    void issuePresignedUrl_shouldThrowException_whenNotSupportUsage() {
        // given
        String contentType = "image/jpeg";
        String invalidFileUsage = "unknown-usage"; // 지원하지 않는 용도
        IssuePresignedUrlRequest request = new IssuePresignedUrlRequest(contentType, invalidFileUsage);

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageDomainException.class);

        // Presigner가 호출되지 않았는지 확인
        verify(s3Presigner, never()).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    @DisplayName("S3 SDK 예외 발생 시 StorageDomainException으로 변환된다")
    void issuePresignedUrl_shouldThrowException_whenSdkExceptionOccurs() {
        // given
        String contentType = "image/jpeg";
        String fileUsage = "agent-profile";
        IssuePresignedUrlRequest request = new IssuePresignedUrlRequest(contentType, fileUsage);

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(software.amazon.awssdk.core.exception.SdkClientException.create("SDK Error"));

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageDomainException.class);
    }

    @Test
    @DisplayName("알 수 없는 예외 발생 시 StorageDomainException으로 변환된다")
    void issuePresignedUrl_shouldThrowException_whenUnknownExceptionOccurs() {
        // given
        String contentType = "image/jpeg";
        String fileUsage = "agent-profile";
        IssuePresignedUrlRequest request = new IssuePresignedUrlRequest(contentType, fileUsage);

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Unexpected Error"));

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageDomainException.class);
    }
}
