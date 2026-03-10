package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.service.GamePlayerService;
import com.chessopeningstats.backend.service.GameService;
import com.chessopeningstats.backend.service.syncgame.dto.GameSyncBatch;
import com.chessopeningstats.backend.util.logger.LogExecutionTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GameIngestService.class에 종속
 * 트랜잭션 분리를 위한 클래스입니다
 */

@Component
@RequiredArgsConstructor
@Transactional
public class GameSyncBatchWriter {
    private final GameService gameService;
    private final GamePlayerService gamePlayerService;

    @LogExecutionTime
    public void write(GameSyncBatch batch) {
        gameService.upsertBatch(batch.gameRows());
        gamePlayerService.upsertBatch(batch.gamePlayerRows());
    }
}

