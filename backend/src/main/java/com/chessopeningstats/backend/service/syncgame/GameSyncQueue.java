package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GameSyncQueue {
    private static final BlockingQueue<Player> chessComQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Player> lichessQueue = new LinkedBlockingQueue<>();

    public void add(Player player) {
        Platform platform = player.getPlatform();
        switch (platform) {
            case CHESS_COM -> {
                if (!chessComQueue.contains(player))
                    chessComQueue.add(player);
            }
            case LICHESS -> {
                if (!lichessQueue.contains(player))
                    lichessQueue.add(player);
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    public BlockingQueue<Player> getChessComQueue() {
        return chessComQueue;
    }

    public BlockingQueue<Player> getLichessQueue() {
        return lichessQueue;
    }
}
