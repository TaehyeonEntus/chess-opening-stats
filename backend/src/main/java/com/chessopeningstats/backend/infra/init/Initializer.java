package com.chessopeningstats.backend.infra.init;

import com.chessopeningstats.backend.infra.client.opening.OpeningsClient;
import com.chessopeningstats.backend.infra.queue.ChessComPlayerQueue;
import com.chessopeningstats.backend.infra.queue.LichessPlayerQueue;
import com.chessopeningstats.backend.infra.storage.OpeningStorage;
import com.chessopeningstats.backend.service.EmitterService;
import com.chessopeningstats.backend.service.playerdashboard.PlayerDashboardFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class Initializer {
    private final PlayerDashboardFacade playerDashboardFacade;
    private final ChessComPlayerQueue chessComPlayerQueue;
    private final LichessPlayerQueue lichessPlayerQueue;
    private final EmitterService emitterService;
    private final OpeningStorage openingStorage;
    private final OpeningsClient openingsClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initOpenings() {
        openingStorage.storeAll(openingsClient.fetchOpenings());
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() {
        while (true) {
            emitterService.announce(
                    Objects.requireNonNull(playerDashboardFacade
                            .getPlayerDashboard(lichessPlayerQueue.dequeue())
                            .block())
            );
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() {
        while (true) {
            emitterService.announce(
                    Objects.requireNonNull(playerDashboardFacade
                            .getPlayerDashboard(chessComPlayerQueue.dequeue())
                            .block())
            );
        }
    }
}
