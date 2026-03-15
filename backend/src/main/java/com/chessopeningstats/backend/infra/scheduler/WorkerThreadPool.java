package com.chessopeningstats.backend.infra.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class WorkerThreadPool {
    @Bean
    public ThreadPoolTaskScheduler chessComScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ChessCom-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public ThreadPoolTaskScheduler lichessScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("Lichess-");
        scheduler.initialize();
        return scheduler;
    }
}
