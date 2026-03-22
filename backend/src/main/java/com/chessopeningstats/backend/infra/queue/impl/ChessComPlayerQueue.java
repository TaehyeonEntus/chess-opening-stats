package com.chessopeningstats.backend.infra.queue.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class ChessComPlayerQueue implements PlayerQueue {
    private final Queue<Player> chessComQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public int size() {
        return chessComQueue.size();
    }

    @Override
    public boolean contains(Player player) {
        return chessComQueue.contains(player);
    }

    @Override
    public void enqueue(Player player) {
        chessComQueue.add(player);
    }

    @Override
    public Player dequeue() {
        return chessComQueue.poll();
    }

    @Override
    public boolean isEmpty() {
        return chessComQueue.isEmpty();
    }
}
