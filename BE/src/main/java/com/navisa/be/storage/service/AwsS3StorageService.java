package com.navisa.be.storage.service;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.storage.constant.StorageSupportingContentType;
import com.navisa.be.storage.constant.StorageLocation;
import com.navisa.be.storage.dto.request.IssuePresignedUrlRequest;
import com.navisa.be.storage.dto.response.IssuePresignedUrlResponse;
import com.navisa.be.storage.exception.StorageDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3StorageService implements StorageService {

    private static final Duration PRESIGNED_URL_EXPIRE = Duration.ofMinutes(10);

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public IssuePresignedUrlResponse issuePresignedUrl(IssuePresignedUrlRequest request) {
        String mimeType = request.fileMimeType();
        String fileUsage = request.fileUsage();

        if(!StorageSupportingContentType.supportsMimeType(mimeType)){
            throw new StorageDomainException(ResponseStatus.STORAGE_UNSUPPORTED_CONTENT_TYPE);
        }
        if(!StorageLocation.supportsUsage(fileUsage)){
            throw new StorageDomainException(ResponseStatus.STORAGE_UNSUPPORTED_USAGE);
        }

        StorageSupportingContentType supportingContentType = StorageSupportingContentType.from(mimeType);
        StorageLocation supportingLocation = StorageLocation.from(fileUsage);
        String objectKey = generateObjectKey(supportingContentType.getExtension(), supportingLocation.getDirectory());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(supportingContentType.getMimeType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(PRESIGNED_URL_EXPIRE)
                        .putObjectRequest(putObjectRequest)
                        .build();

        try{
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

    private String generateObjectKey(String extension, String directory) {
        LocalDate now = LocalDate.now();
        String uuid = UUID.randomUUID().toString();

        String datePath = String.format(
                "%d/%02d/%02d",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth()
        );

        return String.format("origin/%s/%s/%s.%s", directory, datePath, uuid, extension);
    }
}
