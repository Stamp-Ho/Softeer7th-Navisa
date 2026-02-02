package com.navisa.be.storage.service;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.storage.model.enums.ImageSize;
import com.navisa.be.storage.model.enums.StorageSupportingContentType;
import com.navisa.be.storage.model.enums.StorageLocation;
import com.navisa.be.storage.dto.request.IssuePresignedUrlRequest;
import com.navisa.be.storage.dto.response.IssuePresignedUrlResponse;
import com.navisa.be.storage.exception.StorageDomainException;
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
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3StorageService implements StorageService {

    private static final String WEBP_EXTENSION = ".webp";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final int PRESIGNED_URL_EXPIRE = 10;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public IssuePresignedUrlResponse issuePresignedUrl(IssuePresignedUrlRequest request) {
        String mimeType = request.fileMimeType();
        String fileUsage = request.fileUsage();

        if (!StorageSupportingContentType.supportsMimeType(mimeType)) {
            throw new StorageDomainException(ResponseStatus.STORAGE_UNSUPPORTED_CONTENT_TYPE);
        }
        if (!StorageLocation.supportsUsage(fileUsage)) {
            throw new StorageDomainException(ResponseStatus.STORAGE_UNSUPPORTED_USAGE);
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
            return new IssuePresignedUrlResponse(
                    presignedRequest.url().toString(),
                    objectKey
            );
        }
        catch (SdkClientException e) {
            // SDK 내부 오류
            log.error("AWS 자격 증명 또는 설정 오류: " + e.getMessage());
            throw new StorageDomainException(ResponseStatus.PRESIGNED_URL_GEN_FAILED);
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("Presigned URL 생성 중 예외 발생: " + e.getMessage());
            throw new StorageDomainException(ResponseStatus.PRESIGNED_URL_GEN_FAILED);
        }
    }

    public String getPresignedUrlFromS3(ImageSize size, String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new StorageDomainException(ResponseStatus.INVALID_S3_OBJECT_KEY);
        }

        // 1. 요청된 사이즈에 따른 최종 S3 Object Key 생성
        String finalKey = convertToFinalKey(size, objectKey);

        // 2. 행정사 프로필 사진인 경우 (예외 발생)
        if (objectKey.startsWith(StorageLocation.AGENT_PROFILE_IMAGE.getDirectory())) {
            throw new StorageDomainException(ResponseStatus.AGENT_PROFILE_PRESIGNED_REQUEST);
        }

        // 3. 외국인 증명사진인 경우 (보안을 위해 S3 Presigned URL 발행)
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
            throw new StorageDomainException(ResponseStatus.S3_RUNTIME_ERROR);
        } catch (SdkClientException e) {
            log.error("AWS SDK 클라이언트 설정 또는 네트워크 오류: {}", e.getMessage());
            throw new StorageDomainException(ResponseStatus.S3_CLIENT_ERROR);
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 알 수 없는 예외 발생: ", e);
            throw new StorageDomainException(ResponseStatus.SERVER_ERROR);
        }
    }

    /**
     * 원본 Key를 바탕으로 도메인과 사이즈 규칙에 맞는 최종 Key로 변환
     */
    protected String convertToFinalKey(ImageSize size, String objectKey) {
        // 원본이 아니면 경로 치환 및 확장자 변경 로직 수행
        if (size != ImageSize.ORIGIN) {
            StringBuilder sb = new StringBuilder(objectKey.length() + 10);
            sb.append(objectKey);

            // 1. 경로 치환: "origin" -> "resize/medium" 등
            // objectKey 구조가 "agent-profile/origin/..." 이므로 "origin" 부분을 바꿈
            String originPath = ImageSize.ORIGIN.getPath();
            int originIdx = sb.indexOf(originPath);
            if (originIdx != -1) {
                sb.replace(originIdx, originIdx + originPath.length(), size.getPath());
            }

            // 2. 확장자 변경: .jpg/.png -> .webp
            int lastDotIndex = sb.lastIndexOf(EXTENSION_SEPARATOR);
            if (lastDotIndex != -1) {
                sb.setLength(lastDotIndex);
            }
            sb.append(WEBP_EXTENSION);

            return sb.toString();
        }
        return objectKey;
    }

    private String generateObjectKey(StorageLocation location, String extension) {
        LocalDate now = LocalDate.now();
        String uuid = UUID.randomUUID().toString();

        String datePath = String.format(
                "%d/%02d/%02d",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth());

        return String.format("%s/" + ImageSize.ORIGIN.getPath() + "/%s/%s.%s", location.getDirectory(), datePath, uuid,
                extension);
    }
}
