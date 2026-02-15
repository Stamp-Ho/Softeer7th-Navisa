package com.navisa.be.global.infra.embedding;

public interface TextEmbeddingClient {
    float[] embedText(String text, GeminiEmbeddingRequestType type);
}
