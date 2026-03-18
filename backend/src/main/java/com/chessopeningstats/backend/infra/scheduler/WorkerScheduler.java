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

    //직렬화는 됐는데 gameSyncFacade 밖으로 Fetch를 빼야함...
    //네트워크 I/O 시간동안 CPU를 굴리긴 하는데 fetch끝나고 바로 다음 Player를 꺼내야하는데
    //여기에 주렁주렁 파이프라인 달자니 Worker의 의미가 사라짐.....
    @Scheduled(fixedDelay = 1000, scheduler = "chessComScheduler")
    public void chessComWorker() {
        Mono.fromCallable(chessComQueue::poll)
                .repeat(() -> !chessComQueue.isEmpty())
                .concatMap(gameSyncFacade::syncPlayer)
                .blockLast();
    }

    @Scheduled(fixedDelay = 1000, scheduler = "lichessScheduler")
    public void lichessWorker() {
        Mono.fromCallable(lichessQueue::poll)
                .repeat(() -> !lichessQueue.isEmpty())
                .concatMap(gameSyncFacade::syncPlayer)
                .blockLast();
    }
}
