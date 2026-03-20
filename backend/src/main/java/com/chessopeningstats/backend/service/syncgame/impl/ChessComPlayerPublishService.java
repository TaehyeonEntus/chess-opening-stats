package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.impl.ChessComPlayerQueue;
import com.chessopeningstats.backend.service.syncgame.PlayerPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChessComPlayerPublishService implements PlayerPublishService {
    private final ChessComPlayerQueue chessComPlayerQueue;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public List<Player> publishPlayer() {
        return chessComPlayerQueue.dequeueAll();
    }
}
