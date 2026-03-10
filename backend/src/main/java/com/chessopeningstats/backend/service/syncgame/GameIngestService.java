package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

public interface GameIngestService {
    Mono<Void> ingest(ParallelFlux<AnalyzedGame> analyzedGames);
}
