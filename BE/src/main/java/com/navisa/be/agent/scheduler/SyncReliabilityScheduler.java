package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncReliabilityScheduler {

    private final RedisTemplate<String, Double> doubleRedisTemplate;
    private final AgentSpecializedJobSummaryRepository summaryRepository;
    private final TransactionTemplate transactionTemplate; // 트랜잭션 수동 제어를 위해 주입

    private static final String GET_AND_DELETE_LUA =
            "local val = redis.call('GET', KEYS[1]); " +
                    "if val then redis.call('DEL', KEYS[1]); end; " +
                    "return val";

    @Scheduled(cron = "0 10 4 * * *")
    @SchedulerLock(
            name = "syncRedisToDbLock",
            lockAtMostFor = "PT5M",
            lockAtLeastFor = "PT30S"
    )
    public void syncRedisToDb() {
        Set<String> keys = doubleRedisTemplate.keys("matching:sandbox:*");
        if (keys == null || keys.isEmpty()) return;

        log.info("[Sync Start] ShedLock 점유 성공. 동기화 건수: {}", keys.size());

        for (String key : keys) {
            try {
                // 스크립트로 원자적 조회 (데이터 경합 방지)
                Double weight = doubleRedisTemplate.execute(
                        new DefaultRedisScript<>(GET_AND_DELETE_LUA, Double.class),
                        Collections.singletonList(key)
                );

                if (weight != null && weight > 0) {
                    String[] parts = key.split(":");
                    UUID agentId = UUID.fromString(parts[2]);
                    Long jobCodeId = Long.parseLong(parts[3]);

                    // 개별 키 단위로 트랜잭션 적용
                    transactionTemplate.execute(status -> {
                        summaryRepository.updateReliabilityInDb(agentId, jobCodeId, weight);
                        return null;
                    });

                    log.debug("[Sync Success] Agent: {}, Job: {}, Weight: {}", agentId, jobCodeId, weight);
                }
            } catch (Exception e) {
                // 예외 발생 시 해당 키만 실패로 기록하고 다음 키로 진행
                log.error("[Sync Error] Key: {} - 실패 사유: {}", key, e.getMessage());
            }
        }
        log.info("[Sync End] 가중치 동기화 프로세스 완료.");
    }
}
