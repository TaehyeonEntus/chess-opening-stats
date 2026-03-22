package com.chessopeningstats.backend.infra.scheduler;

import com.chessopeningstats.backend.infra.queue.impl.ChessComPlayerQueue;
import com.chessopeningstats.backend.infra.queue.impl.LichessPlayerQueue;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerScheduler {
    private final ChessComPlayerQueue chessComPlayerQueue;
    private final LichessPlayerQueue lichessPlayerQueue;
    private final GameSyncFacade gameSyncFacade;

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() throws InterruptedException {
        while (true) {
            gameSyncFacade.syncGames(chessComPlayerQueue.dequeue()).block();
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() throws InterruptedException {
        while (true) {
            gameSyncFacade.syncGames(lichessPlayerQueue.dequeue()).block();
        }
    }
}
