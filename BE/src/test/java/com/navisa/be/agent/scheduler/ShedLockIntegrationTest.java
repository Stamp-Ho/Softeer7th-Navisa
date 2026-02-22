package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ShedLockIntegrationTest extends IntegrationTestSupport {

    @MockitoSpyBean
    private SyncReliabilityScheduler syncReliabilityScheduler;

    @MockitoSpyBean
    private AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    @Test
    @DisplayName("ShedLock은 여러 인스턴스 중 단 하나만 로직을 실행하도록 보장한다")
    void verifyShedLockPreventsDuplicateExecution() throws InterruptedException {
        // given
        int numberOfInstances = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfInstances);
        CountDownLatch latch = new CountDownLatch(numberOfInstances);

        // when
        for (int i = 0; i < numberOfInstances; i++) {
            executorService.submit(() -> {
                try {
                    syncReliabilityScheduler.syncRedisToDb();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        // ShedLock에 의해 실제 내부 비즈니스 로직(데이터 업데이트 등)이 1회만 실행되었는지 검증
        verify(agentSpecializedJobSummaryRepository, atMost(1)).updateReliabilityInDb(any(), any(), any());
    }
}
