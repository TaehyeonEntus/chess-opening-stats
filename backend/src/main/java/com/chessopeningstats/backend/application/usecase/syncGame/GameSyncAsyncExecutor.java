package com.chessopeningstats.backend.application.usecase.syncGame;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.domain.PlayerService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.GameAnalyzeService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.ingest.GameIngestService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.GameProvideServiceRegistry;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.SyncAlreadyRunningException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameSyncAsyncExecutor {

    private final GameProvideServiceRegistry gameProvideServiceRegistry;
    private final GameAnalyzeService gameAnalyzeService;
    private final GameIngestService gameIngestService;
    private final AccountService accountService;
    private final PlayerService playerService;
    private final ConcurrentHashMap<Long, Boolean> runningTasks = new ConcurrentHashMap<>();

    @Async
    public void sync(long accountId, List<Long> playerIdList) {
        if (runningTasks.putIfAbsent(accountId, true) != null)
            return;
        try {
            playerIdList.forEach(playerId -> syncOne(accountId, playerId));
        } finally {
            runningTasks.remove(accountId);
        }
    }

    public void syncOne(long accountId, long playerId) {
        Instant fetchedAt = Instant.now();

        Account account = accountService.getAccount(accountId);
        Player player = playerService.getPlayer(playerId);

        // 1. fetch
        List<NormalizedGameDto> normalizedGameDtos = gameProvideServiceRegistry
                .getService(player.getPlatform())
                .provideGames(player);

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

    public boolean isRunning(long accountId){
        return runningTasks.containsKey(accountId);
    }
}