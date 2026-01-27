package com.navisa.be.common.infrastructure.client;

import java.util.List;

public interface TextEmbeddingClient {
    List<Double> embedText(String text, GeminiEmbeddingRequestType type);
}
