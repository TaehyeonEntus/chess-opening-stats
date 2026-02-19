package com.chessopeningstats.backend.batch.job;

import com.chessopeningstats.backend.application.usecase.syncGame.GameSyncFacade;
import com.chessopeningstats.backend.batch.BatchListener;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.BusinessException;
import com.chessopeningstats.backend.exception.handler.BatchExceptionHandler;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class GameSyncBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AccountRepository accountRepository;
    private final GameSyncFacade gameSyncFacade;

    @Bean
    public Job gameSyncJob(
            Step gameSyncStep,
            BatchListener loggingListener,
            BatchExceptionHandler failureHandler
    ) {
        return new JobBuilder("gameSyncJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 매 실행마다 새로운 Job Instance 생성
                .start(gameSyncStep)
                .listener(loggingListener)
                .listener(failureHandler)
                .build();
    }

    @Bean
    public Step gameSyncStep(
            ListItemReader<Account> playerReader,
            ItemWriter<Account> playerWriter
    ) {
        return new StepBuilder("gameSyncStep", jobRepository)
                .<Account, Account>chunk(1)
                .transactionManager(transactionManager)
                .reader(playerReader)
                .writer(playerWriter)
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(BusinessException.class)
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<Account> accountReader() {
        return new ListItemReader<>(accountRepository.findAll());}

    @Bean
    @StepScope
    public ItemWriter<Account> accountWriter() {
        return accounts -> {
            for (Account account : accounts) {
                gameSyncFacade.sync(account.getId());
            }
        };
    }
}
