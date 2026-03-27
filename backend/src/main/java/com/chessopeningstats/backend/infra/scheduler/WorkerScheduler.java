package com.chessopeningstats.backend.infra.scheduler;

import com.chessopeningstats.backend.infra.queue.ChessComPlayerQueue;
import com.chessopeningstats.backend.infra.queue.LichessPlayerQueue;
import com.chessopeningstats.backend.service.EmitterService;
import com.chessopeningstats.backend.service.playerdashboard.PlayerDashboardFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerScheduler {
    private final ChessComPlayerQueue chessComPlayerQueue;
    private final LichessPlayerQueue lichessPlayerQueue;
    private final PlayerDashboardFacade playerDashboardFacade;
    private final EmitterService emitterService;

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() throws IOException {
        while (true) {
            emitterService.announce(playerDashboardFacade.syncGames(chessComPlayerQueue.dequeue()).block());
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() throws IOException {
        while (true) {
            emitterService.announce(playerDashboardFacade.syncGames(lichessPlayerQueue.dequeue()).block());
        }
    }
}