package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.*;

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

    @Test
    @DisplayName("SCAN을 통해 Redis 가중치 데이터를 안전하게 읽어와 DB로 일괄 동기화한다")
    void syncRedisToDb_Success_WithScan() {
        // given
        UUID agentId = UUID.randomUUID();
        String key = "matching:sandbox:" + agentId + ":1";

        @SuppressWarnings("unchecked")
        Cursor<String> mockCursor = (Cursor<String>) mock(Cursor.class);
        given(mockCursor.hasNext()).willReturn(true, false);
        given(mockCursor.next()).willReturn(key);

        given(doubleRedisTemplate.scan(any(ScanOptions.class))).willReturn(mockCursor);

        given(doubleRedisTemplate.execute(any(RedisScript.class), anyList()))
                .willReturn(10.5);

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        verify(doubleRedisTemplate, times(1)).scan(any(ScanOptions.class));
        verify(summaryRepository, times(1)).updateReliabilityInDb(eq(agentId), eq(1L), eq(10.5));
    }

    @Test
    @DisplayName("SCAN 도중 예외가 발생해도 안전하게 리소스를 닫고 종료한다")
    void syncRedisToDb_HandleScanException() {
        // given
        given(doubleRedisTemplate.scan(any(ScanOptions.class))).willThrow(new RuntimeException("Redis Scan Error"));

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        // 예외가 잡히고 DB 업데이트는 실행되지 않아야 함
        verify(summaryRepository, never()).updateReliabilityInDb(any(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("동기화할 데이터가 없으면 DB 업데이트를 수행하지 않는다")
    void syncRedisToDb_NoData_SkipUpdate() {
        // given
        Cursor<String> emptyCursor = mock(Cursor.class);
        given(emptyCursor.hasNext()).willReturn(false);
        given(doubleRedisTemplate.scan(any(ScanOptions.class))).willReturn(emptyCursor);

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        verify(summaryRepository, never()).updateReliabilityInDb(any(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("Lua 스크립트가 null을 반환하면 DB 업데이트를 수행하지 않는다")
    void syncRedisToDb_NullWeight_SkipUpdate() {
        // given
        UUID agentId = UUID.randomUUID();
        String key = "matching:sandbox:" + agentId + ":1";

        @SuppressWarnings("unchecked")
        Cursor<String> mockCursor = (Cursor<String>) mock(Cursor.class);
        given(mockCursor.hasNext()).willReturn(true, false);
        given(mockCursor.next()).willReturn(key);

        given(doubleRedisTemplate.scan(any(ScanOptions.class))).willReturn(mockCursor);
        given(doubleRedisTemplate.execute(any(RedisScript.class), anyList()))
                .willReturn(null);

        // when
        syncReliabilityScheduler.syncRedisToDb();

        // then
        verify(summaryRepository, never()).updateReliabilityInDb(any(), anyLong(), anyDouble());
    }
}
