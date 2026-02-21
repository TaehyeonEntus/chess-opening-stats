package com.chessopeningstats.backend.application.usecase.syncGame;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.GameAnalyzeService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.ingest.GameIngestService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.GameProvideServiceRegistry;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.SyncAlreadyRunningException;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final AccountRepository AccountRepository;
    private final GameProvideServiceRegistry gameProvideServiceRegistry;
    private final GameAnalyzeService gameAnalyzeService;
    private final GameIngestService gameIngestService;
    private final ConcurrentHashMap<Long, Boolean> runningTasks = new ConcurrentHashMap<>();
    private final AccountService accountService;

    @LogExecutionTime
    public void sync(long accountId) {
        Account account = accountService.getAccount(accountId);
        List<Player> players = account.getAccountPlayers().stream()
                .map(AccountPlayer::getPlayer)
                .toList();

        for (Player player : players) {
            if (runningTasks.putIfAbsent(player.getId(), true) != null)
                throw new SyncAlreadyRunningException();
            try {
                sync(account, player);
            } finally {
                runningTasks.remove(player.getId());
            }
        }
    }

    public void sync(Account account, Player player) {
        Instant fetchedAt = Instant.now();

        // 1. fetch
        List<NormalizedGameDto> normalizedGameDtos = gameProvideServiceRegistry.getService(player.getPlatform()).provideGames(player);

        // skip if empty
        if (normalizedGameDtos.isEmpty()) {
            accountService.updateLastSyncedAt(account.getId(), fetchedAt);
            return;
        }

        // 2. analyze
        Set<AnalyzedGameDto> analyzedGameDtos = gameAnalyzeService.analyzeAll(normalizedGameDtos);

        // 3. ingest (Transaction)
        gameIngestService.ingestAll(account.getId(), player.getId(), analyzedGameDtos, fetchedAt);
    }
}
