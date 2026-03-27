package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;

public interface PlayerQueue {
    Platform platform();

    void enqueue(Player player);

    Player dequeue();

    int size();

    boolean contains(Player player);
}
