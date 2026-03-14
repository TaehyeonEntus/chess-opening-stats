package com.chessopeningstats.backend.service.syncgame.dto;

import java.util.List;

public record ColorDashboard(
        ColorRecord record,
        List<ColorOpeningStat> mostPlayedOpenings,
        List<ColorOpeningStat> highestWinRateOpenings,
        List<ColorOpeningStat> openings
) {
}
