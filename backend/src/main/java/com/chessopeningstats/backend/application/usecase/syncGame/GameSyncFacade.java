package com.chessopeningstats.backend.application.usecase.syncGame;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.usecase.syncGame.dto.RunningStatusResponse;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final GameSyncAsyncExecutor gameSyncAsyncExecutor;
    private final AccountService accountService;

    @LogExecutionTime
    public void sync(long accountId) {
        Account account = accountService.getAccount(accountId);
        List<Long> playerIdList = account.getAccountPlayers().stream()
                .map(AccountPlayer::getPlayer)
                .map(Player::getId)
                .toList();

        gameSyncAsyncExecutor.sync(accountId, playerIdList);
    }

    public RunningStatusResponse isRunning(long accountId){
        return RunningStatusResponse.of(gameSyncAsyncExecutor.isRunning(accountId));
    }
}
