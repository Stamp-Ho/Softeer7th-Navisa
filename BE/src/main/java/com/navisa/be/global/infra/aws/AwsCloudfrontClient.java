package com.navisa.be.global.infra.aws;

import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.common.exception.StorageException;
import com.navisa.be.global.common.model.enums.ImageSize;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwsCloudfrontClient implements CdnClient {

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    @Override
    public String getCloudfrontImageUrl(ImageSize size, String objectKey) {
        if (objectKey == null || objectKey.isBlank())
            throw new StorageException(ResponseStatus.INVALID_S3_OBJECT_KEY);

        if (size == null)
            throw new IllegalArgumentException("size는 null일 수 없습니다.");

        String resizeFinalKey = ImageSize.convertToFinalKey(size, objectKey);

        return String.format("https://%s/%s", cloudfrontDomain, resizeFinalKey);
    }
}
