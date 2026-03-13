package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 작업 대기 큐입니다!!
 */
@Component
@RequiredArgsConstructor
public class GameSyncQueue {
    private final GameSyncMap gameSyncMap;
    private static final BlockingQueue<Player> chessComQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Player> lichessQueue = new LinkedBlockingQueue<>();

    public void add(Player player) {
        Platform platform = player.getPlatform();
        switch (platform) {
            case CHESS_COM -> {
                if (!chessComQueue.contains(player)) {
                    gameSyncMap.addChessComPlayer(player.getId());
                    chessComQueue.add(player);
                }
            }
            case LICHESS -> {
                if (!lichessQueue.contains(player)) {
                    gameSyncMap.addLichessPlayer(player.getId());
                    lichessQueue.add(player);
                }
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
