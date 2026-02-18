package com.navisa.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 동시에 발송할 메일 수
        executor.setMaxPoolSize(20); // 최대 스레드 수
        executor.setQueueCapacity(1000); // 1000건을 담을 수 있는 큐 크기
        executor.setThreadNamePrefix("MailExecutor-");
        executor.initialize();
        return executor;
    }
}
