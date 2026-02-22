package com.navisa.be.global.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Resilience4jLogConfig {

    @Bean
    public RegistryEventConsumer<CircuitBreaker> circuitBreakerEventConsumer() {
        return new RegistryEventConsumer<CircuitBreaker>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                registerEvents(entryAddedEvent.getAddedEntry());
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemovedEvent) {}

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                registerEvents(entryReplacedEvent.getNewEntry());
            }
        };
    }

    private void registerEvents(CircuitBreaker cb) {
        cb.getEventPublisher()
            .onStateTransition(event -> {
                CircuitBreaker.Metrics metrics = cb.getMetrics();
                log.warn("[CircuitBreaker State Change] '{}' : {} | 실패율: {}% | 총 호출: {} (성공: {}, 실패: {})",
                    cb.getName(),
                    event.getStateTransition(),
                    metrics.getFailureRate() < 0 ? "N/A(호출 부족)" : String.format("%.2f", metrics.getFailureRate()),
                    metrics.getNumberOfBufferedCalls(),
                    metrics.getNumberOfSuccessfulCalls(),
                    metrics.getNumberOfFailedCalls()
                );
            })
            .onCallNotPermitted(event -> {
                log.debug("[CircuitBreaker OPEN] '{}' 요청이 차단되었습니다.", cb.getName());
            });
    }
}
