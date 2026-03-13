package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.NoLinkedPlayersException;
import com.chessopeningstats.backend.exception.TooManySyncRequestException;
import com.chessopeningstats.backend.service.AccountPlayerService;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import com.chessopeningstats.backend.service.syncgame.GameSyncMap;
import com.chessopeningstats.backend.service.syncgame.GameSyncQueue;
import com.chessopeningstats.backend.web.account.dto.SyncGameResponse;
import com.chessopeningstats.backend.web.account.dto.SyncGameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SyncGameUseCase {
    private final AccountPlayerService accountPlayerService;
    private final AccountService accountService;
    private final GameSyncFacade gameSyncFacade;
    private final GameSyncQueue gameSyncQueue;
    private final GameSyncMap gameSyncMap;

    public SyncGameResponse syncGames(long accountId) {
        Account account = accountService.get(accountId);

        if (isSyncedIn10Mins(account))
            throw new TooManySyncRequestException();

        if (account.getAccountPlayers().isEmpty())
            throw new NoLinkedPlayersException();

        gameSyncFacade.syncAccount(accountId);
        return new SyncGameResponse(gameSyncQueue.getChessComQueue().size(), gameSyncQueue.getLichessQueue().size());
    }

    public SyncGameStatus syncStatus(long accountId) {
        for (long playerId : accountPlayerService.getPlayerIdsByAccountId(accountId))
            if (gameSyncMap.isStillSync(playerId))
                return new SyncGameStatus(true);
        return new SyncGameStatus(false);
    }

    private boolean isSyncedIn10Mins(Account account) {
        return account.getLastSyncedAt().isAfter(Instant.now().minus(Duration.ofMinutes(10)));
    }
}
