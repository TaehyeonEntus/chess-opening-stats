package com.chessopeningstats.backend.service.playerdashboard.dto;

import java.util.List;

public record ColorDashboard(
        Stat stat,
        List<OpeningStat> mostPlayedOpenings,
        List<OpeningStat> highestWinRateOpenings,
        List<OpeningStat> openings
) {
}
