package com.navisa.be.global.common.dto.projection;

public record JobCodeSimilarityProjection(
        Long id,
        String name,
        Double similarity // 계산된 유사도 값
) {
}
