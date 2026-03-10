package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.infra.repository.GamePlayerRepository;
import com.chessopeningstats.backend.infra.repository.batch.GamePlayerBatchRepository;
import com.chessopeningstats.backend.infra.repository.batch.dto.GamePlayerRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamePlayerService {
    private final PlayerService playerService;
    private final GamePlayerRepository gamePlayerRepository;
    private final GamePlayerBatchRepository gamePlayerBatchRepository;

    public void upsertBatch(List<GamePlayerRow> gamePlayerRows) {
        gamePlayerBatchRepository.upsertBatch(gamePlayerRows);
    }

    @Transactional
    public void updateLastPlayedAt(long playerId) {
        playerService.get(playerId).setLastPlayedAt(gamePlayerRepository.findLatestPlayedAt(playerId));
    }
}
