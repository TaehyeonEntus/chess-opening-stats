package com.chessopeningstats.backend.infra.scheduler;

import com.chessopeningstats.backend.infra.queue.ChessComQueue;
import com.chessopeningstats.backend.infra.queue.LichessQueue;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WorkerScheduler {
    private final ChessComQueue chessComQueue;
    private final LichessQueue lichessQueue;
    private final GameSyncFacade gameSyncFacade;

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() {
        Mono.fromCallable(chessComQueue::poll)
                .repeat(() -> !chessComQueue.isEmpty())
                .flatMap(gameSyncFacade::syncPlayer)
                .blockLast();
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() {
        Mono.fromCallable(lichessQueue::poll)
                .repeat(() -> !lichessQueue.isEmpty())
                .flatMap(gameSyncFacade::syncPlayer)
                .blockLast();
    }
}
