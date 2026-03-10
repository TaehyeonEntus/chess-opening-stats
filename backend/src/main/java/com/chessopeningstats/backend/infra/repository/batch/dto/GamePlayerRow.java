package com.chessopeningstats.backend.infra.repository.batch.dto;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;

public record GamePlayerRow(
        String gameId,
        long playerId,
        GamePlayerColor color,
        GamePlayerResult result
) {
}
