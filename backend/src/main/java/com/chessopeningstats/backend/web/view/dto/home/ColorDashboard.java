package com.chessopeningstats.backend.web.view.dto.home;

import java.util.List;

public record ColorDashboard(
        ColorRecord record,
        List<ColorOpeningStat> mostPlayedOpenings,
        List<ColorOpeningStat> highestWinRateOpenings,
        List<ColorOpeningStat> openings
) {
}
