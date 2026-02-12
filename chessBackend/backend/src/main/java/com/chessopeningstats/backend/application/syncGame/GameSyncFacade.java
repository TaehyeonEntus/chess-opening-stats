package com.chessopeningstats.backend.application.syncGame;

import com.chessopeningstats.backend.application.syncGame.internal.analysis.GameAnalyzeService;
import com.chessopeningstats.backend.application.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.application.syncGame.internal.ingest.GameIngestService;
import com.chessopeningstats.backend.application.syncGame.internal.provide.GameProvideServiceRegistry;
import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final PlayerRepository playerRepository;
    private final GameProvideServiceRegistry gameProvideServiceRegistry;
    private final GameAnalyzeService gameAnalyzeService;
    private final GameIngestService gameIngestService;

    @LogExecutionTime
    public void sync(long playerId) {
        playerRepository
                .findById(playerId)
                .orElseThrow(PlayerNotFoundException::new)
                .getPlayerAccounts()
                .forEach(pa -> sync(pa.getPlayer(), pa.getAccount()));
    }

    public void sync(Player player, Account account) {
        // 0. 갱신 시간
        Instant fetchedAt = Instant.now();

        // 1. fetch
        List<NormalizedGameDto> normalizedGameDtos = gameProvideServiceRegistry.getService(account.getPlatform()).provideGames(account);

        // skip if empty
        if (normalizedGameDtos.isEmpty()) {
            return;
        }

        // 2. analyze
        Set<AnalyzedGameDto> analyzedGameDtos = gameAnalyzeService.analyzeAll(normalizedGameDtos);

        // 3. ingest (Transaction)
        gameIngestService.ingestAll(player.getId(), account.getId(), analyzedGameDtos, fetchedAt);
    }
}
