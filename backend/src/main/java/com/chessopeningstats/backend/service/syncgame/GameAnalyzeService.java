package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import reactor.core.publisher.ParallelFlux;

public interface GameAnalyzeService {
    ParallelFlux<AnalyzedGame> analyze(ParallelFlux<NormalizedGame> sanitizedGames);
}
