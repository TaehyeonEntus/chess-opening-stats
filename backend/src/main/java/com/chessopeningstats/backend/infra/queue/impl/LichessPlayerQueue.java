package com.chessopeningstats.backend.infra.queue.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class LichessPlayerQueue implements PlayerQueue {
    private final Queue<Player> lichessQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public void enqueue(Player player) {
        lichessQueue.add(player);
    }

    @Override
    public Player dequeue() {
        return lichessQueue.poll();
    }

    @Override
    public int size() {
        return lichessQueue.size();
    }

    @Override
    public boolean contains(Player player) {
        return lichessQueue.contains(player);
    }

    @Override
    public List<Player> dequeueAll() {
        List<Player> list = new ArrayList<>();
        while (!lichessQueue.isEmpty())
            list.add(dequeue());
        return list;
    }
}
