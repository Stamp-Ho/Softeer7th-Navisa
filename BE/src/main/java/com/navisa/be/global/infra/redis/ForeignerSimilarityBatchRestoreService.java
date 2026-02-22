package com.navisa.be.global.infra.redis;

import com.navisa.be.foreigner.service.ForeignerSimilarityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ForeignerSimilarityBatchRestoreService {

    private static final String RETRY_STREAM_KEY = "gemini-embedding-retry-stream";
    private static final String CONSUMER_GROUP = "gemini-retry-group";
    private final ForeignerSimilarityService similarityService;
    private final StringRedisTemplate redisTemplate;

    public List<MapRecord<String, Object, Object>> process(List<MapRecord<String, Object, Object>> batch,
            Map<UUID, String> idToTextMap,
            Map<String, float[]> embeddings) {
        List<MapRecord<String, Object, Object>> failed = new ArrayList<>();

        Map<UUID, List<MapRecord<String, Object, Object>>> recordsByFid = batch.stream()
                .collect(Collectors.groupingBy(r -> UUID.fromString((String) r.getValue().get("foreignerId"))));

        for (Map.Entry<UUID, List<MapRecord<String, Object, Object>>> entry : recordsByFid.entrySet()) {
            UUID fId = entry.getKey();
            List<MapRecord<String, Object, Object>> records = entry.getValue();

            String text = idToTextMap.get(fId);
            float[] vector = embeddings.get(text);

            if (vector != null && vector.length > 0) {
                try {
                    similarityService.processSimilarity(fId, vector);
                    for (var record : records) {
                        redisTemplate.opsForStream().acknowledge(RETRY_STREAM_KEY, CONSUMER_GROUP, record.getId());
                    }
                } catch (Exception e) {
                    log.error("Failed to process similarity for: {}", fId, e);
                    failed.addAll(records);
                }
            } else {
                failed.addAll(records);
            }
        }
        return failed;
    }
}
