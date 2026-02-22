package com.navisa.be.global.infra.redis;

import com.navisa.be.global.infra.embedding.GeminiEmbeddingRequestType;
import com.navisa.be.global.infra.embedding.GeminiTextEmbeddingClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForeignerSimilarityBatchRetryConsumer implements InitializingBean {

    private static final int MAX_RETRY_COUNT = 360; // 최대 재시도 횟수
    private static final int BATCH_SIZE = 100; // 한 번에 처리할 최대 배치 크기
    private static final String DLQ_STREAM_KEY = "gemini-embedding-dlq";
    private static final String RETRY_STREAM_KEY = "gemini-embedding-retry-stream";
    private static final String CONSUMER_GROUP = "gemini-retry-group";

    @Value("${redis.stream.consumer-name}")
    private String consumerName;

    private final GeminiTextEmbeddingClient geminiClient;
    private final ForeignerSimilarityBatchRestoreService foreignerSimilarityRestoreService;
    private final StringRedisTemplate redisTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public void afterPropertiesSet() {
        prepareStream();
    }

    private void prepareStream() {
        try {
            // createGroup 호출 시 자동으로 스트림이 없으면 생성(MKSTREAM)하는 기능 포함
            redisTemplate.opsForStream().createGroup(RETRY_STREAM_KEY, ReadOffset.from("0-0"), CONSUMER_GROUP);
            log.info("Redis Stream Consumer Group '{}' 생성 완료", CONSUMER_GROUP);
        } catch (RedisSystemException e) {
            // RootCause가 RedisBusyException(이미 존재)인지 확인
            if (e.getRootCause() instanceof io.lettuce.core.RedisBusyException) {
                log.info("Redis Stream Consumer Group '{}'이 이미 존재합니다.", CONSUMER_GROUP);
            } else {
                throw new IllegalStateException("Stream 준비 중 예상치 못한 에러 발생", e);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Stream 준비 중 예상치 못한 에러 발생", e);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void processBatch() {
        // Redis Stream에서 직접 배치 읽기
        StreamOffset<String> streamOffset = StreamOffset.create(RETRY_STREAM_KEY, ReadOffset.lastConsumed());
        List<MapRecord<String, Object, Object>> batch = redisTemplate.opsForStream().read(
                Consumer.from(CONSUMER_GROUP, consumerName),
                StreamReadOptions.empty().count(BATCH_SIZE),
                streamOffset
        );

        // 비어 있으면 리턴
        if (batch == null || batch.isEmpty()) {
            return;
        }

        log.info("Batch processing started. Batch size: {}", batch.size());

        try {
            // 데이터 추출 및 배치 API 호출
            Map<UUID, String> idToTextMap = extractData(batch);
            Map<String, float[]> embeddings = geminiClient.embedTextBatch(
                    new ArrayList<>(idToTextMap.values()), GeminiEmbeddingRequestType.QUERY
            );
            
            if (embeddings.isEmpty()) {
                handleFailedRecords(batch);
                return;
            }

            List<MapRecord<String, Object, Object>> failed = foreignerSimilarityRestoreService.process(batch, idToTextMap, embeddings);

            // 저장에 실패한 요청 처리 (재시도 or DLQ)
            if (!failed.isEmpty()) {
                handleFailedRecords(failed);
            }
        } catch (Exception e) {
            log.error("Batch processing failed completely. Re-queueing all.", e);
            handleFailedRecords(batch);
        }
    }

    private void handleFailedRecords(List<MapRecord<String, Object, Object>> failed) {

        for (var record : failed) {
            // 메시지에서 현재 재시도 횟수 추출 (없으면 0)
            int currentRetry;
            try {
                Object retryCountObj = record.getValue().get("retryCount");
                String retryCountStr = retryCountObj != null ? retryCountObj.toString() : "0";
                currentRetry = Integer.parseInt(retryCountStr);
            } catch (NumberFormatException e) {
                log.error("Failed record 처리 중 오류. foreignerId: {}, Error: {}", record.getValue().get("foreignerId"), e.getMessage());

                redisTemplate.opsForStream().add(DLQ_STREAM_KEY, record.getValue());
                redisTemplate.opsForStream().acknowledge(RETRY_STREAM_KEY, CONSUMER_GROUP, record.getId());

                continue;
            }

            if (currentRetry < MAX_RETRY_COUNT) {
                Map<Object, Object> newBody = new HashMap<>(record.getValue());
                newBody.put("retryCount", String.valueOf(currentRetry + 1));

                // Redis 스트림에 새 재시도 메시지 추가
                redisTemplate.opsForStream().add(RETRY_STREAM_KEY, newBody);

                // 원본 스트림에서 ACK 처리하여 Pending 상태 해제
                redisTemplate.opsForStream().acknowledge(RETRY_STREAM_KEY, CONSUMER_GROUP, record.getId());
            } else {
                // 시도 횟수 초과 시 DLQ로 이동
                log.error("Dead Letter: Max retry reached for foreignerId: {}. Moving to DLQ.", record.getValue().get("foreignerId"));

                // DLQ 스트림에 저장
                redisTemplate.opsForStream().add(DLQ_STREAM_KEY, record.getValue());

                // 원본 스트림에서 ACK 처리하여 Pending 상태 해제
                redisTemplate.opsForStream().acknowledge(RETRY_STREAM_KEY, CONSUMER_GROUP, record.getId());
            }
        }
    }

    private Map<UUID, String> extractData(List<MapRecord<String, Object, Object>> batch) {
        return batch.stream().collect(Collectors.toMap(
                r -> UUID.fromString((String) r.getValue().get("foreignerId")),
                r -> (String) r.getValue().get("text"),
                (existing, replacement) -> replacement // 중복 시 최신 데이터(replacement)로 덮어쓰기
        ));
    }
}
