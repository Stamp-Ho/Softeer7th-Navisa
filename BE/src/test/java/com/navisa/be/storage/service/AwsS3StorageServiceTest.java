package com.navisa.be.storage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URL;

import com.navisa.be.storage.model.enums.StorageLocation;
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
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import com.navisa.be.storage.model.enums.ImageSize;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

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
        assertThat(response.objectKey()).startsWith(directory + "/" + objectKeyRoot);
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

    @Test
    @DisplayName("유효한 요청이면 S3 다운로드 Presigned URL 발급에 성공한다")
    void getPresignedUrl_fromS3_shouldReturnUrl_whenValidRequest() throws Exception {
        // given
        ImageSize size = ImageSize.ORIGIN;
        String objectKey = "foreigner-identity/origin/test-uuid.jpg";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/" + objectKey;

        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(new URL(expectedUrl));
        when(s3Presigner.presignGetObject((GetObjectPresignRequest) any())).thenReturn(mockPresignedRequest);

        // when
        String resultUrl = awsS3StorageService.getPresignedUrlFromS3(size, objectKey);

        // then
        assertThat(resultUrl).isEqualTo(expectedUrl);
        verify(s3Presigner, times(1)).presignGetObject((GetObjectPresignRequest) any());
    }

    @Test
    @DisplayName("Object Key가 유효하지 않으면 BaseException(INVALID_S3_OBJECT_KEY)이 발생한다")
    void getPresignedUrl_fromS3_shouldThrowException_whenObjectKeyIsNotValid() {
        // given
        ImageSize size = ImageSize.ORIGIN;
        String invalidObjectKey = ""; // 빈 문자열

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.getPresignedUrlFromS3(size, invalidObjectKey))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.INVALID_S3_OBJECT_KEY);
    }

    @Test
    @DisplayName("행정사 프로필 사진 요청이면 BaseException(AGENT_PROFILE_PRESIGNED_REQUEST)이 발생한다")
    void getPresignedUrl_fromS3_shouldThrowException_whenObjectKeyIsAgentProfile() {
        // given
        ImageSize size = ImageSize.ORIGIN;
        // StorageLocation.AGENT_PROFILE_IMAGE.getDirectory()는 "agent-profile"
        String agentProfileKey = "agent-profile/origin/test-uuid.jpg";

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.getPresignedUrlFromS3(size, agentProfileKey))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.AGENT_PROFILE_PRESIGNED_REQUEST);
    }

    @Test
    @DisplayName("S3 Exception 발생 시 BaseException(S3_RUNTIME_ERROR)이 발생한다")
    void getPresignedUrl_fromS3_shouldThrowException_whenS3ExceptionOccurs() {
        // given
        ImageSize size = ImageSize.ORIGIN;
        String objectKey = "foreigner-identity/origin/test-uuid.jpg";

        when(s3Presigner.presignGetObject((GetObjectPresignRequest) any()))
                .thenThrow(software.amazon.awssdk.services.s3.model.S3Exception.builder()
                        .statusCode(500)
                        .awsErrorDetails(software.amazon.awssdk.awscore.exception.AwsErrorDetails.builder()
                                .errorMessage("S3 Error").build())
                        .build());

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.getPresignedUrlFromS3(size, objectKey))
                .isInstanceOf(StorageDomainException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.S3_RUNTIME_ERROR);
    }
}
