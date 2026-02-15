package com.navisa.be.global.infra.embedding;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.error.BaseException;

import java.util.List;

public record GeminiEmbeddingResponse(
        @JsonProperty("embeddings") List<EmbeddingData> embeddings,
        @JsonProperty("embedding") EmbeddingData embedding) {
    public record EmbeddingData(
            @JsonProperty("values") float[] values) {
    }

    public float[] getFirstVector() {
        if (embedding != null && embedding.values() != null) {
            return embedding.values();
        }
        if (embeddings != null && !embeddings.isEmpty() && embeddings.get(0).values() != null) {
            return embeddings.get(0).values();
        }
        throw new BaseException(ResponseStatus.NOT_FOUND_TEXT_EMBEDDING_RESULT);
    }
}
