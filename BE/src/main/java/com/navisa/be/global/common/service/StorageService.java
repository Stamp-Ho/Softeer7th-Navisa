package com.navisa.be.global.common.service;

import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.global.common.dto.response.PresignedUrlResponse;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.common.model.enums.StorageLocation;
import com.navisa.be.global.infra.aws.CdnClient;
import com.navisa.be.global.infra.aws.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageClient storageClient;
    private final CdnClient cdnClient;

    public PresignedUrlResponse issuePresignedUrl(IssuedPresignedUrlRequest request) {
        return storageClient.issuePresignedUrl(request);
    }

    public String getImgUrl(ImageSize size, String objectKey, boolean isForeigner) {
        if (objectKey == null || objectKey.isBlank()
                || "null".equalsIgnoreCase(objectKey)
                || "undefined".equalsIgnoreCase(objectKey)) {
            return null;
        }

        // 행정사 경로라면 플래그와 상관없이 무조건 CloudFront로 보냄
        if (objectKey.startsWith(StorageLocation.AGENT_PROFILE_IMAGE.getDirectory())) {
            return cdnClient.getCloudfrontImageUrl(size, objectKey);
        }

        // 그 외의 경우에만 플래그에 따라 분기
        if (isForeigner) {
            return storageClient.getPresignedUrlFromS3(size, objectKey);
        }

        return cdnClient.getCloudfrontImageUrl(size, objectKey);
    }
}