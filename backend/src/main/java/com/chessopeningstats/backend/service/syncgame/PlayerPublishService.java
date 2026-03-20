package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;

import java.util.List;

public interface PlayerPublishService {
    Platform platform();

    List<Player> publishPlayer();
}
