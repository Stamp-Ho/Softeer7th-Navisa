package com.navisa.be.common.infrastructure.client;

import com.navisa.be.global.infra.embedding.GeminiEmbeddingRequestType;
import com.navisa.be.global.infra.embedding.GeminiTextEmbeddingClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

import com.navisa.be.support.IntegrationTestSupport;

class GeminiTextEmbeddingClientCircuitBreakerTest extends IntegrationTestSupport {

    public static final String GEMINI_EMBEDDING_RETRY_STREAM = "gemini-embedding-retry-stream";
    public static final String GEMINI_EMBEDDING = "geminiEmbedding";
    public static final String GEMINI_BATCH_EMBEDDING = "geminiBatchEmbedding";

    @Autowired
    private GeminiTextEmbeddingClient geminiClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        ReflectionTestUtils.setField(geminiClient, "webClient",
                WebClient.builder()
                        .baseUrl(baseUrl)
                        .build()

        );

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(GEMINI_EMBEDDING);
        cb.reset();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("CircuitBreaker: 실패율 50% 초과 시 OPEN 상태로 전환된다")
    void circuitBreaker_OpensOnFailureRate() {
        // given
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(GEMINI_EMBEDDING);

        // when 10회 연속 실패시 failureRate가 50프로
        IntStream.range(0, 10).forEach(i -> {
            mockWebServer.enqueue(new MockResponse().setResponseCode(500));
            try {
                geminiClient.embedText("test", GeminiEmbeddingRequestType.DOCUMENT, UUID.randomUUID());
            } catch (Exception ignored) {
            }
        });

        // then
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("CircuitBreaker: 느린 호출 비율 50% 초과 시 OPEN 상태로 전환된다")
    void circuitBreaker_OpensOnSlowCalls() {
        // given
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(GEMINI_EMBEDDING);

        // when
        // 5번은 빠른 응답, 5번은 900ms 지연 응답.
        IntStream.range(0, 5).forEach(i -> {
            mockWebServer.enqueue(new MockResponse()
                    .setBody("{\"embedding\": {\"values\": []}}")
                    .addHeader("Content-Type", "application/json"));
            geminiClient.embedText("test", GeminiEmbeddingRequestType.DOCUMENT, UUID.randomUUID());
        });

        IntStream.range(0, 5).forEach(i -> {
            mockWebServer.enqueue(new MockResponse()
                    .setBody("{\"embedding\": {\"values\": []}}")
                    .addHeader("Content-Type", "application/json")
                    .setBodyDelay(900, TimeUnit.MILLISECONDS));
            geminiClient.embedText("test", GeminiEmbeddingRequestType.DOCUMENT, UUID.randomUUID());
        });

        // then
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("CircuitBreaker: OPEN 상태일 때 호출하면 즉시 fallback이 실행된다")
    void circuitBreaker_FallbackExecutionWhenOpen() {
        // given

        // 강제로 서킷 브레이커를 OPEN 상태로 전환
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(GEMINI_EMBEDDING);
        cb.transitionToOpenState();

        UUID foreignerId = UUID.randomUUID();
        String text = "fallback test text";

        // when

        // 외부 API가 호출되지 않고 Optional.empty()가 반환되어어야 함
        Optional<float[]> result = geminiClient.embedText(text, GeminiEmbeddingRequestType.DOCUMENT, foreignerId);

        // then
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .read(StreamOffset.fromStart(GEMINI_EMBEDDING_RETRY_STREAM));

        assertThat(result).isEmpty();

        // Redis Stream에 데이터가 쌓였는지 확인
        assertThat(records).isNotEmpty();
        Map<Object, Object> streamData = records.get(records.size() - 1).getValue();
        assertThat(streamData.get("foreignerId")).isEqualTo(foreignerId.toString());
        assertThat(streamData.get("text")).isEqualTo(text);
    }

    @Test
    @DisplayName("CircuitBreaker: 배치용 서킷브레이커가 OPEN 상태일 때 즉시 fallbackBatch가 실행된다")
    void circuitBreaker_BatchFallbackWhenOpen() {
        // given
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(GEMINI_BATCH_EMBEDDING);
        cb.transitionToOpenState();

        // when
        // 배치 호출
        Map<String, float[]> result = geminiClient.embedTextBatch(List.of("text1", "text2"), GeminiEmbeddingRequestType.DOCUMENT);

        // then
        assertThat(result).isEmpty();
        assertThat(mockWebServer.getRequestCount()).isZero(); // 서버에 요청조차 가지 않음
    }
}
