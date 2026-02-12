package com.chessopeningstats.backend.batch.job;

import com.chessopeningstats.backend.application.syncGame.GameSyncFacade;
import com.chessopeningstats.backend.batch.BatchListener;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.BusinessException;
import com.chessopeningstats.backend.exception.handler.BatchExceptionHandler;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
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
    private final PlayerRepository playerRepository;
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
            ListItemReader<Player> playerReader,
            ItemWriter<Player> playerWriter
    ) {
        return new StepBuilder("gameSyncStep", jobRepository)
                .<Player, Player>chunk(1)
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
    public ListItemReader<Player> playerReader() {
        return new ListItemReader<>(playerRepository.findAll());
    }

    @Bean
    @StepScope
    public ItemWriter<Player> playerWriter() {
        return players -> {
            for (Player player : players) {
                gameSyncFacade.sync(player.getId());
            }
        };
    }
}
