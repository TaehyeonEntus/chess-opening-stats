package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import reactor.core.publisher.Flux;

public interface GameNormalizeService<T extends RawGame> {
    Platform platform();

    default Flux<NormalizedGame> normalize(Flux<T> rawGames, String username) {
        return rawGames.map(rawGame -> normalizeOne(rawGame, username));
    }

    NormalizedGame normalizeOne(T rawGame, String username);
}
