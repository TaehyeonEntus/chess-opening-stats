package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueRouter {
    private final ChessComQueue chessComQueue;
    private final LichessQueue lichessQueue;

    public void add(Player player) {
        switch (player.platform()) {
            case CHESS_COM -> chessComQueue.add(player);
            case LICHESS -> lichessQueue.add(player);
            default -> throw new UnsupportedPlatformException(player.platform());
        }
    }
}
