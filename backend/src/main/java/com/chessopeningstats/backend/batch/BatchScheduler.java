package com.chessopeningstats.backend.batch;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.service.GameService;
import com.chessopeningstats.backend.service.PlayerService;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import com.chessopeningstats.backend.service.syncgame.GameSyncQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final GameSyncFacade gameSyncFacade;
    private final AccountService accountService;
    private final PlayerService playerService;
    private final GameSyncQueue gameSyncQueue;
    private final GameService gameService;

    @Scheduled(fixedDelay = 86400000)
    public void scheduledSync() {
        accountService.getAllIds().forEach(gameSyncFacade::syncAccount);
    }

    @Scheduled(fixedDelay = 86400000)
    public void scheduledGc() {
        playerService.garbageCollect();
        gameService.garbageCollect();
    }

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void runChessComGameSyncWorker() {
        Player player;
        while ((player = gameSyncQueue.getChessComQueue().poll()) != null) {
            gameSyncFacade.syncPlayer(player).block();
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void runLichessGameSyncWorker() {
        Player player;
        while ((player = gameSyncQueue.getLichessQueue().poll()) != null) {
            gameSyncFacade.syncPlayer(player).block();
        }
    }
}
