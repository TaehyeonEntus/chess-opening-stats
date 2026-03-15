package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import reactor.core.publisher.ParallelFlux;

public interface GameNormalizeService<T extends RawGame> {
    Platform platform();

    ParallelFlux<NormalizedGame> normalize(ParallelFlux<T> rawGames, Player player);
}
