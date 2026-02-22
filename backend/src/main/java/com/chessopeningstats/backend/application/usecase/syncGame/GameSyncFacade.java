package com.chessopeningstats.backend.application.usecase.syncGame;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.domain.PlayerService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.GameAnalyzeService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.ingest.GameIngestService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.GameProvideServiceRegistry;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final GameSyncAsyncExecutor gameSyncAsyncExecutor;
    private final AccountService accountService;

    @LogExecutionTime
    public void sync(long accountId) {
        Account account = accountService.getAccount(accountId);
        List<Player> players = account.getAccountPlayers().stream()
                .map(AccountPlayer::getPlayer)
                .toList();

        for (Player player : players) {
            gameSyncAsyncExecutor.sync(account.getId(), player.getId());
        }
    }
}
