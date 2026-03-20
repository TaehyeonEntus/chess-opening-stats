package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;

public interface GameAnalyzeService {
    AnalyzedGame analyze(NormalizedGame sanitizedGame);
}
