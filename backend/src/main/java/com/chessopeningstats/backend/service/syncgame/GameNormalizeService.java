package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;

public interface GameNormalizeService<T extends RawGame> {
    Platform platform();

    NormalizedGame normalize(T rawGame);
}
