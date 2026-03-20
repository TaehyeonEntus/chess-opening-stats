package com.chessopeningstats.backend.infra.scheduler;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.service.syncgame.GameSyncFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerScheduler {
    private final GameSyncFacade gameSyncFacade;

    //직렬화는 됐는데 gameSyncFacade 밖으로 Fetch를 빼야함...
    //네트워크 I/O 시간동안 CPU를 굴리긴 하는데 syncFacade 외부까지 파이프라인으로 동작해야함

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() {
        gameSyncFacade.syncGames(Platform.CHESS_COM).block();
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() {
        gameSyncFacade.syncGames(Platform.LICHESS).block();
    }
}
