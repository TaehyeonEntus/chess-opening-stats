package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.impl.ChessComPlayerQueue;
import com.chessopeningstats.backend.service.syncgame.PlayerPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChessComPlayerPublishService implements PlayerPublishService {
    private final ChessComPlayerQueue chessComPlayerQueue;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public Flux<Player> publishPlayer() {
        return Flux.generate(sink -> {
            if (chessComPlayerQueue.isEmpty())
                sink.complete();
            else
                sink.next(chessComPlayerQueue.dequeue());
        });
    }
}
