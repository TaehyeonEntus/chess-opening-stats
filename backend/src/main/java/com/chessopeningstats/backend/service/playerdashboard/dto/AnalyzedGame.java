package com.chessopeningstats.backend.service.playerdashboard.dto;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Result;

import java.util.List;


public record AnalyzedGame(
        Color color,
        Result result,
        List<Long> openingIds,
        Long lastOpeningId,
        Player player
) {
}
