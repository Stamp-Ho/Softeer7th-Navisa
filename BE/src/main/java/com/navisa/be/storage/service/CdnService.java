package com.navisa.be.storage.service;

import com.navisa.be.storage.model.enums.ImageSize;

public interface CdnService {
    String getImageUrl(ImageSize size, String objectKey);
}
