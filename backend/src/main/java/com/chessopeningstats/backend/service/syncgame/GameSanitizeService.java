package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;

public interface GameSanitizeService {
    boolean sanitize(NormalizedGame normalizedGame);
}
