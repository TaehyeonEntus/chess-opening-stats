package com.chessopeningstats.backend.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobOperator jobOperator;

    @Qualifier("gameSyncJob")
    private final Job gameSyncJob;

    @Scheduled(fixedDelayString = "${sync.batch.fixed-delay-ms}")
    public void runGameSync() {
        jobOperator.startNextInstance(gameSyncJob);
    }
}
