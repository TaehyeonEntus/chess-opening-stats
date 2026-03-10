package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.infra.repository.batch.dto.GamePlayerRow;
import com.chessopeningstats.backend.infra.repository.batch.dto.GameRow;
import com.chessopeningstats.backend.service.syncgame.GameIngestService;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.GameSyncBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class GameIngestServiceImpl implements GameIngestService {
    private final GameSyncBatchWriter gameSyncBatchWriter;
    private final ExecutorService virtualThreadPool;
    @Value("${cpu.core}")
    private final String cpuCore;

    @Override
    public Mono<Void> ingest(ParallelFlux<AnalyzedGame> analyzedGames) {
        return analyzedGames
                .sequential()
                .buffer(1000)
                .flatMap(this::ingestBatch, Integer.parseInt(cpuCore) * 2)
                .then();
    }

    private Mono<Void> ingestBatch(List<AnalyzedGame> analyzedGameBatch) {
        return Mono.<Void>fromRunnable(() -> {
            List<GameRow> gameRows = new ArrayList<>();
            List<GamePlayerRow> gamePlayerRows = new ArrayList<>();

            for (AnalyzedGame analyzedGame : analyzedGameBatch) {
                gameRows.add(analyzedGame.gameRow());
                gamePlayerRows.add(analyzedGame.gamePlayerRow());
            }

            gameSyncBatchWriter.write(new GameSyncBatch(gameRows, gamePlayerRows));
        }).subscribeOn(Schedulers.fromExecutor(virtualThreadPool));
    }
}