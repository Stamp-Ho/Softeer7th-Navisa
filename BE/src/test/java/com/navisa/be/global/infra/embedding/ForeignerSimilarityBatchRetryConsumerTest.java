package com.navisa.be.common.infrastructure.client;

import com.navisa.be.global.infra.embedding.GeminiEmbeddingRequestType;
import com.navisa.be.global.infra.embedding.GeminiTextEmbeddingClient;
import com.navisa.be.global.infra.redis.ForeignerSimilarityBatchRestoreService;
import com.navisa.be.global.infra.redis.ForeignerSimilarityBatchRetryConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ForeignerSimilarityBatchRetryConsumerTest {

    @Mock
    private GeminiTextEmbeddingClient geminiClient;

    @Mock
    private ForeignerSimilarityBatchRestoreService foreignerSimilarityRestoreService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    @InjectMocks
    private ForeignerSimilarityBatchRetryConsumer consumer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(consumer, "consumerName", "test-consumer");
    }

    @Test
    @DisplayName("버퍼가 비어있으면 아무 동작도 하지 않는다")
    void processBatch_shouldDoNothing_whenEmptyBuffer() {
        // given
        given(redisTemplate.opsForStream()).willReturn(streamOperations);
        given(streamOperations.read(any(Consumer.class), any(), any())).willReturn(Collections.emptyList());

        // when
        consumer.processBatch();

        // then
        verify(geminiClient, never()).embedTextBatch(anyList(), any());
        verify(foreignerSimilarityRestoreService, never()).process(anyList(), any(), any());
    }

    @Test
    @DisplayName("임베딩에 성공하고 복원에 성공하면 버퍼에 실패 요청이 들어가지 않는다")
    void processBatch_shouldSuccess() {
        // given
        String streamKey = "gemini-embedding-retry-stream";
        UUID foreignerId = UUID.randomUUID();
        String text = "test text";
        MapRecord<String, Object, Object> record = StreamRecords.newRecord()
                .in(streamKey)
                .ofMap(Map.<Object, Object>of("foreignerId", foreignerId.toString(), "text", text))
                .withId(RecordId.of("1637827382738-0"));

        given(redisTemplate.opsForStream()).willReturn(streamOperations);
        given(streamOperations.read(any(Consumer.class), any(), any())).willReturn(List.of(record));

        given(geminiClient.embedTextBatch(anyList(), eq(GeminiEmbeddingRequestType.QUERY)))
                .willReturn(Map.of(text, new float[] { 0.1f }));

        given(foreignerSimilarityRestoreService.process(anyList(), any(), any())).willReturn(Collections.emptyList()); // 실패
                                                                                                                       // 없음

        // when
        consumer.processBatch();

        // then
        verify(geminiClient).embedTextBatch(anyList(), eq(GeminiEmbeddingRequestType.QUERY));
        verify(foreignerSimilarityRestoreService).process(anyList(), any(), any());
    }

    @Test
    @DisplayName("재시도 횟수가 남았으면 카운트 증가 후 Redis 스트림에 다시 들어간다")
    void processBatch_shouldRetry() {
        // given
        String streamKey = "gemini-embedding-retry-stream";
        MapRecord<String, Object, Object> record = StreamRecords.newRecord()
                .in(streamKey)
                .ofMap(Map.<Object, Object>of("foreignerId", UUID.randomUUID().toString(), "text", "text", "retryCount",
                        "1"))
                .withId(RecordId.of("1637827382738-0"));

        given(redisTemplate.opsForStream()).willReturn(streamOperations);
        given(streamOperations.read(any(Consumer.class), any(), any())).willReturn(List.of(record));

        // 임베딩에 성공
        given(geminiClient.embedTextBatch(anyList(), any())).willReturn(Map.of("text", new float[512]));

        // Service가 실패한 레코드를 그대로 반환
        given(foreignerSimilarityRestoreService.process(anyList(), any(), any()))
                .willReturn(List.of(record));

        // when
        consumer.processBatch();

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<Object, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(streamOperations).add(eq(streamKey), captor.capture());

        Map<Object, Object> newBody = captor.getValue();
        assertThat(newBody.get("retryCount")).isEqualTo("2");

        verify(streamOperations).acknowledge(eq(streamKey), eq("gemini-retry-group"), eq(record.getId()));
    }

    @Test
    @DisplayName("재시도 횟수를 초과하면 DLQ로 이동하고 ACK한다")
    void processBatch_shouldSendToDLQ() {
        // given
        String streamKey = "gemini-embedding-retry-stream";
        String retryCount = "1800";
        MapRecord<String, Object, Object> record = StreamRecords.newRecord()
                .in(streamKey)
                .ofMap(Map.<Object, Object>of("foreignerId", UUID.randomUUID().toString(), "text", "text", "retryCount",
                        retryCount))
                .withId(RecordId.of("1637827382738-0"));

        given(redisTemplate.opsForStream()).willReturn(streamOperations);
        given(streamOperations.read(any(Consumer.class), any(), any())).willReturn(List.of(record));

        // 임베딩 최종 실패
        given(geminiClient.embedTextBatch(anyList(), any())).willReturn(Map.of());

        // when
        consumer.processBatch();

        // then
        verify(streamOperations).add(eq("gemini-embedding-dlq"), any(Map.class));
        verify(streamOperations).acknowledge(eq(streamKey), eq("gemini-retry-group"), eq(record.getId()));
    }

    @Test
    @DisplayName("예외 발생 시 모든 실패 요청이 Redis 스트림에 저장되고 ACK된다")
    void processBatch_shouldThrowException() {
        // given
        MapRecord<String, Object, Object> record = StreamRecords.newRecord()
                .in("gemini-embedding-retry-stream")
                .ofMap(Map.<Object, Object>of("foreignerId", UUID.randomUUID().toString(), "text", "text"))
                .withId(RecordId.of("1637827382738-0"));

        given(redisTemplate.opsForStream()).willReturn(streamOperations);
        given(streamOperations.read(any(Consumer.class), any(), any()))
                .willReturn(List.of(record));

        given(geminiClient.embedTextBatch(anyList(), any())).willThrow(new RuntimeException("API Error"));

        // when
        consumer.processBatch();

        // then
        verify(streamOperations).add(eq("gemini-embedding-retry-stream"), any(Map.class));
        verify(streamOperations).acknowledge(eq("gemini-embedding-retry-stream"), eq("gemini-retry-group"), eq(record.getId()));
    }
}
