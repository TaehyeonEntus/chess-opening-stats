package com.chessopeningstats.backend.infra.repository.batch.dto;

import com.chessopeningstats.backend.domain.GameTime;
import com.chessopeningstats.backend.domain.GameType;

import java.time.Instant;

public record GameRow(
        String uuid,
        Instant playedAt,
        GameTime gameTime,
        GameType gameType,
        Long lastMatchedOpeningId,
        String openingIds
) {
}
