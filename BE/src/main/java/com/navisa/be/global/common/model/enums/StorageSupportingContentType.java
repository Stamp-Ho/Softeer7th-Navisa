package com.navisa.be.global.common.model.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum StorageSupportingContentType {

    JPG("image/jpg", "jpg"),
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png");

    private final String mimeType;
    private final String extension;

    private static final Map<String, StorageSupportingContentType> MIME_TYPE_CACHE =
            Collections.unmodifiableMap(
                    Stream.of(values()).collect(Collectors.toMap(
                            type -> type.mimeType.toLowerCase(),
                            type -> type,
                            (existing, replacement) -> existing // image/jpeg 중복 시 JPG를 우선함
                    ))
            );

    StorageSupportingContentType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static boolean supportsMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return false;
        }
        return MIME_TYPE_CACHE.containsKey(mimeType.toLowerCase());
    }

    public static StorageSupportingContentType from(String mimeType) {
        if (mimeType == null) {
            throw new IllegalArgumentException("mimeType이 null일 수 업습니다");
        }

        StorageSupportingContentType contentType = MIME_TYPE_CACHE.get(mimeType.toLowerCase());
        if(contentType == null){
            throw new IllegalArgumentException("지원하지 안는 타입입니다");
        }
        return contentType;
    }
}
