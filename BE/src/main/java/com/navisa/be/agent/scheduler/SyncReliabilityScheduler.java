package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncReliabilityScheduler {

    private final RedisTemplate<String, Double> doubleRedisTemplate;
    private final AgentSpecializedJobSummaryRepository summaryRepository;

    private static final DefaultRedisScript<Double> GET_AND_DELETE_SCRIPT =
            new DefaultRedisScript<>(
                    "local val = redis.call('GET', KEYS[1]); " +
                            "if val then redis.call('DEL', KEYS[1]); end; " +
                            "return val",
                    Double.class
            );

    @Scheduled(cron = "0 10 4 * * *", zone = "Asia/Seoul")
    @SchedulerLock(
            name = "syncRedisToDbLock",
            lockAtMostFor = "PT10M",
            lockAtLeastFor = "PT30S"
    )
    public void syncRedisToDb() {
        ScanOptions options = ScanOptions.scanOptions()
                .match("matching:sandbox:*")
                .count(100) // 한 번에 100개씩 끊어서 조회
                .build();

        List<SyncData> syncList = new ArrayList<>();

        try (Cursor<String> cursor = doubleRedisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();

                Double weight = doubleRedisTemplate.execute(
                        GET_AND_DELETE_SCRIPT,
                        Collections.singletonList(key)
                );

                if (weight != null && weight > 0) {
                    SyncData data = parseKeyAndWeight(key, weight);
                    if (data != null) {
                        syncList.add(data);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[Sync Error] Redis Scan 중 에러 발생", e);
        }

        if (syncList.isEmpty()) return;

        log.info("[Sync Start] 동기화 대상 건수: {}", syncList.size());

        // 루프 밖에서 한 번에 Batch Update
        try {
            processBatchUpdate(syncList);
            log.info("[Sync End] 가중치 동기화 프로세스 완료.");
        } catch (Exception e) {
            log.error("[Sync Fatal Error] DB 업데이트 실패", e);
        }
    }

    private void processBatchUpdate(List<SyncData> syncList) {
        for (SyncData data : syncList) {
            summaryRepository.updateReliabilityInDb(data.agentId, data.jobCodeId, data.weight);
        }
    }

    private SyncData parseKeyAndWeight(String key, Double weight) {
        String[] parts = key.split(":");
        if (parts.length < 4) {
            log.warn("[Sync Warning] 예상치 못한 키 형식: {}", key);
            return null;
        }
        return new SyncData(UUID.fromString(parts[2]), Long.parseLong(parts[3]), weight);
    }

    private record SyncData(UUID agentId, Long jobCodeId, Double weight) {
    }
}
