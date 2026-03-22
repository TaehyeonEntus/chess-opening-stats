package com.chessopeningstats.backend.infra.queue.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class LichessPlayerQueue implements PlayerQueue {
    private final Queue<Player> lichessQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Platform platform() {
        return Platform.LICHESS;
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
    public void enqueue(Player player){
        lichessQueue.add(player);
    }

    @Override
    public Player dequeue(){
        return lichessQueue.poll();
    }

    @Override
    public boolean isEmpty(){
        return lichessQueue.isEmpty();
    }
}
