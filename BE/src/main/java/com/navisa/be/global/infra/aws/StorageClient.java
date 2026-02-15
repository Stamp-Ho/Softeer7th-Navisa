package com.navisa.be.global.infra.aws;

import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.global.common.dto.response.PresignedUrlResponse;
import com.navisa.be.global.common.model.enums.ImageSize;

public interface StorageClient {

    PresignedUrlResponse issuePresignedUrl(IssuedPresignedUrlRequest request);
    String getPresignedUrlFromS3(ImageSize size, String objectKey);
}
