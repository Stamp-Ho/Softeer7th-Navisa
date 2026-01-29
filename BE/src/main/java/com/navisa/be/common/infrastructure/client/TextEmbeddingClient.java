package com.navisa.be.common.infrastructure.client;

public interface TextEmbeddingClient {
    float[] embedText(String text, GeminiEmbeddingRequestType type);
}
