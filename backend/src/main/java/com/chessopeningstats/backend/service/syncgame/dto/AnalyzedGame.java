package com.chessopeningstats.backend.service.syncgame.dto;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Result;

import java.util.List;


public record AnalyzedGame(
        Color color,
        Result result,
        List<Long> openingIds,
        Long lastOpeningId
) {
}
