package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.NoLinkedPlayersException;
import com.chessopeningstats.backend.exception.TooManySyncRequestException;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SyncGameUseCase {
    private final AccountService accountService;
    private final GameSyncFacade gameSyncFacade;

    public void syncGames(long accountId) {
        Account account = accountService.get(accountId);

        if (isSyncedIn10Mins(account))
            throw new TooManySyncRequestException();

        if (account.getAccountPlayers().isEmpty())
            throw new NoLinkedPlayersException();

        gameSyncFacade.syncAccount(accountId);
    }

    private boolean isSyncedIn10Mins(Account account) {
        return account.getLastSyncedAt().isAfter(Instant.now().minus(Duration.ofMinutes(10)));
    }
}
