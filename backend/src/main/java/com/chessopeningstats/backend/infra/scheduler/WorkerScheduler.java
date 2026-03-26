package com.chessopeningstats.backend.infra.scheduler;

import com.chessopeningstats.backend.infra.queue.impl.ChessComPlayerQueue;
import com.chessopeningstats.backend.infra.queue.impl.LichessPlayerQueue;
import com.chessopeningstats.backend.infra.repository.EmitterRepository;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import com.chessopeningstats.backend.service.syncgame.dto.PlayerDashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerScheduler {
    private final ChessComPlayerQueue chessComPlayerQueue;
    private final LichessPlayerQueue lichessPlayerQueue;
    private final GameSyncFacade gameSyncFacade;
    private final EmitterRepository emitterRepository;

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() throws InterruptedException, IOException {
        while (true) {
            PlayerDashboard playerDashboard = gameSyncFacade.syncGames(chessComPlayerQueue.dequeue()).block();
            for (SseEmitter emitter : emitterRepository.get(playerDashboard.player())) {
                emitter.send(SseEmitter.event().name("dashboard").data(playerDashboard.dashboard()));
                emitter.complete();
            }
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() throws InterruptedException, IOException {
        while (true) {
            PlayerDashboard playerDashboard = gameSyncFacade.syncGames(lichessPlayerQueue.dequeue()).block();
            for (SseEmitter emitter : emitterRepository.get(playerDashboard.player())) {
                emitter.send(SseEmitter.event().name("dashboard").data(playerDashboard.dashboard()));
                emitter.complete();
            }
        }
    }
}