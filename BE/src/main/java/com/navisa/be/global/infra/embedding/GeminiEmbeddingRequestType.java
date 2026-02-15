package com.navisa.be.global.infra.embedding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeminiEmbeddingRequestType {
    DOCUMENT("RETRIEVAL_DOCUMENT"), // DB 저장용 (문서)
    QUERY("RETRIEVAL_QUERY");       // 검색용 (질의)

    private final String value;
}
