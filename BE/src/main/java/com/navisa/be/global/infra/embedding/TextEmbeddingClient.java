package com.navisa.be.global.infra.embedding;

import java.util.Optional;
import java.util.UUID;

public interface TextEmbeddingClient {
    Optional<float[]> embedText(String text, GeminiEmbeddingRequestType type, UUID foreigner);
}
