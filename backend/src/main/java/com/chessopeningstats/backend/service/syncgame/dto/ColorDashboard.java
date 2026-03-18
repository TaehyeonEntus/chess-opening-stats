package com.chessopeningstats.backend.service.syncgame.dto;

import java.util.List;

public record ColorDashboard(
        Stat stat,
        List<OpeningStat> mostPlayedOpenings,
        List<OpeningStat> highestWinRateOpenings,
        List<OpeningStat> openings
) {
}
