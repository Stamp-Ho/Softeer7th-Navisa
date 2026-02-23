package com.navisa.be.global.infra.aws;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URL;

import com.navisa.be.global.common.model.enums.StorageLocation;
import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.global.common.dto.response.PresignedUrlResponse;
import com.navisa.be.global.common.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.web.response.ResponseStatus;

@ExtendWith(MockitoExtension.class)
class AwsS3StorageClientTest {

    private final String BUCKET_NAME = "test-bucket";

    private AwsS3StorageClient awsS3StorageService;

    @Mock
    private S3Presigner s3Presigner;

    @BeforeEach
    void setUp() {
        awsS3StorageService = new AwsS3StorageClient(s3Presigner);

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

        IssuedPresignedUrlRequest request = new IssuedPresignedUrlRequest(contentType, fileUsage);

        PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(new URL(url));
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

        // when
        PresignedUrlResponse response = awsS3StorageService.issuePresignedUrl(request);
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
        IssuedPresignedUrlRequest request = new IssuedPresignedUrlRequest(invalidContentType, fileUsage);

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageException.class);

        // Presigner가 호출되지 않았는지 확인
        verify(s3Presigner, never()).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    @DisplayName("지원하지 않는 파일 용도이면 StorageDomainException이 발생한다")
    void issuePresignedUrl_shouldThrowException_whenNotSupportUsage() {
        // given
        String contentType = "image/jpeg";
        String invalidFileUsage = "unknown-usage"; // 지원하지 않는 용도
        IssuedPresignedUrlRequest request = new IssuedPresignedUrlRequest(contentType, invalidFileUsage);

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageException.class);

        // Presigner가 호출되지 않았는지 확인
        verify(s3Presigner, never()).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    @DisplayName("S3 SDK 예외 발생 시 StorageDomainException으로 변환된다")
    void issuePresignedUrl_shouldThrowException_whenSdkExceptionOccurs() {
        // given
        String contentType = "image/jpeg";
        String fileUsage = "agent-profile";
        IssuedPresignedUrlRequest request = new IssuedPresignedUrlRequest(contentType, fileUsage);

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(software.amazon.awssdk.core.exception.SdkClientException.create("SDK Error"));

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageException.class);
    }

    @Test
    @DisplayName("알 수 없는 예외 발생 시 StorageDomainException으로 변환된다")
    void issuePresignedUrl_shouldThrowException_whenUnknownExceptionOccurs() {
        // given
        String contentType = "image/jpeg";
        String fileUsage = "agent-profile";
        IssuedPresignedUrlRequest request = new IssuedPresignedUrlRequest(contentType, fileUsage);

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Unexpected Error"));

        // when & then
        assertThatThrownBy(() -> awsS3StorageService.issuePresignedUrl(request))
                .isInstanceOf(StorageException.class);
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
    @DisplayName("유효하지 않은 키나 행정사 경로는 에러 대신 null을 반환한다")
    void getPresignedUrl_ShouldReturnNull_ForInvalidOrAgentKey() {
        String blankResult = awsS3StorageService.getPresignedUrlFromS3(ImageSize.MEDIUM, "");
        String agentResult = awsS3StorageService.getPresignedUrlFromS3(ImageSize.MEDIUM, "agent-profile/test.jpg");

        assertThat(blankResult).isNull();
        assertThat(agentResult).isNull();
    }

    @Test
    @DisplayName("행정사 프로필 사진 요청이면 S3 보안 URL 대신 null을 반환한다")
    void getPresignedUrl_fromS3_shouldReturnNull_whenObjectKeyIsAgentProfile() {
        // given
        ImageSize size = ImageSize.ORIGIN;
        String agentProfileKey = "agent-profile/origin/test-uuid.jpg";

        // when
        String result = awsS3StorageService.getPresignedUrlFromS3(size, agentProfileKey);

        // then
        assertThat(result).isNull();
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
                .isInstanceOf(StorageException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.S3_RUNTIME_ERROR);
    }

    @ParameterizedTest
    @DisplayName("이미지 키가 null, 빈 문자열, 혹은 공백이면 예외 없이 null을 반환한다.")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void getPresignedUrl_ShouldReturnNull_WhenKeyIsInvalid(String invalidKey) {
        // when
        String result = awsS3StorageService.getPresignedUrlFromS3(ImageSize.MEDIUM, invalidKey);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("행정사 프로필 경로(agent-profile/)로 시작하는 키는 예외 없이 null을 반환한다.")
    void getPresignedUrl_ShouldReturnNull_WhenKeyIsAgentPath() {
        // given
        String agentKey = StorageLocation.AGENT_PROFILE_IMAGE.getDirectory() + "/test-image.webp";

        // when
        String result = awsS3StorageService.getPresignedUrlFromS3(ImageSize.MEDIUM, agentKey);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("문자열 'null'이나 'undefined'가 들어와도 안전하게 null을 반환한다.")
    void getPresignedUrl_ShouldReturnNull_WhenKeyIsStringNull() {
        // when
        String resultNull = awsS3StorageService.getPresignedUrlFromS3(ImageSize.MEDIUM, "null");
        String resultUndefined = awsS3StorageService.getPresignedUrlFromS3(ImageSize.MEDIUM, "undefined");

        // then
        assertThat(resultNull).isNull();
        assertThat(resultUndefined).isNull();
    }
}
