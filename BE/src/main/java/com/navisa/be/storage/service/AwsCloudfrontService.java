package com.navisa.be.storage.service;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.storage.exception.StorageDomainException;
import com.navisa.be.storage.model.enums.ImageSize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AwsCloudfrontService implements CdnService {

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    private final AwsS3StorageService awsS3StorageService;

    public AwsCloudfrontService(AwsS3StorageService awsS3StorageService) {
        this.awsS3StorageService = awsS3StorageService;
    }

    @Override
    public String getImageUrl(ImageSize size, String objectKey) {
        if (objectKey == null || objectKey.isBlank())
            throw new StorageDomainException(ResponseStatus.INVALID_S3_OBJECT_KEY);

        if (size == null)
            throw new IllegalArgumentException("size는 null일 수 없습니다.");

        String resizeFinalKey = awsS3StorageService.convertToFinalKey(size, objectKey);

        return String.format("https://%s/%s", cloudfrontDomain, resizeFinalKey);
    }
}
