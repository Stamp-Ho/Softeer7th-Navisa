package com.navisa.be.global.common.model.enums;

import lombok.Getter;

@Getter
public enum ImageSize {
    ORIGIN("origin"),
    MEDIUM("resize/medium"),
    SMALL("resize/small");

    private final String path;

    private static final String WEBP_EXTENSION = ".webp";
    private static  final String EXTENSION_SEPARATOR = ".";
    private static  final int PRESIGNED_URL_EXPIRE = 10;

    ImageSize(String path) { this.path = path; }

    /**
     * 원본 Key를 바탕으로 도메인과 사이즈 규칙에 맞는 최종 Key로 변환
     */
    public static String convertToFinalKey(ImageSize size, String objectKey) {
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
}
