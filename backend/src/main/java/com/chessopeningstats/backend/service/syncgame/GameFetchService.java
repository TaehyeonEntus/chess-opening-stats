package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.PlayerGameClient;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import reactor.core.publisher.ParallelFlux;

public interface GameFetchService<T extends RawGame> {
    Platform platform();

    PlayerGameClient<T> client();

    ParallelFlux<T> fetch(Player player);
}
