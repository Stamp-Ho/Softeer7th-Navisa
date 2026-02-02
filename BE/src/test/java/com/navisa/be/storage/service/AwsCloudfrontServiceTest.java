package com.navisa.be.storage.service;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.storage.exception.StorageDomainException;
import com.navisa.be.storage.model.enums.ImageSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwsCloudfrontServiceTest {

    @InjectMocks
    private AwsCloudfrontService awsCloudfrontService;

    @Mock
    private AwsS3StorageService awsS3StorageService;

    private static final String CLOUDFRONT_DOMAIN = "test-domain.cloudfront.net";

    @BeforeEach
    void setUp() {
        // instance 필드에 값을 주입
        ReflectionTestUtils.setField(awsCloudfrontService, "cloudfrontDomain", CLOUDFRONT_DOMAIN);
    }

    @Test
    @DisplayName("유효한 요청이면 CloudFront URL을 반환한다")
    void getImageUrl_shouldReturnUrl_whenValidRequest() {
        // given
        ImageSize size = ImageSize.MEDIUM;
        String objectKey = "agent-profile/origin/2024/01/01/uuid.jpg";
        String expectedFinalKey = "agent-profile/resize/medium/2024/01/01/uuid.webp";

        when(awsS3StorageService.convertToFinalKey(eq(size), eq(objectKey))).thenReturn(expectedFinalKey);

        // when
        String result = awsCloudfrontService.getImageUrl(size, objectKey);

        // then
        assertThat(result).isEqualTo("https://" + CLOUDFRONT_DOMAIN + "/" + expectedFinalKey);
    }

    @Test
    @DisplayName("objectKey가 null이면 StorageDomainException(INVALID_S3_OBJECT_KEY) 예외가 발생한다")
    void getImageUrl_shouldThrowException_whenObjectKeyIsNull() {
        // given
        ImageSize size = ImageSize.MEDIUM;
        String invalidObjectKey = null;

        // when & then
        assertThatThrownBy(() -> awsCloudfrontService.getImageUrl(size, invalidObjectKey))
                .isInstanceOf(StorageDomainException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.INVALID_S3_OBJECT_KEY);
    }

    @Test
    @DisplayName("objectKey가 빈 문자열이면 StorageDomainException(INVALID_S3_OBJECT_KEY) 예외가 발생한다")
    void getImageUrl_shouldThrowException_whenObjectKeyIsBlank() {
        // given
        ImageSize size = ImageSize.MEDIUM;
        String invalidObjectKey = "";

        // when & then
        assertThatThrownBy(() -> awsCloudfrontService.getImageUrl(size, invalidObjectKey))
                .isInstanceOf(StorageDomainException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.INVALID_S3_OBJECT_KEY);
    }

    @Test
    @DisplayName("size가 null이면 IllegalArgumentException 예외가 발생한다")
    void getImageUrl_shouldThrowException_whenSizeIsNull() {
        // given
        ImageSize size = null;
        String objectKey = "agent-profile/origin/2024/01/01/uuid.jpg";

        // when & then
        assertThatThrownBy(() -> awsCloudfrontService.getImageUrl(size, objectKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("size는 null일 수 없습니다.");
    }
}
