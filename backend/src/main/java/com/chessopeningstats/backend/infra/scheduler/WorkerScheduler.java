package com.chessopeningstats.backend.infra.scheduler;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.ChessComQueue;
import com.chessopeningstats.backend.infra.queue.LichessQueue;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerScheduler {
    private final ChessComQueue chessComQueue;
    private final LichessQueue lichessQueue;
    private final GameSyncFacade gameSyncFacade;

    @Scheduled(fixedDelay = 1000)
    public void chessComWorker() {
        Player player;
        while ((player = chessComQueue.poll()) != null) {
            gameSyncFacade.syncPlayer(player).block();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void lichessWorker() {
        Player player;
        while ((player = lichessQueue.poll()) != null) {
            gameSyncFacade.syncPlayer(player).block();
        }
    }
}
