package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncReliabilitySchedulerTest {

    @InjectMocks
    private SyncReliabilityScheduler syncReliabilityScheduler;

    @Mock
    private RedisTemplate<String, Double> doubleRedisTemplate;

    @Mock
    private AgentSpecializedJobSummaryRepository summaryRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("Redis의 가중치 데이터를 DB로 성공적으로 동기화한다")
    void syncRedisToDb_Success() {
        // given
        String key = "matching:sandbox:" + UUID.randomUUID() + ":1";
        Set<String> keys = Collections.singleton(key);

        given(doubleRedisTemplate.keys("matching:sandbox:*")).willReturn(keys);
        // Lua 스크립트 실행 결과로 가중치 10.5 반환
        given(doubleRedisTemplate.execute(any(RedisScript.class), anyList())).willReturn(10.5);

        // TransactionTemplate 실행 시 람다 내부 로직 강제 호출 설정
        given(transactionTemplate.execute(any())).willAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        verify(summaryRepository, times(1)).updateReliabilityInDb(any(UUID.class), anyLong(), eq(10.5));
        verify(doubleRedisTemplate, never()).delete(anyString());
        verify(doubleRedisTemplate, times(1)).execute(any(RedisScript.class), eq(Collections.singletonList(key)));
    }

    @Test
    @DisplayName("동기화 과정 중 개별 키에서 예외가 발생해도 다음 키 처리를 계속한다")
    void syncRedisToDb_HandleExceptionAndContinue() {
        // given
        String key1 = "matching:sandbox:" + UUID.randomUUID() + ":1";
        String key2 = "matching:sandbox:" + UUID.randomUUID() + ":2";

        // key1이 반드시 먼저 처리되도록 삽입 순서를 보장하는 LinkedHashSet 사용
        Set<String> keys = new java.util.LinkedHashSet<>();
        keys.add(key1); // 예외 발생 키
        keys.add(key2); // 정상 반환 키

        given(doubleRedisTemplate.keys("matching:sandbox:*")).willReturn(keys);

        // 첫 번째 키는 예외 발생, 두 번째 키는 정상 반환하도록 설정
        given(doubleRedisTemplate.execute(any(RedisScript.class), anyList()))
                .willThrow(new RuntimeException("Redis Error")) // key1
                .willReturn(5.0); // key2

        given(transactionTemplate.execute(any())).willAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        // 예외가 발생해도 key2에 대한 DB 업데이트는 실행되어야 함 (총 1회 실행)
        verify(summaryRepository, times(1)).updateReliabilityInDb(any(UUID.class), anyLong(), eq(5.0));
    }

    @Test
    @DisplayName("조회된 Redis 키가 없으면 로직을 즉시 종료한다")
    void syncRedisToDb_NoKeys() {
        // given
        given(doubleRedisTemplate.keys("matching:sandbox:*")).willReturn(Collections.emptySet());

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        verify(doubleRedisTemplate, never()).execute(any(RedisScript.class), anyList());
        verify(summaryRepository, never()).updateReliabilityInDb(any(), anyLong(), anyDouble());
    }
}
