package com.chessopeningstats.backend.application.usecase.syncGame;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.GameAnalyzeService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.ingest.GameIngestService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.GameProvideServiceRegistry;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final AccountRepository AccountRepository;
    private final GameProvideServiceRegistry gameProvideServiceRegistry;
    private final GameAnalyzeService gameAnalyzeService;
    private final GameIngestService gameIngestService;

    @LogExecutionTime
    public void sync(long accountId) {
        AccountRepository
                .findById(accountId)
                .orElseThrow(PlayerNotFoundException::new)
                .getAccountPlayers()
                .forEach(this::sync);
    }

    public void sync(AccountPlayer accountPlayer) {
        Account account = accountPlayer.getAccount();
        Player player = accountPlayer.getPlayer();

        Instant fetchedAt = Instant.now();

        // 1. fetch
        List<NormalizedGameDto> normalizedGameDtos = gameProvideServiceRegistry.getService(player.getPlatform()).provideGames(player);

        // skip if empty
        if (normalizedGameDtos.isEmpty()) {
            return;
        }

        // 2. analyze
        Set<AnalyzedGameDto> analyzedGameDtos = gameAnalyzeService.analyzeAll(normalizedGameDtos);

        // 3. ingest (Transaction)
        gameIngestService.ingestAll(account.getId(), player.getId(), analyzedGameDtos, fetchedAt);
    }
}
