package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncQueue {
    private final ChessComQueue chessComQueue;
    private final LichessQueue lichessQueue;

    public void add(Player player) {
        switch (player.platform()) {
            case CHESS_COM -> chessComQueue.add(player);
            case LICHESS -> lichessQueue.add(player);
            default -> throw new UnsupportedPlatformException(player.platform());
        }
    }

    public int size(Platform platform) {
        return switch (platform) {
            case CHESS_COM -> chessComQueue.size();
            case LICHESS -> lichessQueue.size();
            default -> throw new UnsupportedPlatformException(platform);
        };
    }

    public boolean contains(Player player){
        return switch (player.platform()) {
            case CHESS_COM -> chessComQueue.contains(player);
            case LICHESS -> lichessQueue.contains(player);
            default -> throw new UnsupportedPlatformException(player.platform());
        };
    }
}
