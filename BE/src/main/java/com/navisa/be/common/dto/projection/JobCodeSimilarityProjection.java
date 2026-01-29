package com.navisa.be.common.dto.projection;

public interface JobCodeSimilarityProjection {
    Long getId();
    String getName();
    Double getSimilarity(); // 계산된 유사도 값
}
