package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import reactor.core.publisher.ParallelFlux;

public interface GameSanitizeService {
    ParallelFlux<NormalizedGame> sanitize(ParallelFlux<NormalizedGame> normalizedGames);
}
