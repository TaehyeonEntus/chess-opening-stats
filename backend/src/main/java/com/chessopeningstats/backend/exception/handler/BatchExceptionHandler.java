package com.chessopeningstats.backend.exception.handler;

import com.chessopeningstats.backend.exception.handler.error.ErrorResponse;
import com.chessopeningstats.backend.exception.handler.error.ExceptionConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchExceptionHandler implements JobExecutionListener {
    private final ExceptionConverter exceptionConverter;
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error(
                    "Batch job FAILED: name={}, id={}",
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getId()
            );
            jobExecution.getAllFailureExceptions().forEach(ex -> {
                ErrorResponse errorResponse = exceptionConverter.convert(ex);
                log.error(
                        "Failure exception: code={}, message={}",
                        errorResponse.getCode(),
                        errorResponse.getMessage(),
                        ex
                );
            });
            // 여기에 추가적인 실패 처리 로직을 넣을 수 있습니다. (e.g., 관리자에게 알림 발송)
        }
    }
}
