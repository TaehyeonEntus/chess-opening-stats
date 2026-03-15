package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.PlayerGameClient;
import com.chessopeningstats.backend.infra.client.playergames.impl.LichessPlayerGameClient;
import com.chessopeningstats.backend.infra.client.playergames.dto.LichessRawGame;
import com.chessopeningstats.backend.service.syncgame.GameFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

@Service
@RequiredArgsConstructor
public class LichessGameFetchService implements GameFetchService<LichessRawGame> {
    private final LichessPlayerGameClient client;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public PlayerGameClient<LichessRawGame> client() {
        return this.client;
    }

    @Override
    public ParallelFlux<LichessRawGame> fetch(Player player) {
        return client.fetchGames(player);
    }
}
