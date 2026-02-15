package com.navisa.be.global.infra.aws;

import com.navisa.be.global.common.model.enums.ImageSize;

public interface CdnClient {

    String getCloudfrontImageUrl(ImageSize size, String objectKey);
}
