package com.chessopeningstats.backend.infra.scheduler;

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

    //직렬화는 됐는데 gameSyncFacade 밖으로 Fetch를 빼야함...
    //네트워크 I/O 시간동안 CPU를 굴리긴 하는데
    //큐 앞뒤 Player끼리의 파이프라인은 아직 동기임.....
    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() {
        while (!chessComQueue.isEmpty()) {
            gameSyncFacade.syncPlayer(chessComQueue.poll()).block();
        }
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() {
        while (!lichessQueue.isEmpty()) {
            gameSyncFacade.syncPlayer(lichessQueue.poll()).block();
        }
    }
}
