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

    //직렬화는 됐는데 gameSyncFacade 밖으로 Fetch를 빼야함...
    //네트워크 I/O 시간동안 CPU를 굴리긴 하는데 syncFacade 외부까지 파이프라인으로 동작해야함

    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() {
        while (!chessComPlayerQueue.isEmpty()) {
            gameSyncFacade.syncGames(chessComPlayerQueue.dequeue()).block();
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() {
        gameSyncFacade.syncGames(lichessPlayerQueue.dequeue()).block();
    }
}
