package com.navisa.be.global.infra.aws;

import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.common.model.enums.StorageSupportingContentType;
import com.navisa.be.global.common.model.enums.StorageLocation;
import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.global.common.dto.response.PresignedUrlResponse;
import com.navisa.be.global.common.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3StorageClient implements StorageClient {

    private static final int PRESIGNED_URL_EXPIRE = 10;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public PresignedUrlResponse issuePresignedUrl(IssuedPresignedUrlRequest request) {
        String mimeType = request.fileMimeType();
        String fileUsage = request.fileUsage();

        if (!StorageSupportingContentType.supportsMimeType(mimeType)) {
            throw new StorageException(ResponseStatus.STORAGE_UNSUPPORTED_CONTENT_TYPE);
        }
        if (!StorageLocation.supportsUsage(fileUsage)) {
            throw new StorageException(ResponseStatus.STORAGE_UNSUPPORTED_USAGE);
        }

        StorageSupportingContentType supportingContentType = StorageSupportingContentType.from(mimeType);
        StorageLocation supportingLocation = StorageLocation.from(fileUsage);
        String objectKey = generateObjectKey(supportingLocation, supportingContentType.getExtension());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(supportingContentType.getMimeType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRE))
                .putObjectRequest(putObjectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return new PresignedUrlResponse(
                    presignedRequest.url().toString(),
                    objectKey
            );
        }
        catch (SdkClientException e) {
            // SDK 내부 오류
            log.error("AWS 자격 증명 또는 설정 오류: " + e.getMessage());
            throw new StorageException(ResponseStatus.PRESIGNED_URL_GEN_FAILED);
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("Presigned URL 생성 중 예외 발생: " + e.getMessage());
            throw new StorageException(ResponseStatus.PRESIGNED_URL_GEN_FAILED);
        }
    }

    public String getPresignedUrlFromS3(ImageSize size, String objectKey) {
        // 1. objectKey가 비어있으면 예외 대신 null 반환
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        if ("null".equalsIgnoreCase(objectKey) || "undefined".equalsIgnoreCase(objectKey)) {
            return null;
        }

        // 2. 행정사 프로필 사진 경로인 경우
        if (objectKey.startsWith(StorageLocation.AGENT_PROFILE_IMAGE.getDirectory())) {
            log.warn("행정사 프로필은 Presigned URL 발행 대상이 아닙니다. Key: {}", objectKey);
            return null;
        }

        // 3. 최종 S3 Object Key 생성 및 Presigned URL 발행
        String finalKey = ImageSize.convertToFinalKey(size, objectKey);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(finalKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRE))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();

        } catch (S3Exception e) {
            log.error("S3 서비스 오류 발생 (Status Code: {}): {}", e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new StorageException(ResponseStatus.S3_RUNTIME_ERROR);
        } catch (SdkClientException e) {
            log.error("AWS SDK 클라이언트 설정 또는 네트워크 오류: {}", e.getMessage());
            throw new StorageException(ResponseStatus.S3_CLIENT_ERROR);
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 알 수 없는 예외 발생: ", e);
            throw new StorageException(ResponseStatus.SERVER_ERROR);
        }
    }

    private String generateObjectKey(StorageLocation location, String extension) {
        String uuid = UUID.randomUUID().toString();

        return String.format("%s/" + ImageSize.ORIGIN.getPath() + "/%s.%s", location.getDirectory(), uuid,
                extension);
    }
}
