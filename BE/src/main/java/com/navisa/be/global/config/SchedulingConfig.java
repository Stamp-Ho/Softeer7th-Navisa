package com.navisa.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    /**
     * WebSocket 스케줄러와 충돌을 방지하기 위해
     * 기본 TaskScheduler 빈을 'taskScheduler'라는 이름으로 명시적 등록합니다.
     */
    @Primary
    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("navisa-scheduled-task-");

        // 예기치 못한 종료 시 진행 중인 작업 완료를 기다리도록 설정
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);

        scheduler.initialize();
        return scheduler;
    }
}
