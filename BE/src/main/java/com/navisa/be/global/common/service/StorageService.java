package com.navisa.be.global.common.service;

import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.global.common.dto.response.PresignedUrlResponse;
import com.navisa.be.global.common.model.enums.ImageSize;
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
        if (isForeigner) {
            return storageClient.getPresignedUrlFromS3(size, objectKey);
        }

        return cdnClient.getCloudfrontImageUrl(size, objectKey);
    }
}