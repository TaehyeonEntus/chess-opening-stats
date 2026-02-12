package com.chessopeningstats.backend.batch;

import java.time.Duration;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(
                "Batch job started: name={}, id={}, instanceId={}, params={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId(),
                jobExecution.getJobInstance().getInstanceId(),
                jobExecution.getJobParameters()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        StepSummary summary = summarize(jobExecution.getStepExecutions());
        log.info(
                "Batch job finished: name={}, id={}, status={}, exitStatus={}, durationMs={}, read={}, write={}, skip={}, commit={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId(),
                jobExecution.getStatus(),
                jobExecution.getExitStatus().getExitCode(),
                durationMillis(jobExecution),
                summary.readCount,
                summary.writeCount,
                summary.skipCount,
                summary.commitCount
        );
    }

    private static long durationMillis(JobExecution jobExecution) {
        if (jobExecution.getStartTime() == null || jobExecution.getEndTime() == null)
            return 0L;

        return Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
    }

    private static StepSummary summarize(Collection<StepExecution> stepExecutions) {
        long read = 0L;
        long write = 0L;
        long skip = 0L;
        long commit = 0L;
        for (StepExecution stepExecution : stepExecutions) {
            read += stepExecution.getReadCount();
            write += stepExecution.getWriteCount();
            skip += stepExecution.getSkipCount();
            commit += stepExecution.getCommitCount();
        }
        return new StepSummary(read, write, skip, commit);
    }

    private record StepSummary(long readCount, long writeCount, long skipCount, long commitCount) {
    }
}
