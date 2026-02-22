package com.navisa.be.global.infra.redis;

import com.navisa.be.foreigner.service.ForeignerSimilarityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForeignerSimilarityBatchRestoreServiceTest {

    @InjectMocks
    private ForeignerSimilarityBatchRestoreService restoreService;

    @Mock
    private ForeignerSimilarityService similarityService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    private static final String STREAM_KEY = "gemini-embedding-retry-stream";
    private static final String GROUP = "gemini-retry-group";

    @BeforeEach
    void setUp() {
        // opsForStream() 호출 시 Mock StreamOperations 반환 설정
        lenient().when(redisTemplate.opsForStream()).thenReturn(streamOperations);
    }

    // 테스트용 MapRecord 생성을 위한 헬퍼 메서드
    private MapRecord<String, Object, Object> createRecord(UUID id, String text, String recordId) {
        return StreamRecords.newRecord()
                .in(STREAM_KEY)
                .ofMap(Map.<Object, Object>of("foreignerId", id.toString(), "text", text))
                .withId(RecordId.of(recordId));
    }

    @Test
    @DisplayName("배치가 모두 성공적으로 처리된다")
    void process_shouldSucceed() {
        // given
        UUID id1 = UUID.randomUUID();
        String text1 = "text1";
        MapRecord<String, Object, Object> record1 = createRecord(id1, text1, "1-0");

        List<MapRecord<String, Object, Object>> batch = List.of(record1);
        Map<UUID, String> idToTextMap = Map.of(id1, text1);
        Map<String, float[]> embeddings = Map.of(text1, new float[] { 0.1f });

        // when
        List<MapRecord<String, Object, Object>> failed = restoreService.process(batch, idToTextMap, embeddings);

        // then
        assertThat(failed).isEmpty();
        verify(similarityService).processSimilarity(eq(id1), any());
        verify(streamOperations).acknowledge(STREAM_KEY, GROUP, record1.getId());
    }

    @Test
    @DisplayName("배치 중에서 하나가 저장 중에 예외가 발생하면 실패로 하나가 반환된다")
    void process_shouldReturnOne_whenException() {
        // given
        UUID id1 = UUID.randomUUID();
        String text1 = "fail_text";
        MapRecord<String, Object, Object> record1 = createRecord(id1, text1, "2-0");

        List<MapRecord<String, Object, Object>> batch = List.of(record1);
        Map<UUID, String> idToTextMap = Map.of(id1, text1);
        Map<String, float[]> embeddings = Map.of(text1, new float[] { 0.1f });

        // DB 저장 시 예외 발생 모킹
        doThrow(new RuntimeException("DB Error")).when(similarityService).processSimilarity(eq(id1), any());

        // when
        List<MapRecord<String, Object, Object>> failed = restoreService.process(batch, idToTextMap, embeddings);

        // then
        assertThat(failed).hasSize(1);
        assertThat(failed.get(0).getId()).isEqualTo(record1.getId());

        // 실패했으므로 ACK는 호출되지 않아야 함
        verify(streamOperations, never()).acknowledge(anyString(), anyString(), any(RecordId.class));
    }

    @Test
    @DisplayName("배치 중에서 하나의 임베딩 결과가 비어 있으면 실패로 하나가 반환된다")
    void process_shouldReturnOne_whenNullEmbedding() {
        // given
        UUID id1 = UUID.randomUUID();
        String text1 = "no_embedding_text";
        MapRecord<String, Object, Object> record1 = createRecord(id1, text1, "3-0");

        List<MapRecord<String, Object, Object>> batch = List.of(record1);
        Map<UUID, String> idToTextMap = Map.of(id1, text1);
        Map<String, float[]> embeddings = Collections.emptyMap(); // 임베딩 결과가 없음

        // when
        List<MapRecord<String, Object, Object>> failed = restoreService.process(batch, idToTextMap, embeddings);

        // then
        assertThat(failed).hasSize(1);
        assertThat(failed.get(0).getValue().get("text")).isEqualTo(text1);
        verify(similarityService, never()).processSimilarity(any(), any());
    }
}