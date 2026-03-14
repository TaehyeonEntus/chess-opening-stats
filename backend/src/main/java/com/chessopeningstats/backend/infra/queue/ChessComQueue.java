package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class ChessComQueue {
    private final BlockingQueue<Player> chessComQueue = new LinkedBlockingQueue<>();

    public void add(Player player) {
        chessComQueue.add(player);
    }

    public Player poll() {
        return chessComQueue.poll();
    }
}
